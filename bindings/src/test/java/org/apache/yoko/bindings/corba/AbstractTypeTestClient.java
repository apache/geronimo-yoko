/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.yoko.bindings.corba;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;

import org.apache.type_test.corba.TypeTestPortType;
import org.apache.type_test.corba.TypeTestTester;
import org.apache.type_test.corba.TypeTestCORBAService;

import org.apache.type_test.types1.NMTokenEnum;
import org.apache.type_test.types1.SimpleEnum;

import junit.framework.TestCase;

public abstract class AbstractTypeTestClient extends TestCase implements TypeTestTester {
    protected static TypeTestPortType client;

    protected boolean perfTestOnly;

    public AbstractTypeTestClient(String name) {
        super(name); 
    }

    public void setPerformanceTestOnly() {
        perfTestOnly = true;
    }

    public static void initClient(Class clz, QName serviceName, QName portName, String wsdlPath) 
        throws Exception {       
        URL wsdlLocation = clz.getResource(wsdlPath);
        TypeTestCORBAService service =
            new TypeTestCORBAService(wsdlLocation, serviceName);
        client = service.getPort(portName, org.apache.type_test.corba.TypeTestPortType.class);
        assertNotNull("Could not create corba client", client);
    }

    protected boolean equalsDate(XMLGregorianCalendar orig, XMLGregorianCalendar actual) {
        boolean result = false;

        if ((orig.getYear() == actual.getYear()) 
            && (orig.getMonth() == actual.getMonth())
            && (orig.getDay() == actual.getDay())
            && (actual.getHour() == DatatypeConstants.FIELD_UNDEFINED) 
            && (actual.getMinute() == DatatypeConstants.FIELD_UNDEFINED)
            && (actual.getSecond() == DatatypeConstants.FIELD_UNDEFINED)
            && (actual.getMillisecond() == DatatypeConstants.FIELD_UNDEFINED)) {

            result = orig.getTimezone() == actual.getTimezone();
        }
        return result;
    }

    protected boolean equalsTime(XMLGregorianCalendar orig, XMLGregorianCalendar actual) {
        boolean result = false;
        if ((orig.getHour() == actual.getHour())
            && (orig.getMinute() == actual.getMinute())
            && (orig.getSecond() == actual.getSecond())
            && (orig.getMillisecond() == actual.getMillisecond())
            && (orig.getTimezone() == actual.getTimezone())) {
            result = true;
        }
        return result;
    }

    protected boolean equalsDateTime(XMLGregorianCalendar orig, XMLGregorianCalendar actual) {
        boolean result = false;
        if ((orig.getYear() == actual.getYear())
            && (orig.getMonth() == actual.getMonth())
            && (orig.getDay() == actual.getDay())
            && (orig.getHour() == actual.getHour())
            && (orig.getMinute() == actual.getMinute())
            && (orig.getSecond() == actual.getSecond())
            && (orig.getMillisecond() == actual.getMillisecond())) {

            result = orig.getTimezone() == actual.getTimezone();
        }
        return result;
    }
    
    public void testVoid() throws Exception {
        client.testVoid();
    }
    
    public void testOneway() throws Exception {
        String x = "hello";
        String y = "oneway";        
        client.testOneway(x, y);
    }

    public void testByte() throws Exception {
        byte valueSets[][] = {
            {0, 1},
            {(byte)100, (byte)127}
        };

        for (int i = 0; i < valueSets.length; i++) {
            byte x = valueSets[i][0];
            Holder<Byte> yOrig = new Holder<Byte>(valueSets[i][1]);
            Holder<Byte> y = new Holder<Byte>(valueSets[i][1]);
            Holder<Byte> z = new Holder<Byte>();
            byte ret = client.testByte(x, y, z);
            if (!perfTestOnly) {
                assertEquals("testByte(): Incorrect value for inout param",
                             Byte.valueOf(x), y.value);
                assertEquals("testByte(): Incorrect value for out param",
                             yOrig.value, z.value);
                assertEquals("testByte(): Incorrect return value", x, ret);
            }
        }
    }

    public void testShort() throws Exception {
        short valueSets[][] = {
            {0, 1},
            {-1, 0},
            {Short.MIN_VALUE, Short.MAX_VALUE}
        };

        for (int i = 0; i < valueSets.length; i++) {
            short x = valueSets[i][0];
            Holder<Short> yOrig = new Holder<Short>(valueSets[i][1]);
            Holder<Short> y = new Holder<Short>(valueSets[i][1]);
            Holder<Short> z = new Holder<Short>();

            short ret = client.testShort(x, y, z);
    
            if (!perfTestOnly) {
                assertEquals("testShort(): Incorrect value for inout param", Short.valueOf(x), y.value);
                assertEquals("testShort(): Incorrect value for out param", yOrig.value, z.value);
                assertEquals("testShort(): Incorrect return value", x, ret);
            }
        }
    }

