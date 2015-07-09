package org.apache.yoko.orb.yasf;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.omg.CORBA.LocalObject;
import org.omg.PortableInterceptor.ForwardRequest;
import org.omg.PortableInterceptor.ServerRequestInfo;
import org.omg.PortableInterceptor.ServerRequestInterceptor;

public class YasfServerInterceptor extends LocalObject implements ServerRequestInterceptor {
    private static final String NAME = YasfServerInterceptor.class.getName();

    @Override
    public void receive_request_service_contexts(ServerRequestInfo ri) throws ForwardRequest {
    }

    @Override
    public void receive_request(ServerRequestInfo ri) throws ForwardRequest {
    }

    @Override
    public void send_reply(ServerRequestInfo ri) {
        ri.add_reply_service_context(Yasf.build().sc(), false);
    }

    @Override
    public void send_exception(ServerRequestInfo ri) throws ForwardRequest {
        ri.add_reply_service_context(Yasf.build().sc(), false);
    }

    @Override
    public void send_other(ServerRequestInfo ri) throws ForwardRequest {
        ri.add_reply_service_context(Yasf.build().sc(), false);
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
