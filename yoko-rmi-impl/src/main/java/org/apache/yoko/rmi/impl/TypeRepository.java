/**
*
* Licensed to the Apache Software Foundation (ASF) under one or more
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

package org.apache.yoko.rmi.impl;

import java.io.Externalizable;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.rmi.CORBA.ClassDesc;

import org.apache.yoko.rmi.util.ByteBuffer;
import org.apache.yoko.rmi.util.ByteString;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ValueDefPackage.FullValueDescription;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.SendingContext.CodeBase;
import org.omg.SendingContext.CodeBaseHelper;
import org.omg.SendingContext.RunTime;


public class TypeRepository {
    static final Logger logger = Logger.getLogger(TypeRepository.class
            .getName());

    org.omg.CORBA.ORB orb;

    private static final class RepIdWeakMap {
    	private final Map<String, WeakReference<TypeDescriptor>> map = 
    			Collections.synchronizedMap(new WeakHashMap<String,WeakReference<TypeDescriptor>>());
    	
    	void put(String repId, TypeDescriptor desc) {
    		map.put(repId, new WeakReference<TypeDescriptor>(desc));
    	}
    	
    	TypeDescriptor get(String repId) {
    		WeakReference<TypeDescriptor> value = map.get(repId);
    		return (value == null) ? null : value.get();
    	}
    }
    
    private static final class LocalDescriptors extends ClassValue<TypeDescriptor> {
        private static final class Raw extends ClassValue<TypeDescriptor> {
        	private static final List<Class<?>> staticAnyTypes = 
        			Collections.unmodifiableList(
        					Arrays.asList(Object.class, Externalizable.class, Serializable.class, Remote.class));
        	
        	private final TypeRepository repo;
        	
        	Raw(TypeRepository repo) {
        		this.repo = repo;
        	}

    		@Override
    		protected TypeDescriptor computeValue(Class<?> type) {
    			if (type.isPrimitive()) {
    				return primitiveDescriptor(type);
    			} else if (type == String.class) {
    				return new StringDescriptor(repo);
    			} else if (type == Class.class) {
    				return new ClassDescriptor(repo);
    			} else if (type == javax.rmi.CORBA.ClassDesc.class) {
    				return this.get(Class.class);
    			} else if (type == java.util.Date.class) {
    				return new DateValueDescriptor(repo);
    			} else if (staticAnyTypes.contains(type)) {
    				return new AnyDescriptor(type, repo);
    			} else if (org.omg.CORBA.portable.IDLEntity.class.isAssignableFrom(type)
    	                && isIDLEntity(type)) {
    	            return new IDLEntityDescriptor(type, repo);
    	        } else if (java.lang.Throwable.class.isAssignableFrom(type)) {
    	            return new ExceptionDescriptor(type, repo);
    	        } else if (type.isArray()) {
    	            return ArrayDescriptor.get(type, repo);
    	        } else if (!type.isInterface()
    	                && java.io.Serializable.class.isAssignableFrom(type)) {
    	            return new ValueDescriptor(type, repo);
    	        } else if (java.rmi.Remote.class.isAssignableFrom(type)) {
    	            if (type.isInterface()) {
    	                return new RemoteInterfaceDescriptor(type, repo);
    	            } else {
    	                return new RemoteClassDescriptor(type, repo);
    	            }
    	        } else if (Object.class.isAssignableFrom(type)) {
    	            if (isAbstractInterface(type)) {
    	                logger.finer("encoding " + type + " as abstract interface");
    	                return new AbstractObjectDescriptor(type, repo);
    	            } else {
    	                logger.finer("encoding " + type + " as a abstract value");
    	                return new ValueDescriptor(type, repo);
    	            }
    	        } else {
    	            throw new RuntimeException("cannot handle class " + type.getName());
    	        }
    		}

    		private TypeDescriptor primitiveDescriptor(Class<?> type) {
    	        if (type == Boolean.TYPE) {
    	            return new BooleanDescriptor(repo);
    	        } else if (type == Byte.TYPE) {
    	            return new ByteDescriptor(repo);
    	        } else if (type == Short.TYPE) {
    	            return new ShortDescriptor(repo);
    	        } else if (type == Character.TYPE) {
    	            return new CharDescriptor(repo);
    	        } else if (type == Integer.TYPE) {
    	            return new IntegerDescriptor(repo);
    	        } else if (type == Long.TYPE) {
    	            return new LongDescriptor(repo);
    	        } else if (type == Float.TYPE) {
    	            return new FloatDescriptor(repo);
    	        } else if (type == Double.TYPE) {
    	            return new DoubleDescriptor(repo);
    	        } else if (type == Void.TYPE) {
    	            return new VoidDescriptor(repo);
    	        } else {
    	            throw new RuntimeException("internal error: " + type);
    	        }
    		}

    	    private static boolean isIDLEntity(Class<?> type) {
    	        Class<?>[] supers = type.getInterfaces();

    	        for (int i = 0; supers != null && i < supers.length; i++) {
    	            if (supers[i] == IDLEntity.class) {
    	                return true;
    	            }
    	        }

    	        return false;
    	    }
        	
    	    private static boolean isAbstractInterface(Class<?> type) {
    	        if (!type.isInterface())
    	            return false;

    	        Class<?>[] interfaces = type.getInterfaces();
    	        for (Class<?> anInterface : interfaces) {
    	            if (!isAbstractInterface(anInterface))
    	                return false;
    	        }

    	        java.lang.reflect.Method[] methods = type.getDeclaredMethods();
    	        for (Method method : methods) {
    	            if (!isRemoteMethod(method))
    	                return false;
    	        }

    	        return true;
    	    }

    	    private static boolean isRemoteMethod(java.lang.reflect.Method m) {
    	        Class<?>[] ex = m.getExceptionTypes();

    	        for (Class<?> anEx : ex) {
    	            if (anEx.isAssignableFrom(RemoteException.class))
    	                return true;
    	        }

    	        return false;
    	    }

        }

    	private final Raw rawValues;
    	private final RepIdWeakMap repIdDescriptors;
    	
    	LocalDescriptors(TypeRepository repo, RepIdWeakMap repIdDescriptors) {
    		rawValues = new Raw(repo);
    		this.repIdDescriptors = repIdDescriptors;
    	}
		@Override
		protected TypeDescriptor computeValue(Class<?> type) {
			final TypeDescriptor desc = rawValues.get(type);
			desc.init();
			repIdDescriptors.put(desc.getRepositoryID(), desc);
			return desc;
		}
    	
    }

    private static final class FvdRepIdDescriptorMaps extends ClassValue<ConcurrentMap<String,ValueDescriptor>> {

		@Override
		protected ConcurrentMap<String,ValueDescriptor> computeValue(
				Class<?> type) {
			return new ConcurrentHashMap<String,ValueDescriptor>();
		}
    }
    
    private final RepIdWeakMap repIdDescriptors;
    private final LocalDescriptors localDescriptors;
    private final FvdRepIdDescriptorMaps fvdDescMaps = new FvdRepIdDescriptorMaps();
    private final ConcurrentMap<String,ValueDescriptor> noTypeDescMap = new ConcurrentHashMap<String,ValueDescriptor>();
    
    public TypeRepository(org.omg.CORBA.ORB orb) {
        this.orb = orb;
        repIdDescriptors = new RepIdWeakMap();
        localDescriptors = new LocalDescriptors(this, repIdDescriptors);
        
        Class<?>[] initTypes = {
        		Object.class, String.class, ClassDesc.class, Date.class, 
        		Externalizable.class, Serializable.class, Remote.class };

        for (Class<?> type: initTypes) {
        	localDescriptors.get(type);
        }
    }

    org.omg.CORBA.ORB getORB() {
        return orb;
    }

    public String getRepositoryID(Class<?> type) {
        return getDescriptor(type).getRepositoryID();
    }

    public RemoteInterfaceDescriptor getRemoteDescriptor(Class<?> type) {
        TypeDescriptor td = getDescriptor(type);
        RemoteInterfaceDescriptor result = td.getRemoteInterface();

        if (result != null) {
            return result;
        }

        RemoteDescriptor desc;

        if (java.rmi.Remote.class.isAssignableFrom(type)) {
            if (type.isInterface()) {
                desc = new RemoteInterfaceDescriptor(type, this);
            } else {
                desc = new RemoteClassDescriptor(type, this);
            }

            desc.init();
        } else {
            throw new IllegalArgumentException("class " + type.toString()
                    + " does not implement" + " java.rmi.Remote");
        }

        result = desc.getRemoteInterface();
        td.setRemoteInterface(result);

        return result;
    }

    public TypeDescriptor getDescriptor(Class<?> type) {
        logger.fine("Requesting type descriptor for class " + type.getName());
        final TypeDescriptor desc = localDescriptors.get(type);
        logger.fine("Class " + type.getName() + " resolves to " + desc.getClass().getName());
        return desc;
    }

    /**
     * @param clz (local) class we are interested in
     * @param repid  repository id from GIOP input for the remote class
     * @param runtime way to look up the complete remote descriptor
     * @return ValueDescriptor
     * @throws ClassNotFoundException  something might go wrong.
     */
    public ValueDescriptor getDescriptor(Class<?> clz, String repid,
            RunTime runtime) throws ClassNotFoundException {
        if (repid == null) {
            return (ValueDescriptor) getDescriptor(clz);
        }

        ValueDescriptor clzdesc = (ValueDescriptor) repIdDescriptors.get(repid);
        if (clzdesc != null) {
            return clzdesc;
        }

        if (clz != null) {
            logger.fine("Requesting type descriptor for class " + clz.getName() + " with repid " + repid); 
            // special handling for array value types.
            if (clz.isArray()) {
                //TODO don't we need to look up the FVD for the array element?
            	return (ValueDescriptor) localDescriptors.get(clz);
            }
            clzdesc = (ValueDescriptor) getDescriptor(clz);
            String localID = clzdesc.getRepositoryID();

            if (repid.equals(localID)) {
                return clzdesc;
            }
            //One might think that java serialization compatibility (same SerialVersionUID) would mean corba
            //serialization compatibility.  However, one implementation might have a writeObject method and the
            //other implementation not.  This is recorded only in the isCustomMarshall of the source value
            //descriptor, so we have to fetch it to find out.  A custom marshall value has a couple extra bytes
            // and padding and these can't be reliably identified without this remote info.  cf YOKO-434.
        }

        logger.fine("Requesting type descriptor for repid " + repid); 
        CodeBase codebase = CodeBaseHelper.narrow(runtime);
        if (codebase == null) {
            throw new MARSHAL("cannot locate RunTime CodeBase");
        }

        FullValueDescription fvd = codebase.meta(repid);

        ValueDescriptor super_desc = null;
        if (!"".equals(fvd.base_value)) {
            super_desc = getDescriptor(clz == null? null: clz.getSuperclass(), fvd.base_value,
                    codebase);
        }

        ValueDescriptor newDesc = new FVDValueDescriptor(fvd, clz, this, repid, super_desc);
        ConcurrentMap<String, ValueDescriptor> remoteDescMap = (clz == null) ? noTypeDescMap : fvdDescMaps.get(clz);
        clzdesc = remoteDescMap.putIfAbsent(repid, newDesc);
        if (clzdesc == null) {
        	clzdesc = newDesc;
        	repIdDescriptors.put(repid, clzdesc);
        }

        return clzdesc;
    }

    public static String idToClass(String repid) {
        // debug
        logger.finer("idToClass " + repid);

        if (repid.startsWith("IDL:")) {

            ByteString id = new ByteString(repid);

            try {
                int end = id.lastIndexOf(':');
                ByteString s = end < 0 ? id.substring(4) : id.substring(4, end);

                ByteBuffer bb = new ByteBuffer();

                //
                // reverse order of dot-separated name components up
                // till the first slash.
                //
                int firstSlash = s.indexOf('/');
                if (firstSlash > 0) {
                    ByteString prefix = s.substring(0, firstSlash);
                    ByteString[] elems = prefix.split('.');

                    for (int i = elems.length - 1; i >= 0; i--) {
                        bb.append(fixName(elems[i]));
                        bb.append('.');
                    }

                    s = s.substring(firstSlash + 1);
                }

                //
                // Append slash-separated name components ...
                //
                ByteString[] elems = s.split('/');
                for (int i = 0; i < elems.length; i++) {
                    bb.append(fixName(elems[i]));
                    if (i != elems.length - 1)
                        bb.append('.');
                }

                String result = bb.toString();

                logger.finer("idToClassName " + repid + " => " + result);

                return result;
            } catch (IndexOutOfBoundsException ex) {
                logger.log(Level.FINE, "idToClass " + ex.getMessage(), ex);
                return null;
            }

        } else if (repid.startsWith("RMI:")) {
            int end = repid.indexOf(':', 4);
            return end < 0 ? repid.substring(4) : repid.substring(4, end);
        }

        return null;
    }

    static String fixName(String name) {
        return (new ByteString(name)).toString();
    }

    static ByteString fixName(ByteString name) {
        if (keyWords.contains(name)) {
            ByteBuffer buf = new ByteBuffer();
            buf.append('_');
            buf.append(name);
            return buf.toByteString();
        }

        ByteString result = name;
        ByteString current = name;

        boolean match = true;
        while (match) {

            int len = current.length();
            match = false;

            for (ByteString reservedPostfixe : reservedPostfixes) {
                if (current.endsWith(reservedPostfixe)) {
                    ByteBuffer buf = new ByteBuffer();
                    buf.append('_');
                    buf.append(result);
                    result = buf.toByteString();

                    int resultLen = reservedPostfixe.length();
                    if (len > resultLen)
                        current = current.substring(0, len - resultLen);
                    else
                        current = new ByteString("");

                    match = true;
                    break;
                }
            }

        }

        return name;
    }

    static final java.util.Set<ByteString> keyWords = new java.util.HashSet<ByteString>();

    static final ByteString[] reservedPostfixes = new ByteString[] {
            new ByteString("Helper"), new ByteString("Holder"),
            new ByteString("Operations"), new ByteString("POA"),
            new ByteString("POATie"), new ByteString("Package"),
            new ByteString("ValueFactory") };

    static {
        String[] words = { "abstract", "boolean", "break", "byte", "case",
                "catch", "char", "class", "clone", "const", "continue",
                "default", "do", "double", "else", "equals", "extends",
                "false", "final", "finalize", "finally", "float", "for",
                "getClass", "goto", "hashCode", "if", "implements", "import",
                "instanceof", "int", "interface", "long", "native", "new",
                "notify", "notifyAll", "null", "package", "private",
                "protected", "public", "return", "short", "static", "super",
                "switch", "synchronized", "this", "throw", "throws",
                "toString", "transient", "true", "try", "void", "volatile",
                "wait", "while" };

        for (String word : words) {
            keyWords.add(new ByteString(word));
        }
    }

}
