package org.apache.yoko.orb.cmsf;

import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.orb.OCI.Buffer;
import org.omg.CORBA.Any;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.IOP.RMICustomMaxStreamFormat;
import org.omg.IOP.ServiceContext;
import org.omg.IOP.TAG_RMI_CUSTOM_MAX_STREAM_FORMAT;
import org.omg.IOP.TaggedComponent;

import java.io.IOException;
import java.util.Arrays;

public enum CmsfVersion {
    CMSFv1(1), CMSFv2(2);
    public static final boolean ENABLED = true;
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
        Buffer buf = new Buffer(data);
        try (InputStream in = new InputStream(buf, false)) {
            in._OB_readEndian();
            cmsf = in.read_octet();
        } catch (Exception e) {
            throw (MARSHAL)(new MARSHAL(e.getMessage())).initCause(e);
        }
        return (cmsf >= 2) ? CMSFv2 : CMSFv1;
    }
    
    private static byte[] genData(byte value) {
        try (OutputStream out = new OutputStream(new Buffer(2))) {
            out._OB_writeEndian();
            out.write_octet(value);
            return out.copyWrittenBytes();
        }
    }
}