    public void testUnsignedShort() throws Exception {
        int valueSets[][] = {{0, 1}, {1, 0}, {0, Byte.MAX_VALUE * 2 + 1}};

        for (int i = 0; i < valueSets.length; i++) {
            int x = valueSets[i][0];
            Holder<Integer> yOrig = new Holder<Integer>(valueSets[i][1]);
            Holder<Integer> y = new Holder<Integer>(valueSets[i][1]);
            Holder<Integer> z = new Holder<Integer>();

            int ret = client.testUnsignedShort(x, y, z);
 
            if (!perfTestOnly) {
                assertEquals("testUnsignedShort(): Incorrect value for inout param",
                             Integer.valueOf(x), y.value);
                assertEquals("testUnsignedShort(): Incorrect value for out param",
                             yOrig.value, z.value);
                assertEquals("testUnsignedShort(): Incorrect return value", x, ret);
            }
        }
    }

    public void testInt() throws Exception {
        int valueSets[][] = {{5, 10}, {-10, 50},
                             {Integer.MIN_VALUE, Integer.MAX_VALUE}};

        for (int i = 0; i < valueSets.length; i++) {
            int x = valueSets[i][0];
            Holder<Integer> yOrig = new Holder<Integer>(valueSets[i][1]);
            Holder<Integer> y = new Holder<Integer>(valueSets[i][1]);
            Holder<Integer> z = new Holder<Integer>();
            
            int ret = client.testInt(x, y, z);

            if (!perfTestOnly) {
                assertEquals("testInt(): Incorrect value for inout param", Integer.valueOf(x), y.value);
                assertEquals("testInt(): Incorrect value for out param", yOrig.value, z.value);
                assertEquals("testInt(): Incorrect return value", x, ret);
            }
        }
    }

    public void testUnsignedInt() throws Exception {
        long valueSets[][] = {{11, 20}, {1, 0},
                              {0, ((long)Short.MAX_VALUE) * 2 + 1}};

        for (int i = 0; i < valueSets.length; i++) {
            long x = valueSets[i][0];
            long yOrig = valueSets[i][1];
            Holder<Long> y = new Holder<Long>(valueSets[i][1]);
            Holder<Long> z = new Holder<Long>();

            long ret = client.testUnsignedInt(x, y, z);
            if (!perfTestOnly) {
                assertEquals("testUnsignedInt(): Incorrect value for inout param",
                             Long.valueOf(x), y.value);
                assertEquals("testUnsignedInt(): Incorrect value for out param",
                             Long.valueOf(yOrig), z.value);
                assertEquals("testUnsignedInt(): Incorrect return value", x, ret);
            }
        }
    }

    public void testLong() throws Exception {
        long valueSets[][] = {{0, 1}, {-1, 0},
                              {Long.MIN_VALUE, Long.MAX_VALUE}};

        for (int i = 0; i < valueSets.length; i++) {
            long x = valueSets[i][0];
            Holder<Long> yOrig = new Holder<Long>(valueSets[i][1]);
            Holder<Long> y = new Holder<Long>(valueSets[i][1]);
            Holder<Long> z = new Holder<Long>();

            long ret = client.testLong(x, y, z);

            if (!perfTestOnly) {
                assertEquals("testLong(): Incorrect value for inout param", Long.valueOf(x), y.value);
                assertEquals("testLong(): Incorrect value for out param", yOrig.value, z.value);
                assertEquals("testLong(): Incorrect return value", x, ret);
            }
        }
    }

    public void testUnsignedLong() throws Exception {
        BigInteger valueSets[][] = {{new BigInteger("0"), new BigInteger("1")},
                                    {new BigInteger("1"), new BigInteger("0")},
                                    {new BigInteger("0"), 
                                     new BigInteger(String.valueOf(Long.MAX_VALUE * Long.MAX_VALUE))}};

        for (int i = 0; i < valueSets.length; i++) {
            BigInteger x = valueSets[i][0];
            Holder<BigInteger> yOrig = new Holder<BigInteger>(valueSets[i][1]);
            Holder<BigInteger> y = new Holder<BigInteger>(valueSets[i][1]);
            Holder<BigInteger> z = new Holder<BigInteger>();

            BigInteger ret = client.testUnsignedLong(x, y, z);
            
            if (!perfTestOnly) {
                assertEquals("testUnsignedLong(): Incorrect value for inout param", x, y.value);
                assertEquals("testUnsignedLong(): Incorrect value for out param", yOrig.value, z.value);
                assertEquals("testUnsignedLong(): Incorrect return value", x, ret);
            }
        }
    }

