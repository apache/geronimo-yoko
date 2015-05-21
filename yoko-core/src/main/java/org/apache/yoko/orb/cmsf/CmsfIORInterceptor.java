package org.apache.yoko.orb.cmsf;

import static org.apache.yoko.orb.cmsf.CmsfVersion.CMSFv2;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.omg.CORBA.LocalObject;
import org.omg.PortableInterceptor.IORInfo;
import org.omg.PortableInterceptor.IORInterceptor;

public final class CmsfIORInterceptor extends LocalObject implements IORInterceptor {
    private static final String NAME = CmsfIORInterceptor.class.getName();

    @Override
    public void establish_components(IORInfo info) {
        if (CmsfVersion.ENABLED) info.add_ior_component(CMSFv2.getTc());
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void destroy() {
    }
    
    private void readObject(ObjectInputStream ios) throws IOException {
        throw new NotSerializableException(NAME);
    }
    
    private void writeObject(ObjectOutputStream oos) throws IOException {
        throw new NotSerializableException(NAME);
    }
}
