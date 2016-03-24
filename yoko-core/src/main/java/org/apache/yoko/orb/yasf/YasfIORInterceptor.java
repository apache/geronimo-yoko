package org.apache.yoko.orb.yasf;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.yoko.util.yasf.Yasf;
import org.omg.CORBA.LocalObject;
import org.omg.PortableInterceptor.IORInfo;
import org.omg.PortableInterceptor.IORInterceptor;

public class YasfIORInterceptor extends LocalObject implements IORInterceptor {
    private static final String NAME = YasfIORInterceptor.class.getName();

    @Override
    public void establish_components(IORInfo info) {
        YasfHelper.addTc(info, Yasf.supported());
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