    public void testFloat() throws Exception {
        float delta = 0.0f;
        float valueSets[][] = {
            {0.0f, 1.0f},
            {-1.0f, (float)java.lang.Math.PI},
            {-100.0f, 100.0f}
        };

        for (int i = 0; i < valueSets.length; i++) {
            float x = valueSets[i][0];
            Holder<Float> yOrig = new Holder<Float>(valueSets[i][1]);
            Holder<Float> y = new Holder<Float>(valueSets[i][1]);
            Holder<Float> z = new Holder<Float>();

            float ret = client.testFloat(x, y, z);

            if (!perfTestOnly) {
                assertEquals(i + ": testFloat(): Wrong value for inout param", x, y.value, delta);
                assertEquals(i + ": testFloat(): Wrong value for out param", yOrig.value, z.value, delta);
                assertEquals(i + ": testFloat(): Wrong return value", x, ret, delta);
            }
        }

        float x = Float.NaN;
        Holder<Float> yOrig = new Holder<Float>(0.0f);
        Holder<Float> y = new Holder<Float>(0.0f);
        Holder<Float> z = new Holder<Float>();
        float ret = client.testFloat(x, y, z);

        if (!perfTestOnly) {
            assertTrue("testFloat(): Incorrect value for inout param", Float.isNaN(y.value));
            assertEquals("testFloat(): Incorrect value for out param", yOrig.value, z.value, delta);
            assertTrue("testFloat(): Incorrect return value", Float.isNaN(ret));
        }
    }

    public void testDouble() throws Exception {
        double delta = 0.0d;
        double valueSets[][] = {
            {0.0f, 1.0f},
            {-1, java.lang.Math.PI},
            {-100.0, 100.0}
        };
        for (int i = 0; i < valueSets.length; i++) {
            double x = valueSets[i][0];
            Holder<Double> yOrig = new Holder<Double>(valueSets[i][1]);
            Holder<Double> y = new Holder<Double>(valueSets[i][1]);
            Holder<Double> z = new Holder<Double>();

            double ret = client.testDouble(x, y, z);

            if (!perfTestOnly) {
                assertEquals("testDouble(): Incorrect value for inout param", x, y.value, delta);
                assertEquals("testDouble(): Incorrect value for out param", yOrig.value, z.value, delta);
                assertEquals("testDouble(): Incorrect return value", x, ret, delta);
            }
        }

        double x = Double.NaN;
        Holder<Double> yOrig = new Holder<Double>(0.0);
        Holder<Double> y = new Holder<Double>(0.0);
        Holder<Double> z = new Holder<Double>();
        double ret = client.testDouble(x, y, z);

        if (!perfTestOnly) {
            assertTrue("testDouble(): Incorrect value for inout param", Double.isNaN(y.value));
            assertEquals("testDouble(): Incorrect value for out param", yOrig.value, z.value, delta);
            assertTrue("testDouble(): Incorrect return value", Double.isNaN(ret));
        }
    }

    public void testUnsignedByte() throws Exception {
        short valueSets[][] = {{0, 1}, {1, 0},
                               {0, 127}};

        for (int i = 0; i < valueSets.length; i++) {
            short x = valueSets[i][0];
            Holder<Short> yOrig = new Holder<Short>(valueSets[i][1]);
            Holder<Short> y = new Holder<Short>(valueSets[i][1]);
            Holder<Short> z = new Holder<Short>();

            short ret = client.testUnsignedByte(x, y, z);

            if (!perfTestOnly) {
                assertEquals("testUnsignedByte(): Incorrect value for inout param",
                             Short.valueOf(x), y.value);
                assertEquals("testUnsignedByte(): Incorrect value for out param",
                             yOrig.value, z.value);
                assertEquals("testUnsignedByte(): Incorrect return value", x, ret);
            }
        }
    }

    public void testBoolean() throws Exception {
        boolean valueSets[][] = {{true, false}, {true, true},
                                 {false, true}, {false, false}};

        for (int i = 0; i < valueSets.length; i++) {
            boolean x = valueSets[i][0];
            Holder<Boolean> yOrig = new Holder<Boolean>(valueSets[i][1]);
            Holder<Boolean> y = new Holder<Boolean>(valueSets[i][1]);
            Holder<Boolean> z = new Holder<Boolean>();

            boolean ret = client.testBoolean(x, y, z);

            if (!perfTestOnly) {
                assertEquals("testBoolean(): Incorrect value for inout param", Boolean.valueOf(x), y.value);
                assertEquals("testBoolean(): Incorrect value for out param", yOrig.value, z.value);
                assertEquals("testBoolean(): Incorrect return value", x, ret);
            }
        }
    }

