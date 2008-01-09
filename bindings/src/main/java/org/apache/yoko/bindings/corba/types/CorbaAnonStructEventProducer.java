package org.apache.yoko.bindings.corba.types;

import org.apache.cxf.service.model.ServiceInfo;

import org.omg.CORBA.ORB;

public class CorbaAnonStructEventProducer extends AbstractNoStartEndEventProducer {
    
    // No start and end elements for the sequence
    public CorbaAnonStructEventProducer(CorbaObjectHandler h,
                                        ServiceInfo service,
                                        ORB orbRef) {
        CorbaStructHandler handler = (CorbaStructHandler)h;
        iterator = handler.members.iterator();
        orb = orbRef;
        serviceInfo = service;
    }
}
