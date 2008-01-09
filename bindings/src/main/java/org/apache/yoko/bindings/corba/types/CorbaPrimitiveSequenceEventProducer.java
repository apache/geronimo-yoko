package org.apache.yoko.bindings.corba.types;

import org.apache.cxf.service.model.ServiceInfo;

import org.omg.CORBA.ORB;

public class CorbaPrimitiveSequenceEventProducer extends AbstractNoStartEndEventProducer {
    
    // No start and end elements for the sequence
    public CorbaPrimitiveSequenceEventProducer(CorbaObjectHandler h,
                                               ServiceInfo service,
                                               ORB orbRef) {
        CorbaSequenceHandler handler = (CorbaSequenceHandler)h;
        iterator = handler.getElements().iterator();
        orb = orbRef;
        serviceInfo = service;
    }
}