    public void testString() throws Exception {
        int bufferSize = 1000;
        StringBuffer buffer = new StringBuffer(bufferSize);
        StringBuffer buffer2 = new StringBuffer(bufferSize);
        for (int x = 0; x < bufferSize; x++) {
            buffer.append((char)('a' + (x % 26)));
            buffer2.append((char)('A' + (x % 26)));
        }
        
        String valueSets[][] = {{"hello", "world"}, {"is pi > 3 ?", " is pi < 4\\\""}};

        for (int i = 0; i < valueSets.length; i++) {
            String x = valueSets[i][0];
            Holder<String> yOrig = new Holder<String>(valueSets[i][1]);
            Holder<String> y = new Holder<String>(valueSets[i][1]);
            Holder<String> z = new Holder<String>();

            String ret = client.testString(x, y, z);

            if (!perfTestOnly) {
                assertEquals("testString(): Incorrect value for inout param", x, y.value);
                assertEquals("testString(): Incorrect value for out param", yOrig.value, z.value);
                assertEquals("testString(): Incorrect return value", x, ret);
            }
        }
    }
    
    public void testQName() throws Exception {
        String valueSets[][] = {
            {"NoNamespaceService", ""},
            {"HelloWorldService", "http://www.iona.com/services"},
            {"MyService", "http://www.iona.com/iona"}
        };
        for (int i = 0; i < valueSets.length; i++) {
            QName x = new QName(valueSets[i][1], valueSets[i][0]);
            QName yOrig = new QName("http://www.iona.com/inoutqname", "InOutQName");
            Holder<QName> y = new Holder<QName>(yOrig);
            Holder<QName> z = new Holder<QName>();

            QName ret = client.testQName(x, y, z);

            if (!perfTestOnly) {
                assertEquals("testQName(): Incorrect value for inout param", x, y.value);
                assertEquals("testQName(): Incorrect value for out param", yOrig, z.value);
                assertEquals("testQName(): Incorrect return value", x, ret);
            }
        }
    }
    
    public void testDate() throws Exception {
    }
    

    public void testDateTime() throws Exception {
    }

    public void testTime() throws Exception {
    }

    public void testGYear() throws Exception {
        javax.xml.datatype.DatatypeFactory datatypeFactory = javax.xml.datatype.DatatypeFactory.newInstance();

        XMLGregorianCalendar x = datatypeFactory.newXMLGregorianCalendar("2004");
        XMLGregorianCalendar yOrig = datatypeFactory.newXMLGregorianCalendar("2003+05:00");

        Holder<XMLGregorianCalendar> y = new Holder<XMLGregorianCalendar>(yOrig);
        Holder<XMLGregorianCalendar> z = new Holder<XMLGregorianCalendar>();

        XMLGregorianCalendar ret = client.testGYear(x, y, z);
        if (!perfTestOnly) {
            assertTrue("testGYear(): Incorrect value for inout param", x.equals(y.value));
            assertTrue("testGYear(): Incorrect value for out param", yOrig.equals(z.value));
            assertTrue("testGYear(): Incorrect return value", x.equals(ret));
        }
    }

    public void testGYearMonth() throws Exception {
        javax.xml.datatype.DatatypeFactory datatypeFactory = javax.xml.datatype.DatatypeFactory.newInstance();

        XMLGregorianCalendar x = datatypeFactory.newXMLGregorianCalendar("2004-08");
        XMLGregorianCalendar yOrig = datatypeFactory.newXMLGregorianCalendar("2003-12+05:00");

        Holder<XMLGregorianCalendar> y = new Holder<XMLGregorianCalendar>(yOrig);
        Holder<XMLGregorianCalendar> z = new Holder<XMLGregorianCalendar>();

        XMLGregorianCalendar ret = client.testGYearMonth(x, y, z);            
        if (!perfTestOnly) {
            assertTrue("testGYearMonth(): Incorrect value for inout param", x.equals(y.value));
            assertTrue("testGYearMonth(): Incorrect value for out param", yOrig.equals(z.value));
            assertTrue("testGYearMonth(): Incorrect return value", x.equals(ret));
        }
    }

    public void testGMonth() throws Exception {
        javax.xml.datatype.DatatypeFactory datatypeFactory = javax.xml.datatype.DatatypeFactory.newInstance();

        XMLGregorianCalendar x = datatypeFactory.newXMLGregorianCalendar("--08--");
        XMLGregorianCalendar yOrig = datatypeFactory.newXMLGregorianCalendar("--12--+05:00");

        Holder<XMLGregorianCalendar> y = new Holder<XMLGregorianCalendar>(yOrig);
        Holder<XMLGregorianCalendar> z = new Holder<XMLGregorianCalendar>();

        XMLGregorianCalendar ret = client.testGMonth(x, y, z);
        if (!perfTestOnly) {
            assertTrue("testGMonth(): Incorrect value for inout param", x.equals(y.value));
            assertTrue("testGMonth(): Incorrect value for out param", yOrig.equals(z.value));
            assertTrue("testGMonth(): Incorrect return value", x.equals(ret));
        }
    }

