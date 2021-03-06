package org.apache.yoko.orb.cmsf;

import static org.apache.yoko.orb.OB.MinorCodes.MinorInvalidComponentId;
import static org.apache.yoko.orb.cmsf.CmsfVersion.CMSFv1;
import static org.apache.yoko.orb.cmsf.CmsfVersion.CMSFv2;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.yoko.util.cmsf.CmsfThreadLocal;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.LocalObject;
import org.omg.IOP.TAG_RMI_CUSTOM_MAX_STREAM_FORMAT;
import org.omg.IOP.TaggedComponent;
import org.omg.PortableInterceptor.ClientRequestInfo;
import org.omg.PortableInterceptor.ClientRequestInterceptor;
import org.omg.PortableInterceptor.ForwardRequest;

public final class CmsfClientInterceptor extends LocalObject implements ClientRequestInterceptor {
    private static final String NAME = CmsfClientInterceptor.class.getName();

    @Override
    public void send_request(ClientRequestInfo ri) throws ForwardRequest {
        CmsfVersion cmsf = CMSFv1;
        try {
            TaggedComponent tc = ri.get_effective_component(TAG_RMI_CUSTOM_MAX_STREAM_FORMAT.value);
            cmsf = CmsfVersion.readData(tc.component_data);
        } catch (BAD_PARAM e) {
            if (e.minor != MinorInvalidComponentId) {
                throw e;
            }
        }
        CmsfThreadLocal.push(cmsf.getValue());
        
        if (CmsfVersion.ENABLED) ri.add_request_service_context(CMSFv2.getSc(), false);
    }

    @Override
    public void send_poll(ClientRequestInfo ri) {
    }

    @Override
    public void receive_reply(ClientRequestInfo ri) {
        CmsfThreadLocal.pop();
    }

    @Override
    public void receive_exception(ClientRequestInfo ri) throws ForwardRequest {
        CmsfThreadLocal.pop();
    }

    @Override
    public void receive_other(ClientRequestInfo ri) throws ForwardRequest {
        CmsfThreadLocal.pop();
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
