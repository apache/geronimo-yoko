/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.yoko.orb.OB;

import org.apache.yoko.orb.CORBA.DataOutputStream;
import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.io.WriteBuffer;
import org.apache.yoko.osgi.ProviderLocator;
import org.apache.yoko.util.Assert;
import org.apache.yoko.util.cmsf.RepIds;
import org.omg.CORBA.CustomMarshal;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.StringValueHelper;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.WStringValueHelper;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.CORBA.portable.StreamableValue;
import org.omg.CORBA.portable.ValueBase;

import javax.rmi.CORBA.ValueHandler;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.IdentityHashMap;

import static org.apache.yoko.util.MinorCodes.MinorNoValueFactory;
import static org.apache.yoko.util.MinorCodes.describeMarshal;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;
import static org.omg.CORBA.TCKind._tk_string;

final public class ValueWriter {

    /** The OutputStream */
    private final OutputStream out_;

    /** The Buffer */
    private final WriteBuffer writeBuffer;

    /** Are we currently writing chunks? */
    private boolean chunked_ = false;

    /** The nesting level of chunked valuetypes */
    private int nestingLevel_ = 0;

    /**
     * True if we need to start a new chunk before the next write to the
     * output stream
     */
    private boolean needChunk_;

    /** The position in the buffer at which we need to patch the chunk size */
    private int chunkSizePos_;

    /** The position in the buffer of the last end tag */
    private int lastEndTagPos_;

    /** Keep track of the last end tag we've written (Java only) */
    private int lastTag_ = 0;

    /** Record valuetype positions in stream */
    private final IdentityHashMap<Serializable, Integer> instanceTable_;

    /** Record repository ID positions in stream */
    private final Hashtable<String, Integer> idTable_;

    /** Record repository ID list positions in stream */
    private final Hashtable<StringSeqHasher, Integer> idListTable_;

    private final Hashtable<String, Integer> codebaseTable_;

    private ValueHandler valueHandler;

    /** Helper class for using a String array as the key in a Hashtable */
    private final class StringSeqHasher {
        final String[] seq_;

        final int hashCode_;

        StringSeqHasher(String[] seq) {
            seq_ = seq;
            hashCode_ = Arrays.hashCode(seq);
        }

        public int hashCode() {
            return hashCode_;
        }

        public boolean equals(Object o) {
            if (!!! (o instanceof StringSeqHasher))
                return false;

            StringSeqHasher h = (StringSeqHasher) o;

            return Arrays.equals(this.seq_, h.seq_);
        }
    }

    private boolean checkIndirection(Serializable value) {
        Integer pos = instanceTable_.get(value);
        if (pos != null) {
            //
            // Write indirection
            //
            out_.write_long(-1);
            int p = pos;
            // Align p on a four-byte boundary
            p += 3;
            p -= p & 0x3;
            int off = p - writeBuffer.getPosition();
            out_.write_long(off);
            return true;
        }

        return false;
    }

    private void beginChunk() {
        Assert.ensure(chunked_);

        // Write a placeholder for the chunk size
        out_.write_long(0);

        // Remember the position of the placeholder
        chunkSizePos_ = writeBuffer.getPosition() - 4;
    }

    private void endChunk() {
        Assert.ensure(chunked_);

        // If chunkSizePos_ > 0, then there is a chunk whose size needs to be updated
        if (chunkSizePos_ > 0) {
            // Compute size of chunk
            int size = writeBuffer.getPosition() - (chunkSizePos_ + 4);

            // Update chunk size
            int savePos = writeBuffer.getPosition();
            writeBuffer.setPosition(chunkSizePos_);
            out_.write_long(size);
            writeBuffer.setPosition(savePos);

            chunkSizePos_ = 0;
        }
    }