    public void testGMonthDay() throws Exception {
        javax.xml.datatype.DatatypeFactory datatypeFactory = javax.xml.datatype.DatatypeFactory.newInstance();

        XMLGregorianCalendar x = datatypeFactory.newXMLGregorianCalendar("--08-21");
        XMLGregorianCalendar yOrig = datatypeFactory.newXMLGregorianCalendar("--12-05+05:00");

        Holder<XMLGregorianCalendar> y = new Holder<XMLGregorianCalendar>(yOrig);
        Holder<XMLGregorianCalendar> z = new Holder<XMLGregorianCalendar>();

        XMLGregorianCalendar ret = client.testGMonthDay(x, y, z);            
        if (!perfTestOnly) {
            assertTrue("testGMonthDay(): Incorrect value for inout param", x.equals(y.value));
            assertTrue("testGMonthDay(): Incorrect value for out param", yOrig.equals(z.value));
            assertTrue("testGMonthDay(): Incorrect return value", x.equals(ret));
        }
    }

    public void testGDay() throws Exception {
        javax.xml.datatype.DatatypeFactory datatypeFactory = javax.xml.datatype.DatatypeFactory.newInstance();

        XMLGregorianCalendar x = datatypeFactory.newXMLGregorianCalendar("---21");
        XMLGregorianCalendar yOrig = datatypeFactory.newXMLGregorianCalendar("---05+05:00");

        Holder<XMLGregorianCalendar> y = new Holder<XMLGregorianCalendar>(yOrig);
        Holder<XMLGregorianCalendar> z = new Holder<XMLGregorianCalendar>();

        XMLGregorianCalendar ret = client.testGDay(x, y, z);            
        if (!perfTestOnly) {
            assertTrue("testGDay(): Incorrect value for inout param", x.equals(y.value));
            assertTrue("testGDay(): Incorrect value for out param", yOrig.equals(z.value));
            assertTrue("testGDay(): Incorrect return value", x.equals(ret));
        }
    }

    public void testDuration() throws Exception {
        javax.xml.datatype.DatatypeFactory datatypeFactory = javax.xml.datatype.DatatypeFactory.newInstance();

        Duration x = datatypeFactory.newDuration("P1Y35DT60M60.500S");
        Duration yOrig = datatypeFactory.newDuration("-P2MT24H60S");

        Holder<Duration> y = new Holder<Duration>(yOrig);
        Holder<Duration> z = new Holder<Duration>();

        Duration ret = client.testDuration(x, y, z);            
        if (!perfTestOnly) {
            assertTrue("testDuration(): Incorrect value for inout param", x.equals(y.value));
            assertTrue("testDuration(): Incorrect value for out param", yOrig.equals(z.value));
            assertTrue("testDuration(): Incorrect return value", x.equals(ret));
        }
    }

    public void testNormalizedString() throws Exception {
        String x = "  normalized string ";
        String yOrig = "  another normalized  string ";

        Holder<String> y = new Holder<String>(yOrig);
        Holder<String> z = new Holder<String>();

        String ret = client.testNormalizedString(x, y, z);            
        if (!perfTestOnly) {
            assertTrue("testNormalizedString(): Incorrect value for inout param", x.equals(y.value));
            assertTrue("testNormalizedString(): Incorrect value for out param", yOrig.equals(z.value));
            assertTrue("testNormalizedString(): Incorrect return value", x.equals(ret));
        }
    }

    public void testToken() throws Exception {
        String x = "token";
        String yOrig = "another token";

        Holder<String> y = new Holder<String>(yOrig);
        Holder<String> z = new Holder<String>();

        String ret = client.testToken(x, y, z);            
        if (!perfTestOnly) {
            assertTrue("testToken(): Incorrect value for inout param", x.equals(y.value));
            assertTrue("testToken(): Incorrect value for out param", yOrig.equals(z.value));
            assertTrue("testToken(): Incorrect return value", x.equals(ret));
        }
    }

    public void testLanguage() throws Exception {
        String x = "abc";
        String yOrig = "abc-def";

        Holder<String> y = new Holder<String>(yOrig);
        Holder<String> z = new Holder<String>();

        String ret = client.testLanguage(x, y, z);            
        if (!perfTestOnly) {
            assertTrue("testLanguage(): Incorrect value for inout param", x.equals(y.value));
            assertTrue("testLanguage(): Incorrect value for out param", yOrig.equals(z.value));
            assertTrue("testLanguage(): Incorrect return value", x.equals(ret));
        }
    }

