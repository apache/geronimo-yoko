package test.rmi.exceptionhandling;

import org.omg.CORBA.LocalObject;
import org.omg.PortableInterceptor.ForwardRequest;
import org.omg.PortableInterceptor.ORBInitInfo;
import org.omg.PortableInterceptor.ORBInitializer;
import org.omg.PortableInterceptor.ServerRequestInfo;
import org.omg.PortableInterceptor.ServerRequestInterceptor;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;

public class MyServerRequestInterceptor extends LocalObject implements ServerRequestInterceptor, ORBInitializer {

    @Override
    public void receive_request(ServerRequestInfo arg0) throws ForwardRequest {
        System.out.printf("%08x: ", Thread.currentThread().getId());
        System.out.println("receive_request(" + arg0.operation() + ")");
    }

    @Override
    public void receive_request_service_contexts(ServerRequestInfo arg0) throws ForwardRequest {
        System.out.printf("%08x: ", Thread.currentThread().getId());
        System.out.println("receive_request_service_contexts(" + arg0.operation() + ")");
    }

    @Override
    public void send_exception(ServerRequestInfo arg0) throws ForwardRequest {
        System.out.printf("%08x: ", Thread.currentThread().getId());
        System.out.println("send_exception(" + arg0.operation() + ")");
    }

    @Override
    public void send_other(ServerRequestInfo arg0) throws ForwardRequest {
        System.out.printf("%08x: ", Thread.currentThread().getId());
        System.out.println("send_other(" + arg0.operation() + ")");
    }

    @Override
    public void send_reply(ServerRequestInfo arg0) {
        System.out.printf("%08x: ", Thread.currentThread().getId());
        System.out.println("send_reply(" + arg0.operation() + ")");
    }

    @Override
    public void destroy() {
    }

    @Override
    public String name() {
        return this.getClass().getName();
    }

    @Override
    public void post_init(ORBInitInfo arg0) {
        try {
            arg0.add_server_request_interceptor(this);
        } catch (DuplicateName e) {
            throw new Error(e);
        }
    }

    @Override
    public void pre_init(ORBInitInfo arg0) {
    }
}