    private BoxedValueHelper getHelper(Serializable value, TypeCode type) {
        BoxedValueHelper result = null;

        Class<?> helperClass = null;
        final Class<?> valueClass = value.getClass();

        //Short-cuts
        if (String.class == valueClass) return fastPathStringBoxHelper(type);
        if (!!!IDLEntity.class.isAssignableFrom(valueClass)) return null;

        // First try constructing a class name based on the class of
        // the value. This will only work for primitive types, because
        // a distinct valuetype class is generated for boxed primitive
        // types. For constructed types, this will obtain the Helper of
        // the boxed type, which is not a BoxedValueHelper.
        //
        try {
            String name = value.getClass().getName() + "Helper";
            // get the appropriate class for the loading.
            Class<?> c = ProviderLocator.loadClass(name, value.getClass(), Thread.currentThread().getContextClassLoader());
            if (BoxedValueHelper.class.isAssignableFrom(c)) helperClass = c;
        } catch (ClassNotFoundException ignored) {
        }

        //
        // If we have a TypeCode, then use the TypeCode's repository ID
        // to locate a Helper class
        //
        if (helperClass == null && type != null) {
            try {
                TypeCode origType = org.apache.yoko.orb.CORBA.TypeCode._OB_getOrigType(type);
                String id = origType.id();
                helperClass = RepIds.query(id).suffix("Helper").toClass();
            } catch (BadKind ex) {
                throw Assert.fail(ex);
            }
        }

        //
        // Instantiate the Helper
        //
        if (helperClass != null) {
            try {
                result = (BoxedValueHelper)helperClass.newInstance();
            } catch (ClassCastException | IllegalAccessException | InstantiationException ignored) {
            }
        }

        return result;
    }

    private BoxedValueHelper fastPathStringBoxHelper(TypeCode type) {
        // There are two ways we could have a string value:
        // EITHER the boxed value is an IDL string
        // OR the boxed value is a Java String.
        // We can tell if it is an IDL String if the kind == _tk_string
        try {
            return (type.content_type().kind().value() == _tk_string) ? new StringValueHelper() : new WStringValueHelper();
        } catch (NullPointerException|BadKind ignored) {
            return new WStringValueHelper();
        }

    }

    // ------------------------------------------------------------------
    // Public methods
    // ------------------------------------------------------------------

    public ValueWriter(OutputStream out, WriteBuffer writeBuffer) {
        this.out_ = out;
        this.writeBuffer = writeBuffer;
        this.chunked_ = false;
        this.nestingLevel_ = 0;
        this.needChunk_ = false;
        this.chunkSizePos_ = 0;
        this.lastEndTagPos_ = 0;
        this.instanceTable_ = new IdentityHashMap<>(131);
        this.idTable_ = new Hashtable<>(131);
        this.idListTable_ = new Hashtable<>(131);
        this.codebaseTable_ = new Hashtable<>(3);
    }

    public void writeValue(Serializable value, String id) {
        if (value == null)
            out_.write_long(0);
        else if (!checkIndirection(value)) {
            boolean isStreamable = (value instanceof StreamableValue);
            boolean isCustom = (value instanceof CustomMarshal);

            //
            // if the repositoryId begins with "RMI:" we force the
            // transmission of this repositoryId in order to
            // interoperate with servers such as WebSphere which don't
            // recognize the 0x7fffff00 value properly
            //
            boolean rmiCompatMode = false;
            if (id != null) {
                String upperId = id.toUpperCase();
                rmiCompatMode = upperId.startsWith("RMI:");
            }

            //
            // writeValue may be called for a valuebox when the type
            // in use is CORBA::ValueBase. We know we've been given a
            // valuebox when the value does not implement StreamableValue
            // or CustomMarshal.
            //
            if (!isStreamable && !isCustom) {
                BoxedValueHelper helper = getHelper (value, null);
                if (helper == null) {
                    writeRMIValue (value);
                } else {
                    writeValueBox (value, null, helper);
                }
                return;
            }

            ValueBase vb = (ValueBase) value;
            String[] truncIds = vb._truncatable_ids();
            String valueId = truncIds[0];
            boolean isTruncatable = (truncIds.length > 1);

            //
            // Determine if the actual type matches the formal type
            //
            boolean typeMatch = (id != null && id.equals(valueId));

            //
            // Chunking is necessary when:
            //
            // * value is custom
            // * value is truncatable and actual type does not match the
            // formal type
            //
            boolean chunked = false;
            if (isCustom || (isTruncatable && !typeMatch))
                chunked = true;

            //
            // Value header
            //
            // Determine whether we need to transmit repository ID information
            // for this value. This is necessary when:
            //
            // * The formal type wasn't supplied or doesn't match the actual
            // type
            // * This is a nested value and it is truncatable
            //

            int tag;
            String[] ids;

            if (rmiCompatMode || !typeMatch
                    || (nestingLevel_ > 1 && isTruncatable)) {
                if (isTruncatable) {
                    //
                    // Value is truncatable, so write the tag and then
                    // a sequence of strings representing the repository
                    // IDs of the truncatable base types.
                    //
                    // The tag is 0x7fffff00 + 0x06 (partial type information)
                    //
                    tag = 0x7fffff06;

                    //
                    // TODO: We could optimize the list of identifiers by
                    // analyzing the receiving context (see GIOP spec)
                    //

                    ids = truncIds;
                } else {
                    //
                    // No formal ID was provided, or the formal ID doesn't
                    // match the actual ID, so we need to send a single
                    // repository ID.
                    //
                    // The tag is 0x7fffff00 + 0x02 (single ID)
                    //

                    tag = 0x7fffff02;

                    ids = new String[1];
                    ids[0] = valueId;
                }
            } else {
                //
                // The value's dynamic type matches the type of the
                // position being marshalled, so we just need to
                // marshal the tag (no repository ID).
                //
                // The tag is 0x7fffff00
                //

                tag = 0x7fffff00;
                ids = new String[0];
            }

            int startPos = beginValue(tag, ids, null, chunked);
            instanceTable_.put(value, startPos);

            //
            // Marshal the value data
            //
            if (isStreamable) {
                ((StreamableValue) value)._write(out_);
            } else // must be custom
            {
                org.omg.CORBA.DataOutputStream out = new DataOutputStream(
                        out_);
                ((CustomMarshal) value).marshal(out);
            }

            endValue();
        }
    }