    public void testNMTOKEN() throws Exception {
        String x = "123:abc";
        String yOrig = "abc.-_:";

        Holder<String> y = new Holder<String>(yOrig);
        Holder<String> z = new Holder<String>();

        String ret = client.testNMTOKEN(x, y, z);            
        if (!perfTestOnly) {
            assertTrue("testNMTOKEN(): Incorrect value for inout param", x.equals(y.value));
            assertTrue("testNMTOKEN(): Incorrect value for out param", yOrig.equals(z.value));
            assertTrue("testNMTOKEN(): Incorrect return value", x.equals(ret));
        }
    }

    public void testNMTOKENS() throws Exception {
    }
    
    public void testName() throws Exception {
        String x = "abc:123";
        String yOrig = "abc.-_";

        Holder<String> y = new Holder<String>(yOrig);
        Holder<String> z = new Holder<String>();

        String ret = client.testName(x, y, z);
        if (!perfTestOnly) {
            assertTrue("testName(): Incorrect value for inout param", x.equals(y.value));
            assertTrue("testName(): Incorrect value for out param", yOrig.equals(z.value));
            assertTrue("testName(): Incorrect return value", x.equals(ret));
        }
    }

    public void testNCName() throws Exception {
        String x = "abc-123";
        String yOrig = "abc.-";

        Holder<String> y = new Holder<String>(yOrig);
        Holder<String> z = new Holder<String>();

        String ret = client.testNCName(x, y, z);
        if (!perfTestOnly) {
            assertTrue("testNCName(): Incorrect value for inout param", x.equals(y.value));
            assertTrue("testNCName(): Incorrect value for out param", yOrig.equals(z.value));
            assertTrue("testNCName(): Incorrect return value", x.equals(ret));
        }
    }

    public void testID() throws Exception {
    }
    
    public void testDecimal() throws Exception {
        BigDecimal valueSets[][] = {
            {new BigDecimal("-1234567890.000000"), new BigDecimal("1234567890.000000")},
            {new BigDecimal("-" + String.valueOf(Long.MAX_VALUE * Long.MAX_VALUE) + ".000000"),
             new BigDecimal(String.valueOf(Long.MAX_VALUE * Long.MAX_VALUE) + ".000000")}
        };

        for (int i = 0; i < valueSets.length; i++) {
            BigDecimal x = valueSets[i][0];
            Holder<BigDecimal> yOrig = new Holder<BigDecimal>(valueSets[i][1]);
            Holder<BigDecimal> y = new Holder<BigDecimal>(valueSets[i][1]);
            Holder<BigDecimal> z = new Holder<BigDecimal>();

            BigDecimal ret = client.testDecimal(x, y, z);

            if (!perfTestOnly) {
                assertEquals("testDecimal(): Incorrect value for inout param", x, y.value);
                assertEquals("testDecimal(): Incorrect value for out param", yOrig.value, z.value);
                assertEquals("testDecimal(): Incorrect return value", x, ret);
            }
        }
    }

    public void testInteger() throws Exception {
        BigInteger valueSets[][] = {
            {new BigInteger("-1234567890"), new BigInteger("1234567890")},
            {new BigInteger("-" + String.valueOf(Long.MAX_VALUE * Long.MAX_VALUE)),
             new BigInteger(String.valueOf(Long.MAX_VALUE * Long.MAX_VALUE))}
        };

        for (int i = 0; i < valueSets.length; i++) {
            BigInteger x = valueSets[i][0];
            Holder<BigInteger> yOrig = new Holder<BigInteger>(valueSets[i][1]);
            Holder<BigInteger> y = new Holder<BigInteger>(valueSets[i][1]);
            Holder<BigInteger> z = new Holder<BigInteger>();

            BigInteger ret = client.testInteger(x, y, z);

            if (!perfTestOnly) {
                assertEquals("testInteger(): Incorrect value for inout param", x, y.value);
                assertEquals("testInteger(): Incorrect value for out param", yOrig.value, z.value);
                assertEquals("testInteger(): Incorrect return value", x, ret);
            }
        }
    }

    public void testPositiveInteger() throws Exception {
        BigInteger valueSets[][] = {
            {new BigInteger("1"), new BigInteger("1234567890")},
            {new BigInteger(String.valueOf(Integer.MAX_VALUE * Integer.MAX_VALUE)),
             new BigInteger(String.valueOf(Long.MAX_VALUE * Long.MAX_VALUE))}
        };

        for (int i = 0; i < valueSets.length; i++) {
            BigInteger x = valueSets[i][0];
            Holder<BigInteger> yOrig = new Holder<BigInteger>(valueSets[i][1]);
            Holder<BigInteger> y = new Holder<BigInteger>(valueSets[i][1]);
            Holder<BigInteger> z = new Holder<BigInteger>();

            BigInteger ret = client.testPositiveInteger(x, y, z);

            if (!perfTestOnly) {
                assertEquals("testPositiveInteger(): Incorrect value for inout param", x, y.value);
                assertEquals("testPositiveInteger(): Incorrect value for out param", yOrig.value, z.value);
                assertEquals("testPositiveInteger(): Incorrect return value", x, ret);
            }
        }
    }

