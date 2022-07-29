package org.apache.yoko.orb.yasf;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.yoko.util.yasf.Yasf;
import org.apache.yoko.util.yasf.YasfThreadLocal;
import org.omg.CORBA.LocalObject;
import org.omg.PortableInterceptor.ClientRequestInfo;
import org.omg.PortableInterceptor.ClientRequestInterceptor;
import org.omg.PortableInterceptor.ForwardRequest;

public class YasfClientInterceptor extends LocalObject implements ClientRequestInterceptor {
    private static final String NAME = YasfClientInterceptor.class.getName();

    @Override
    public void send_request(ClientRequestInfo ri) throws ForwardRequest {
        byte[] yasfData = YasfHelper.readData(ri);

        YasfThreadLocal.push(Yasf.toSet(yasfData));

        YasfHelper.addSc(ri);
    }

    @Override
    public void send_poll(ClientRequestInfo ri) {
    }

    @Override
    public void receive_reply(ClientRequestInfo ri) {
        YasfThreadLocal.pop();
    }

    @Override
    public void receive_exception(ClientRequestInfo ri) throws ForwardRequest {
        YasfThreadLocal.pop();
    }

    @Override
    public void receive_other(ClientRequestInfo ri) throws ForwardRequest {
        YasfThreadLocal.pop();
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