    private void writeRMIValue(Serializable value) {

        // check if it is the null object
        if (value == null) {
            out_.write_long(0);
            return;
        }

        // check if this value has already been written
        if (checkIndirection(value)) {
            return;
        }

        // special-case string
        if (value instanceof String) {
            //out_._OB_align(4);
            int pos = writeBuffer.getPosition();
            WStringValueHelper.write (out_, (String)value);
            instanceTable_.put (value, pos);
            return;
        }

        // get hold of the value handler
        if (valueHandler == null) {
            valueHandler = javax.rmi.CORBA.Util.createValueHandler ();
        }

        //
        // Needs writeReplace?
        //
        Serializable repValue
                = valueHandler.writeReplace (value);

        Serializable originalValue = null;

        //
        // Repeat base checks if value was replaced
        //
        if (value != repValue)
        {
            if (repValue == null) {
                out_.write_long(0);
                return;
            }

            if (checkIndirection(repValue)) {
                return;
            }

            if (repValue instanceof String) {
                int pos = writeBuffer.getPosition();
                WStringValueHelper.write (out_, (String)repValue);
                instanceTable_.put (repValue, pos);
                // we record the original value position so that another attempt to write out 
                // the original object will resolve to the same object. 
                instanceTable_.put (value, pos);
                return;
            }
            // save the original value because we want to record that object in the 
            // indirection table also, once we've established the offset position. 
            originalValue = value;
            value = repValue;

        }

        //
        // Get the class object for the value
        //
        Class clz = value.getClass ();

        //
        // 0x7fffff00 + SINGLE_ID
        //
        int tag = 0x7fffff02;

        String codebase = javax.rmi.CORBA.Util.getCodebase (clz);
        if (codebase != null && codebase.length () != 0)
            tag |= 1;

        //
        // Determine the repository ID
        //
        String[] ids = new String[1];
        ids[0] = valueHandler.getRMIRepositoryID (clz);

        //
        // Determine if chunked encoding is needed.
        //
        boolean isChunked = valueHandler.isCustomMarshaled(clz);

        int pos = beginValue (tag, ids, codebase, isChunked);
        instanceTable_.put (value, pos);
        // if this was replace via writeReplace, record the original 
        // value in the indirection table too. 
        if (originalValue != null) {
            instanceTable_.put (originalValue, pos);
        }
        valueHandler.writeValue (out_, value);
        endValue ();
    }

    public void writeValueBox(Serializable value,
                              TypeCode type,
                              BoxedValueHelper helper) {
        if (value == null)
            out_.write_long(0);
        else if (!checkIndirection(value)) {
            //
            // Try to get Helper if one wasn't provided
            //
            if (helper == null)
                helper = getHelper(value, type);

            //
            // Raise MARSHAL if no Helper was found
            //
            if (helper == null)
                throw new MARSHAL(describeMarshal(MinorNoValueFactory) + ": no helper for valuebox", MinorNoValueFactory, COMPLETED_NO);

            //
            // Setup header
            //
            String[] ids = new String[1];
            ids[0] = helper.get_id();
            int tag = 0x7fffff02;

            //
            // Marshal value
            //
            int startPos = beginValue(tag, ids, null, false);
            instanceTable_.put(value, startPos);
            helper.write_value(out_, value);
            endValue();
        }
    }

