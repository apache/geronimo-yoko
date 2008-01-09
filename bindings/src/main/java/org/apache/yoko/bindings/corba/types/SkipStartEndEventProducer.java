package org.apache.yoko.bindings.corba.types;

import java.util.List;

import javax.xml.namespace.QName;
//import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;

public class SkipStartEndEventProducer implements CorbaTypeEventProducer {

    private final CorbaTypeEventProducer eventProducer;
    private QName name;   
    private int peekedEvent;
    private boolean hasNext = true;

    public SkipStartEndEventProducer(CorbaTypeEventProducer contentEventProducer, QName n) {
        eventProducer = contentEventProducer;
        name = n;
        // skip start_element
        contentEventProducer.next();
        peekedEvent = contentEventProducer.next();
    }

    public String getLocalName() {
        return name.getLocalPart();
    }

    public QName getName() {
        return name;
    }

    public String getText() {    
        return eventProducer.getText();
    }

    public boolean hasNext() {
        boolean ret = hasNext;
        if (ret) {
            ret = eventProducer.hasNext();
        }
        return ret;
    }

    public int next() {
        int ret = peekedEvent;
        name = eventProducer.getName();
        peekedEvent = eventProducer.next();
        /*
        if (peekedEvent == XMLStreamReader.END_ELEMENT) {
            hasNext = false;
            peekedEvent = 0;
        }
        */
        return ret;
    }

    public List<Attribute> getAttributes() {
        return eventProducer.getAttributes();
    }

    public List<Namespace> getNamespaces() {
        return eventProducer.getNamespaces();
    }

}
