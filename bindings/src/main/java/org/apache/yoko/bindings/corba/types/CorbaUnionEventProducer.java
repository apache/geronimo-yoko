package org.apache.yoko.bindings.corba.types;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;

import org.apache.cxf.service.model.ServiceInfo;
import org.apache.schemas.yoko.bindings.corba.Union;
import org.apache.schemas.yoko.bindings.corba.Unionbranch;
import org.omg.CORBA.ORB;

public class CorbaUnionEventProducer extends AbstractStartEndEventProducer {

    static final List<Attribute> IS_NIL_ATTRIBUTE_LIST = new ArrayList<Attribute>();
    static {
        XMLEventFactory factory = XMLEventFactory.newInstance();
        IS_NIL_ATTRIBUTE_LIST.add(factory.createAttribute(
                    new QName("http://www.w3.org/2001/XMLSchema-instance", "nil"), "true"));
    }
    private final boolean isNil;

    public CorbaUnionEventProducer(CorbaObjectHandler h, ServiceInfo sInfo, ORB o) {
        CorbaUnionHandler handler = (CorbaUnionHandler) h;
        serviceInfo = sInfo;
        orb = o;
        name = handler.getName();        
        isNil = checkIsNil(handler);
        if (!isNil) {
            CorbaObjectHandler contents = handler.getValue();
            if (contents != null) {      
                Union unionType = (Union)handler.getType();
                List<Unionbranch> branches = unionType.getUnionbranch();
                if (unionType.isSetNillable() && unionType.isNillable()) {
                    CorbaTypeEventProducer contentEventProducer = 
                        CorbaHandlerUtils.getTypeEventProducer(contents, serviceInfo, orb);
                    currentEventProducer = new SkipStartEndEventProducer(contentEventProducer, name);
                } else {
                    List<CorbaObjectHandler> list = new ArrayList<CorbaObjectHandler>();
                    list.add(contents);
                    iterator = list.iterator();
                }
            }
        }
    }

    private boolean checkIsNil(CorbaUnionHandler handler) {
        boolean isItNil = false;
        Union unionType = (Union)handler.getType();

        if (unionType.isSetNillable() && unionType.isNillable()) {
            CorbaPrimitiveHandler descHandler = (CorbaPrimitiveHandler) handler.getDiscriminator();
            Boolean descValue = (Boolean) descHandler.getValue();
            if (!((Boolean)descValue).booleanValue()) {
                isItNil = true;
            }
        }
        return isItNil;
    }
    
    public List<Attribute> getAttributes() {
        List<Attribute> attributes = IS_NIL_ATTRIBUTE_LIST;
        if (!isNil) {
            attributes = super.getAttributes();
        }
        return attributes;
    }
}
