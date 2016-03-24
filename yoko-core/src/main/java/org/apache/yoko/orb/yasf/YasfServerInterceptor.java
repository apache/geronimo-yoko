package org.apache.yoko.orb.yasf;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.yoko.util.yasf.Yasf;
import org.apache.yoko.util.yasf.YasfThreadLocal;
import org.omg.CORBA.LocalObject;
import org.omg.PortableInterceptor.ForwardRequest;
import org.omg.PortableInterceptor.ServerRequestInfo;
import org.omg.PortableInterceptor.ServerRequestInterceptor;

public class YasfServerInterceptor extends LocalObject implements ServerRequestInterceptor {
    private static final String NAME = YasfServerInterceptor.class.getName();

    private final int slotId;

    public YasfServerInterceptor(int slotId) {
        this.slotId = slotId;
    }

    @Override
    public void receive_request_service_contexts(ServerRequestInfo ri) throws ForwardRequest {
        YasfThreadLocal.reset();
        byte[] yasfData = YasfHelper.readData(ri);
        YasfHelper.setSlot(slotId, ri, yasfData);
    }

    @Override
    public void receive_request(ServerRequestInfo ri) throws ForwardRequest {
    }

    @Override
    public void send_reply(ServerRequestInfo ri) {
        YasfThreadLocal.push(Yasf.toSet(YasfHelper.getSlot(slotId, ri)));
        // Adding for diagnostic purposes
        YasfHelper.addSc(ri, Yasf.supported());
    }

    @Override
    public void send_exception(ServerRequestInfo ri) throws ForwardRequest {
        YasfThreadLocal.push(Yasf.toSet(YasfHelper.getSlot(slotId, ri)));
        // Adding for diagnostic purposes
        YasfHelper.addSc(ri, Yasf.supported());
    }

    @Override
    public void send_other(ServerRequestInfo ri) throws ForwardRequest {
        YasfThreadLocal.push(Yasf.toSet(YasfHelper.getSlot(slotId, ri)));
        // Adding for diagnostic purposes
        YasfHelper.addSc(ri, Yasf.supported());
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
