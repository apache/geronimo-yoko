package org.apache.yoko.tools.utils;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import junit.framework.Assert;

public class WSDLGenerationTester {
       
    public WSDLGenerationTester() {    
    }
    
    public void compare(XMLStreamReader orig, XMLStreamReader actual)
        throws Exception {

        boolean origEnd = false;
        boolean actualEnd = false;
        while (orig.hasNext() || actual.hasNext()) {
            int origTag = orig.next();
            while (!orig.isStartElement() && !orig.isEndElement() && !orig.isCharacters()) {
                if (orig.hasNext()) {
                    origTag = orig.next();
                } else {
                    origEnd = true;
                    break;
                }
            }
            int actualTag = actual.next();
            while (!actual.isStartElement() && !actual.isEndElement() && !actual.isCharacters()) {
                if (actual.hasNext()) {
                    actualTag = actual.next();
                } else {
                    actualEnd = true;
                    break;
                }
            }
            if (!origEnd && !actualEnd) {
                Assert.assertEquals("XML mismatch", origTag, actualTag);
                if (orig.isStartElement()) {
                    compareStartElement(orig, actual);
                } else if (orig.isEndElement()) {
                    compareEndElement(orig, actual);
                } else if (orig.isCharacters()) {
                    compareCharacters(orig, actual);
                }
            } else {
                break;
            }
        }
    }

    private void compareStartElement(XMLStreamReader orig, XMLStreamReader actual)
        throws Exception {        
        Assert.assertEquals("Start element is not matched", orig.getName(), actual.getName());
        Assert.assertEquals("Attribute count is not matched for element " + orig.getName(),
                     orig.getAttributeCount(),
                     actual.getAttributeCount());
        int count = orig.getAttributeCount();
        for (int i = 0; i < count; i++) {
            QName attrName = orig.getAttributeName(i);
            Assert.assertEquals("Attribute " + attrName + " not found or value not matching",
                         orig.getAttributeValue(attrName.getNamespaceURI(), attrName.getLocalPart()),
                         actual.getAttributeValue(attrName.getNamespaceURI(), attrName.getLocalPart()));
        }
    }
    
    private void compareEndElement(XMLStreamReader orig, XMLStreamReader actual)
        throws Exception {
        Assert.assertEquals("End element is not matched", orig.getName(), actual.getName());
    }
                                 
    private void compareCharacters(XMLStreamReader orig, XMLStreamReader actual)
        throws Exception {
        Assert.assertEquals("Element Characters not matched", orig.getText(), actual.getText());
    }                                                                       
}