    public void writeAbstractInterface(Object obj) {
        if (obj != null) {
            if (obj instanceof org.omg.CORBA.Object) {
                out_.write_boolean(true); // discriminator for objref
                out_.write_Object((org.omg.CORBA.Object) obj);
            } else if (obj instanceof Serializable) {
                out_.write_boolean(false); // discriminator for valuetype
                writeValue((Serializable) obj, null);
            } else
                throw new MARSHAL("Object of class " + obj.getClass().getName()
                        + " is not an object reference or valuetype");
        } else {
            //
            // A nil abstract interface is marshalled as a null valuetype
            //
            out_.write_boolean(false); // discriminator for valuetype
            out_.write_long(0);
        }
    }

    public int
    beginValue(int tag, String[] ids, String codebase, boolean chunk) {
        if(chunk)
            chunked_ = true;

        if (chunked_) {
            tag |= 0x08;
            nestingLevel_++;
            needChunk_ = false;

            //
            // If nestingLevel_ > 1, then we are about to marshal a chunked,
            // nested valuetype. We must end the previous chunk first.
            //
            if (nestingLevel_ > 1)
                endChunk();
        }

        //
        // Write value header
        //

        out_.write_long(tag);
        int startPos = writeBuffer.getPosition() - 4; // start of value

        // write codebase if present
        if ((tag & 0x00000001) == 1) {

            // check for indirection of codebase
            Integer pos = codebaseTable_.get(codebase);
            if (pos != null) {
                out_.write_long(-1);
                int off = pos - writeBuffer.getPosition();
                out_.write_long(off);
            } else {
                codebaseTable_.put(codebase, writeBuffer.getPosition());
                out_.write_string(codebase);
            }
        }

        if ((tag & 0x00000006) == 6) {
            //
            // Check for possible indirection of repository IDs
            //
            StringSeqHasher key = new StringSeqHasher(ids);
            Integer pos = idListTable_.get(key);
            if (pos != null) {
                //
                // Write indirection
                //
                out_.write_long(-1);
                int off = pos - writeBuffer.getPosition();
                out_.write_long(off);
            } else {
                idListTable_.put(key, writeBuffer.getPosition());
                out_.write_long(ids.length);
                for (String id : ids) {
                    //
                    // Add this ID to the history list, if necessary
                    //
                    if (!idTable_.containsKey(id))
                        idTable_.put(id, writeBuffer.getPosition());
                    out_.write_string(id);
                }
            }
        } else if ((tag & 0x00000006) == 2) {
            Assert.ensure(ids.length == 1);

            //
            // Check to see if we've already marshalled this repository ID,
            // and if so, we write an indirection marker
            //
            Integer pos = idTable_.get(ids[0]);
            if (pos != null) {
                //
                // Write indirection
                //
                out_.write_long(-1);
                int off = pos - writeBuffer.getPosition();
                out_.write_long(off);
            } else {
                //
                // Remember ID in history at current position
                //
                idTable_.put(ids[0], writeBuffer.getPosition());
                out_.write_string(ids[0]);
            }
        }

        if (chunked_) {
            needChunk_ = true;
            chunkSizePos_ = 0;
            lastEndTagPos_ = 0;
        }

        return startPos;
    }

    public void endValue() {
        if (chunked_) {
            needChunk_ = false;

            //
            // If we haven't written anything since the last end tag, then
            // this value coterminates with a nested value, so we increment
            // the last end tag rather than write a new one
            //
            if (lastEndTagPos_ > 0 && writeBuffer.getPosition() == lastEndTagPos_ + 4) {
                //
                // Increment last end tag
                //
                lastTag_++;
                writeBuffer.setPosition(lastEndTagPos_); // same as "buf_.pos_ -= 4;"
                out_.write_long(lastTag_);
            } else {
                endChunk();

                //
                // Write the end tag and remember its position
                //
                lastTag_ = -nestingLevel_;
                out_.write_long(lastTag_);
                if (nestingLevel_ > 1)
                    lastEndTagPos_ = writeBuffer.getPosition() - 4;
            }

            //
            // We're finished chunking if nestingLevel_ == 1
            //
            if (nestingLevel_ == 1) {
                chunked_ = false;
                lastEndTagPos_ = 0;
                lastTag_ = 0;
            } else {
                //
                // We need to start a new chunk
                //
                needChunk_ = true;
            }

            Assert.ensure(chunkSizePos_ == 0);
            nestingLevel_--;
        }
    }

    public void checkBeginChunk() {
        if (needChunk_) {
            needChunk_ = false; // Do this before beginChunk() to
            // avoid recursion!
            beginChunk();
        }
    }
}
