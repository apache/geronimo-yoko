package org.apache.yoko.orb.cmsf;

import static org.apache.yoko.orb.cmsf.CmsfVersion.CMSFv1;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.yoko.rmi.cmsf.CmsfThreadLocalStack;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.LocalObject;
import org.omg.IOP.RMICustomMaxStreamFormat;
import org.omg.IOP.ServiceContext;
import org.omg.PortableInterceptor.ForwardRequest;
import org.omg.PortableInterceptor.InvalidSlot;
import org.omg.PortableInterceptor.ServerRequestInfo;
import org.omg.PortableInterceptor.ServerRequestInterceptor;

public class CmsfServerInterceptor extends LocalObject implements ServerRequestInterceptor {
    private static final String NAME = CmsfServerInterceptor.class.getName();

    private final int slotId;
    
    public CmsfServerInterceptor(int slotId) {
        this.slotId = slotId;
    }

    @Override
    public void receive_request_service_contexts(ServerRequestInfo ri) throws ForwardRequest {
        CmsfVersion cmsf = CMSFv1;
        try {
            ServiceContext sc = ri.get_request_service_context(RMICustomMaxStreamFormat.value);
            cmsf = CmsfVersion.readData(sc.context_data);
        } catch (BAD_PARAM e) {
            if (e.minor != 26) {
                throw e;
            }
        }
        try {
            ri.set_slot(slotId, cmsf.getAny());
        } catch (InvalidSlot e) {
        }
    }

    @Override
    public void receive_request(ServerRequestInfo ri) throws ForwardRequest {
        CmsfVersion cmsf = CMSFv1;
        try {
            cmsf = CmsfVersion.readAny(ri.get_slot(slotId));
        } catch (InvalidSlot e) {
        }
        CmsfThreadLocalStack.push(cmsf.getValue());
    }

    @Override
    public void send_reply(ServerRequestInfo ri) {
        CmsfThreadLocalStack.pop();
    }

    @Override
    public void send_exception(ServerRequestInfo ri) throws ForwardRequest {
        CmsfThreadLocalStack.pop();
    }

    @Override
    public void send_other(ServerRequestInfo ri) throws ForwardRequest {
        CmsfThreadLocalStack.pop();
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