    public void testNonPositiveInteger() throws Exception {
        BigInteger valueSets[][] = {
            {new BigInteger("0"), new BigInteger("-1234567890")},
            {new BigInteger("-" + String.valueOf(Integer.MAX_VALUE * Integer.MAX_VALUE)),
             new BigInteger("-" + String.valueOf(Long.MAX_VALUE * Long.MAX_VALUE))}
        };

        for (int i = 0; i < valueSets.length; i++) {
            BigInteger x = valueSets[i][0];
            Holder<BigInteger> yOrig = new Holder<BigInteger>(valueSets[i][1]);
            Holder<BigInteger> y = new Holder<BigInteger>(valueSets[i][1]);
            Holder<BigInteger> z = new Holder<BigInteger>();

            BigInteger ret = client.testNonPositiveInteger(x, y, z);

            if (!perfTestOnly) {
                assertEquals("testNonPositiveInteger(): Incorrect value for inout param", x, y.value);
                assertEquals("testNonPositiveInteger(): Incorrect value for out param", yOrig.value, z.value);
                assertEquals("testNonPositiveInteger(): Incorrect return value", x, ret);
            }
        }
    }

    public void testNegativeInteger() throws Exception {
        BigInteger valueSets[][] = {
            {new BigInteger("-1"), new BigInteger("-1234567890")},
            {new BigInteger("-" + String.valueOf(Integer.MAX_VALUE * Integer.MAX_VALUE)),
             new BigInteger("-" + String.valueOf(Long.MAX_VALUE * Long.MAX_VALUE))}
        };

        for (int i = 0; i < valueSets.length; i++) {
            BigInteger x = valueSets[i][0];
            Holder<BigInteger> yOrig = new Holder<BigInteger>(valueSets[i][1]);
            Holder<BigInteger> y = new Holder<BigInteger>(valueSets[i][1]);
            Holder<BigInteger> z = new Holder<BigInteger>();

            BigInteger ret = client.testNegativeInteger(x, y, z);

            if (!perfTestOnly) {
                assertEquals("testNegativeInteger(): Incorrect value for inout param", x, y.value);
                assertEquals("testNegativeInteger(): Incorrect value for out param", yOrig.value, z.value);
                assertEquals("testNegativeInteger(): Incorrect return value", x, ret);
            }
        }
    }

    public void testNonNegativeInteger() throws Exception {
        BigInteger valueSets[][] = {
            {new BigInteger("0"), new BigInteger("1234567890")},
            {new BigInteger(String.valueOf(Integer.MAX_VALUE * Integer.MAX_VALUE)),
             new BigInteger(String.valueOf(Long.MAX_VALUE * Long.MAX_VALUE))}
        };

        for (int i = 0; i < valueSets.length; i++) {
            BigInteger x = valueSets[i][0];
            Holder<BigInteger> yOrig = new Holder<BigInteger>(valueSets[i][1]);
            Holder<BigInteger> y = new Holder<BigInteger>(valueSets[i][1]);
            Holder<BigInteger> z = new Holder<BigInteger>();

            BigInteger ret = client.testNonNegativeInteger(x, y, z);

            if (!perfTestOnly) {
                assertEquals("testNonNegativeInteger(): Incorrect value for inout param", x, y.value);
                assertEquals("testNonNegativeInteger(): Incorrect value for out param", yOrig.value, z.value);
                assertEquals("testNonNegativeInteger(): Incorrect return value", x, ret);
            }
        }
    }

    public void testHexBinary() throws Exception {
        byte[] x = "hello".getBytes();
        Holder<byte[]> y = new Holder<byte[]>("goodbye".getBytes());
        Holder<byte[]> yOriginal = new Holder<byte[]>("goodbye".getBytes());
        Holder<byte[]> z = new Holder<byte[]>();
        byte[] ret = client.testHexBinary(x, y, z);

        if (!perfTestOnly) {
            assertTrue("testHexBinary(): Incorrect value for inout param",
                       Arrays.equals(x, y.value));
            assertTrue("testHexBinary(): Incorrect value for out param",
                       Arrays.equals(yOriginal.value, z.value));
            assertTrue("testHexBinary(): Incorrect return value",
                       Arrays.equals(x, ret));
        }
    }

