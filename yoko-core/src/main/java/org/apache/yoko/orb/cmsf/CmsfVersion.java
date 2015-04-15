package org.apache.yoko.orb.cmsf;

import java.io.IOException;

import org.apache.yoko.orb.OCI.Buffer;
import org.omg.CORBA.Any;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.IOP.RMICustomMaxStreamFormat;
import org.omg.IOP.ServiceContext;
import org.omg.IOP.TAG_RMI_CUSTOM_MAX_STREAM_FORMAT;
import org.omg.IOP.TaggedComponent;

public enum CmsfVersion {
    CMSFv1(1), CMSFv2(2);
    public static final boolean ENABLED = false;
    private final byte value;
    private final TaggedComponent tc;
    private final ServiceContext sc;
    private final Any any;
    
    private CmsfVersion(int value) {
        this((byte)(value & 0xff));
    }
    
    private CmsfVersion(byte value) {
        this.value = value;
        final byte[] data = genData(value);
        this.tc = new TaggedComponent(TAG_RMI_CUSTOM_MAX_STREAM_FORMAT.value, data.clone());
        this.sc = new ServiceContext(RMICustomMaxStreamFormat.value, data);
        this.any = ORB.init().create_any();
        this.any.insert_octet((byte)value);
    }
    
    byte getValue() {
        return value;
    }
    
    TaggedComponent getTc() {
        return tc;
    }
    
    ServiceContext getSc() {
        return sc;
    }
    
    Any getAny() {
        return any;
    }

    static CmsfVersion readAny(Any any) {
        if (any == null) return CMSFv1;
        return (any.extract_octet() >= 2) ? CMSFv2 : CMSFv1;
    }
    
    static CmsfVersion readData(byte[] data) {
        if (data == null) return CMSFv1;
        int cmsf = 1;
        Buffer buf = new Buffer(data, data.length);
        try (org.apache.yoko.orb.CORBA.InputStream in = 
                new org.apache.yoko.orb.CORBA.InputStream(buf, 0, false)) {
            in._OB_readEndian();
            cmsf = in.read_octet();
        } catch (Exception e) {
            throw (MARSHAL)(new MARSHAL(e.getMessage())).initCause(e);
        }
        return (cmsf >= 2) ? CMSFv2 : CMSFv1;
    }
    
    private static byte[] genData(byte value) {
        Buffer buf = new Buffer();
        try (org.apache.yoko.orb.CORBA.OutputStream out = 
                new org.apache.yoko.orb.CORBA.OutputStream(buf)) {
            out._OB_writeEndian();
            out.write_octet(value);
            return buf.data();
        } catch (IOException e) {
            throw (INTERNAL)(new INTERNAL(e.getMessage())).initCause(e);
        }
    }
}
