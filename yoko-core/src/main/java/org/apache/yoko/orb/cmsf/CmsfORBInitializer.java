package org.apache.yoko.orb.cmsf;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.LocalObject;
import org.omg.PortableInterceptor.ORBInitInfo;
import org.omg.PortableInterceptor.ORBInitializer;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;

public class CmsfORBInitializer extends LocalObject implements ORBInitializer {
    private static final String NAME = CmsfORBInitializer.class.getName();
    
    @Override
    public void pre_init(ORBInitInfo info) {
    }

    @Override
    public void post_init(ORBInitInfo info) {
        final int cmsfSlotId = info.allocate_slot_id();
        try {
            info.add_ior_interceptor(new CmsfIORInterceptor());
            info.add_client_request_interceptor(new CmsfClientInterceptor());
            info.add_server_request_interceptor(new CmsfServerInterceptor(cmsfSlotId));
        } catch (DuplicateName e) {
            throw (INITIALIZE)(new INITIALIZE()).initCause(e);
        }
    }
    
    private void readObject(ObjectInputStream ios) throws IOException {
        throw new NotSerializableException(NAME);
    }
    
    private void writeObject(ObjectOutputStream oos) throws IOException {
        throw new NotSerializableException(NAME);
    }
}