    public void testBase64Binary() throws Exception {
        byte[] x = "hello".getBytes();
        Holder<byte[]> y = new Holder<byte[]>("goodbye".getBytes());
        Holder<byte[]> yOriginal = new Holder<byte[]>("goodbye".getBytes());
        Holder<byte[]> z = new Holder<byte[]>();
        byte[] ret = client.testBase64Binary(x, y, z);

        if (!perfTestOnly) {
            assertTrue("testBase64Binary(): Incorrect value for inout param",
                       Arrays.equals(x, y.value));
            assertTrue("testBase64Binary(): Incorrect value for out param",
                       Arrays.equals(yOriginal.value, z.value));
            assertTrue("testBase64Binary(): Incorrect return value",
                       Arrays.equals(x, ret));
        }

        // Test uninitialized holder value
        try {
            y = new Holder<byte[]>();
            z = new Holder<byte[]>();
            
            client.testBase64Binary(x, y, z);
            fail("Uninitialized Holder for inout parameter should have thrown an error.");
        } catch (Exception e) {
            // Ignore expected //failure.
        }
    }

    public void testAnyURI() throws Exception {
        String valueSets[][] = {
            {"file:///root%20%20/-;?&+", "file:///w:/test!artix~java*"},
            {"http://iona.com/", "file:///z:/mail_iona=com,\'xmlbus\'"},
            {"mailto:windows@systems", "file:///"}
        };

        for (int i = 0; i < valueSets.length; i++) {
            String x = new String(valueSets[i][0]);
            String yOrig = new String(valueSets[i][1]);
            Holder<String> y = new Holder<String>(yOrig);
            Holder<String> z = new Holder<String>();

            String ret = client.testAnyURI(x, y, z);

            if (!perfTestOnly) {
                assertEquals("testAnyURI(): Incorrect value for inout param", x, y.value);
                assertEquals("testAnyURI(): Incorrect value for out param", yOrig, z.value);
                assertEquals("testAnyURI(): Incorrect return value", x, ret);
            }
        }
    }

    /**
     * XXX - In the generated code for ColourEnum, the fromValue() method
     * is not declared static - fixed in jaxb-ri-20060421 nightly build.
     */
    public void testColourEnum() throws Exception {
        /*
        String[] xx = {"RED", "GREEN", "BLUE"};
        String[] yy = {"GREEN", "BLUE", "RED"};

        Holder<ColourEnum> z = new Holder<ColourEnum>();

        for (int i = 0; i < 3; i++) {
            ColourEnum x = ColourEnum.fromValue(xx[i]);
            ColourEnum yOrig = ColourEnum.fromValue(yy[i]);
            Holder<ColourEnum> y = new Holder<ColourEnum>(yOrig);

            ColourEnum ret = client.testColourEnum(x, y, z);

            if (!perfTestOnly) {
                assertEquals("testColourEnum(): Incorrect value for inout param",
                             x.value(), y.value.value());
                assertEquals("testColourEnum(): Incorrect value for out param",
                             yOrig.value(), z.value.value());
                assertEquals("testColourEnum(): Incorrect return value",
                             x.value(), ret.value());
            }
        }
        */
    }    
    
    public void testSimpleEnum() throws Exception {
        String[] xx = {"abc", "def", "ghi"};
        String[] yy = {"ghi", "abc", "def"};

        Holder<SimpleEnum> z = new Holder<SimpleEnum>();
        for (int i = 0; i < 3; i++) {
            SimpleEnum x = SimpleEnum.fromValue(xx[i]);
            SimpleEnum yOrig = SimpleEnum.fromValue(yy[i]);
            Holder<SimpleEnum> y = new Holder<SimpleEnum>(yOrig);

            SimpleEnum ret = client.testSimpleEnum(x, y, z);

            if (!perfTestOnly) {
                assertEquals("testSimpleEnum(): Incorrect value for inout param",
                             x.value(), y.value.value());
                assertEquals("testSimpleEnum(): Incorrect value for out param",
                             yOrig.value(), z.value.value());
                assertEquals("testSimpleEnum(): Incorrect return value",
                             x.value(), ret.value());
            }
        }
    }    
    
    public void testNMTokenEnum() throws Exception {
        String[] xx = {"hello", "there"};
        String[] yy = {"there", "hello"};

        Holder<NMTokenEnum> z = new Holder<NMTokenEnum>();

        for (int i = 0; i < 2; i++) {
            NMTokenEnum x = NMTokenEnum.fromValue(xx[i]);
            NMTokenEnum yOrig = NMTokenEnum.fromValue(yy[i]);
            Holder<NMTokenEnum> y = new Holder<NMTokenEnum>(yOrig);

            NMTokenEnum ret = client.testNMTokenEnum(x, y, z);

            if (!perfTestOnly) {
                assertEquals("testNMTokenEnum(): Incorrect value for inout param",
                             x.value(), y.value.value());
                assertEquals("testNMTokenEnum(): Incorrect value for out param",
                             yOrig.value(), z.value.value());
                assertEquals("testNMTokenEnum(): Incorrect return value",
                             x.value(), ret.value());
            }
        }
    }
    
    protected boolean equals(byte[] x, byte[] y) {
        String xx = new String(x);
        String yy = new String(y);
        return xx.equals(yy);
    }
    
}
