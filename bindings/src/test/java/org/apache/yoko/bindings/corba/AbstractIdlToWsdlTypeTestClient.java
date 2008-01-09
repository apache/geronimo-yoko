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

import java.util.List;

import javax.xml.ws.Holder;

import junit.framework.TestCase;

import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTest;
import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTestEnum1;
import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTestEnumSet;
import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTestRecursiveStruct;
import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTest1RecursiveStruct;
import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTestRecursiveUnion;
import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTest1RecursiveUnion;
import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTestSeqLong;
import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTestStringSet;
import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTestStruct1;
import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTestStruct2;
import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTestStructSet;
import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTestUnion1;
import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTestUnion2;
import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTestUnionSet;

import org.apache.yoko.orb.CORBA.Any;

public abstract class AbstractIdlToWsdlTypeTestClient extends TestCase {

    protected static org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTest client;
    
    AbstractIdlToWsdlTypeTestClient(String name) {
        super(name);
    }
    
    public void testGreetMe() {
        String name = new String("Partner");
        String ret = client.greetMe(name);
        String expected = "Hallo there " + name;
        assertTrue(ret.equals(expected));
    }
    
    ////
    // base_type
    public void testAlias() throws Exception {
        int valueSets[][] = {
            {10, 100},
            {1000, 2000}
        };

        for (int i = 0; i < valueSets.length; i++) {
            int in = valueSets[i][0];
            Holder<Integer> inoutOrig = new Holder<Integer>(valueSets[i][1]);
            Holder<Integer> inout = new Holder<Integer>(valueSets[i][1]);
            Holder<Integer> out = new Holder<Integer>();

            long ret = client.testAlias(in, inout, out);
            
            assertEquals("testAlias(): Incorrect value for out param", inoutOrig.value, out.value);
            assertEquals("testAlias(): Incorrect value for inout param", Integer.valueOf(in), inout.value);
            assertEquals("testAlias(): Incorrect return value", in, ret);
        }
    }

    
    public void testFloat() {
        float valueSets[][] = {
            {0, 0},                               
            {Float.parseFloat("-1.1"), 0},
            {0, Float.parseFloat("1.1")},
            {Float.MIN_VALUE, Float.MAX_VALUE},
            {Float.MAX_VALUE, Float.MIN_VALUE},
        };
        
        for (int i = 0; i < valueSets.length; i++) {
            float in = valueSets[i][0];
            Holder<Float> inoutOrig = new Holder<Float>(valueSets[i][1]);
            Holder<Float> inout = new Holder<Float>(valueSets[i][1]);
            Holder<Float> out = new Holder<Float>();

            float ret = client.testFloat(in, inout, out);

            assertEquals("testFloat(): Incorrect value for out param", inoutOrig.value, out.value);
            assertEquals("testFloat(): Incorrect value for inout param", Float.valueOf(in), inout.value);
            assertEquals("testFloat(): Incorrect return value", in, ret);        
        }
    }
    
    public void testDouble() {
        double valueSets[][] = {
            {0, 0},                               
            {Double.parseDouble("-1.1"), 0},
            {0, Double.parseDouble("1.1")},
            {Double.MIN_VALUE, Double.MAX_VALUE},
            {Double.MAX_VALUE, Double.MIN_VALUE}
        };
        
        for (int i = 0; i < valueSets.length; i++) {
            double in = valueSets[i][0];
            Holder<Double> inoutOrig = new Holder<Double>(valueSets[i][1]);
            Holder<Double> inout = new Holder<Double>(valueSets[i][1]);
            Holder<Double> out = new Holder<Double>();

            double ret = client.testDouble(in, inout, out);

            assertEquals("testDouble(): Incorrect value for out param", inoutOrig.value, out.value);
            assertEquals("testDouble(): Incorrect value for inout param", Double.valueOf(in), inout.value);
            assertEquals("testDouble(): Incorrect return value", in, ret);        
        }
    }

    public void testShort() throws Exception {
        short valueSets[][] = {
            {0, 0},                   
            {0, 1},
            {-1, 0},
            {Short.MIN_VALUE, Short.MAX_VALUE},
            {Short.MAX_VALUE, Short.MIN_VALUE},
        };

        for (int i = 0; i < valueSets.length; i++) {
            short in = valueSets[i][0];
            Holder<Short> inoutOrig = new Holder<Short>(valueSets[i][1]);
            Holder<Short> inout = new Holder<Short>(valueSets[i][1]);
            Holder<Short> out = new Holder<Short>();

            short ret = client.testShort(in, inout, out);
            
            assertEquals("testShort(): Incorrect value for out param", inoutOrig.value, out.value);
            assertEquals("testShort(): Incorrect value for inout param", Short.valueOf(in), inout.value);
            assertEquals("testShort(): Incorrect return value", in, ret);
        }
    }

    public void testLong() throws Exception {
        int valueSets[][] = {
            {0, 0},                   
            {0, 1},
            {-1, 0},
            {Integer.MIN_VALUE, Integer.MAX_VALUE},
            {Integer.MAX_VALUE, Integer.MIN_VALUE}
        };

        for (int i = 0; i < valueSets.length; i++) {
            int in = valueSets[i][0];
            Holder<Integer> inoutOrig = new Holder<Integer>(valueSets[i][1]);
            Holder<Integer> inout = new Holder<Integer>(valueSets[i][1]);
            Holder<Integer> out = new Holder<Integer>();

            long ret = client.testLong(in, inout, out);
            
            assertEquals("testLong(): Incorrect value for out param", inoutOrig.value, out.value);
            assertEquals("testLong(): Incorrect value for inout param", Integer.valueOf(in), inout.value);
            assertEquals("testLong(): Incorrect return value", in, ret);
        }
    }

    public void testLongLong() throws Exception {
        long valueSets[][] = {
            {0, 0},                   
            {0, 1},
            {-1, 0},
            {Long.MIN_VALUE, Long.MAX_VALUE},
            {Long.MAX_VALUE, Long.MIN_VALUE}
        };

        for (int i = 0; i < valueSets.length; i++) {
            long in = valueSets[i][0];
            Holder<Long> inoutOrig = new Holder<Long>(valueSets[i][1]);
            Holder<Long> inout = new Holder<Long>(valueSets[i][1]);
            Holder<Long> out = new Holder<Long>();

            long ret = client.testLongLong(in, inout, out);
            
            assertEquals("testLongLong(): Incorrect value for out param", inoutOrig.value, out.value);
            assertEquals("testLongLong(): Incorrect value for inout param", Long.valueOf(in), inout.value);
            assertEquals("testLongLong(): Incorrect return value", in, ret);
        }
    }

    public void testUnsignedShort() throws Exception {
        int valueSets[][] = {
            {0, 0},                   
            {0, 1},
            {0, Integer.parseInt("16384")},
            {0, Integer.parseInt("32767")},
            {0, Integer.parseInt("32768")},
            {0, Integer.parseInt("65535")}
            //{0, Integer.parseInt("65536")}  // should fail here
        };
        
        for (int i = 0; i < valueSets.length; i++) {
            int in = valueSets[i][0];
            Holder<Integer> inoutOrig = new Holder<Integer>(valueSets[i][1]);
            Holder<Integer> inout = new Holder<Integer>(valueSets[i][1]);
            Holder<Integer> out = new Holder<Integer>();

            int ret = client.testUnsignedShort(in, inout, out);
            
            assertEquals("testUnsignedShort(): Incorrect value for out param", inoutOrig.value, out.value);
            assertEquals("testUnsignedShort(): Incorrect value for inout param", Integer.valueOf(in), inout.value);
            assertEquals("testUnsignedShort(): Incorrect return value", in, ret);
        }
    }

    public void testUnsignedLong() throws Exception {
        Double MAX = Math.pow(2, 32);
        long MAX_UNSIGNED_LONG = MAX.longValue() - 1;
        long valueSets[][] = {
            {0, 0},                   
            {0, 1},
            {0, MAX_UNSIGNED_LONG / 2},
            {0, MAX_UNSIGNED_LONG * 5 / 8},
            {0, MAX_UNSIGNED_LONG * 3 / 4},
            {0, MAX_UNSIGNED_LONG - 1},
            {0, MAX_UNSIGNED_LONG}
        };

        for (int i = 0; i < valueSets.length; i++) {
            long in = valueSets[i][0];
            Holder<Long> inoutOrig = new Holder<Long>(valueSets[i][1]);
            Holder<Long> inout = new Holder<Long>(valueSets[i][1]);
            Holder<Long> out = new Holder<Long>();

            long ret = client.testUnsignedLong(in, inout, out);
            
            assertEquals("testUnsignedLong(): Incorrect value for out param", inoutOrig.value, out.value);
            assertEquals("testUnsignedLong(): Incorrect value for inout param", Long.valueOf(in), inout.value);
            assertEquals("testUnsignedLong(): Incorrect return value", in, ret);
        }
    }

    public void testUnsignedLongLong() throws Exception {
        java.math.BigInteger valueSets[][] = {
            {java.math.BigInteger.ZERO, java.math.BigInteger.ZERO},                   
            {java.math.BigInteger.ZERO, java.math.BigInteger.ONE},
            {java.math.BigInteger.ONE, java.math.BigInteger.ZERO},
            {java.math.BigInteger.ZERO, java.math.BigInteger.valueOf(Long.MAX_VALUE)},
            {java.math.BigInteger.valueOf(Long.MAX_VALUE), java.math.BigInteger.ZERO}
        };
        
        for (int i = 0; i < valueSets.length; i++) {
            java.math.BigInteger in = valueSets[i][0];
            Holder<java.math.BigInteger> inoutOrig = new Holder<java.math.BigInteger>(valueSets[i][1]);
            Holder<java.math.BigInteger> inout = new Holder<java.math.BigInteger>(valueSets[i][1]);
            Holder<java.math.BigInteger> out = new Holder<java.math.BigInteger>();

            java.math.BigInteger ret = client.testUnsignedLongLong(in, inout, out);
            
            assertEquals("testUnsignedLongLong(): Incorrect value for out param", inoutOrig.value, out.value);
            assertEquals("testUnsignedLongLong(): Incorrect value for inout param", in, inout.value);
            assertEquals("testUnsignedLongLong(): Incorrect return value", in, ret);
        }
    }

    public void testChar() throws Exception {
        byte valueSets[][] = {
            {0, 0},
            {0, 1},
            {1, 0},
            {0, Byte.MAX_VALUE},
            {'a', 'z'},
            {0, Byte.MIN_VALUE},
            {Byte.MIN_VALUE, Byte.MAX_VALUE}
        };
        
        for (int i = 0; i < valueSets.length; i++) {
            byte in = valueSets[i][0];
            Holder<Byte> inoutOrig = new Holder<Byte>(valueSets[i][1]);
            Holder<Byte> inout = new Holder<Byte>(valueSets[i][1]);
            Holder<Byte> out = new Holder<Byte>();

            byte ret = client.testChar(in, inout, out);
            
            assertEquals("testChar(): Incorrect value for out param", inoutOrig.value, out.value);
            assertEquals("testChar(): Incorrect value for inout param", Byte.valueOf(in), inout.value);
            assertEquals("testChar(): Incorrect return value", in, ret);
        }
    }

    public void testWchar() throws Exception {
        String valueSets[][] = {
            {"a", "b"},                   
            {"b", "a"},
            {"a", "z"},
            {"0", "y"}
        };

        for (int i = 0; i < valueSets.length; i++) {
            String in = valueSets[i][0];
            Holder<String> inoutOrig = new Holder<String>(valueSets[i][1]);
            Holder<String> inout = new Holder<String>(valueSets[i][1]);
            Holder<String> out = new Holder<String>();

            String ret = client.testWchar(in, inout, out);
            
            assertEquals("testWchar(): Incorrect value for out param", inoutOrig.value, out.value);
            assertEquals("testWchar(): Incorrect value for inout param", in, inout.value);
            assertEquals("testWchar(): Incorrect return value", in, ret);
        }
    }

    public void testBoolean() throws Exception {
        boolean valueSets[][] = {
            {false, false},                   
            {false, true},
            {true, false},
            {true, true}
        };

        for (int i = 0; i < valueSets.length; i++) {
            boolean in = valueSets[i][0];
            Holder<Boolean> inoutOrig = new Holder<Boolean>(valueSets[i][1]);
            Holder<Boolean> inout = new Holder<Boolean>(valueSets[i][1]);
            Holder<Boolean> out = new Holder<Boolean>();

            boolean ret = client.testBoolean(in, inout, out);
            
            assertEquals("testBoolean(): Incorrect value for out param", inoutOrig.value, out.value);
            assertEquals("testBoolean(): Incorrect value for inout param", Boolean.valueOf(in), inout.value);
            assertEquals("testBoolean(): Incorrect return value", in, ret);
        }
    }

    public void testOctet() throws Exception {
        short valueSets[][] = {
            {0, 0},                   
            {0, 1},
            {-1, 0},
            {Short.MIN_VALUE, Short.MAX_VALUE},
            {Short.MAX_VALUE, Short.MIN_VALUE}
        };

        for (int i = 0; i < valueSets.length; i++) {
            short in = valueSets[i][0];
            Holder<Short> inoutOrig = new Holder<Short>(valueSets[i][1]);
            Holder<Short> inout = new Holder<Short>(valueSets[i][1]);
            Holder<Short> out = new Holder<Short>();

            short ret = client.testOctet(in, inout, out);
            
            assertEquals("testOctet(): Incorrect value for out param", inoutOrig.value, out.value);
            assertEquals("testOctet(): Incorrect value for inout param", Short.valueOf(in), inout.value);
            assertEquals("testOctet(): Incorrect return value", in, ret);
        }
    }

    public boolean compareAny(Object left, Object right) {
        boolean result = false;
        if (left instanceof Boolean) {
            if (right instanceof Boolean) {
                Boolean l = (Boolean)left;
                Boolean r = (Boolean)right;
                result = l.equals(r);
            } else {
                result = false;
            }
        } else if (left instanceof IdltowsdlTypeTestStruct1) {
            if (right instanceof IdltowsdlTypeTestStruct1) {
                IdltowsdlTypeTestStruct1 l = (IdltowsdlTypeTestStruct1)left;
                IdltowsdlTypeTestStruct1 r = (IdltowsdlTypeTestStruct1)right;
                result = l.getStruct1Long() == r.getStruct1Long();
                result |= l.getStruct1Short() == r.getStruct1Short();
            } else {
                result = false;
            }
        } else if (left instanceof String) {
            if (right instanceof String) {
                String l = (String)left;
                String r = (String)right;
                result = l.equals(r);
            } else {
                result = false;
            }
        } else {
            result = false;
        }
        return result;
    }


    public void testAny() throws Exception {
        Boolean bool = new Boolean(true);
        IdltowsdlTypeTestStruct1 struct = new IdltowsdlTypeTestStruct1();
        struct.setStruct1Long(123);
        struct.setStruct1Short((short) 456);
        String str = new String("Hello");

        Object valueSets[][] = {
            {bool, bool},
            {bool, struct},
            {struct, bool},
            {struct, str},
            {struct, struct},
            {str, str}
        };

        for (int i = 0; i < valueSets.length; i++) {
            Object in = valueSets[i][0];
            Holder<Object> inoutOrig = new Holder<Object>(valueSets[i][1]);
            Holder<Object> inout = new Holder<Object>(valueSets[i][1]);
            Holder<Object> out = new Holder<Object>();
            
            Object ret = client.testAny(in, inout, out);
            assertTrue("testAny(): Incorrect value for out param", compareAny(inoutOrig.value, out.value));
            assertTrue("testAny(): Incorrect value for inout param", compareAny(in, inout.value));
            assertTrue("testAny(): Incorrect value for return", compareAny(in, ret));
        }
    }

    ////
    // template_type_spec

    public void testSeqLong() {
        IdltowsdlTypeTestSeqLong emptySeqLong = new IdltowsdlTypeTestSeqLong();
        IdltowsdlTypeTestSeqLong zeroSeqLong = new IdltowsdlTypeTestSeqLong();
        zeroSeqLong.getItem().add(0);
        IdltowsdlTypeTestSeqLong singleSeqLong = new IdltowsdlTypeTestSeqLong();
        singleSeqLong.getItem().add(Integer.MAX_VALUE);
        IdltowsdlTypeTestSeqLong doubleSeqLong = new IdltowsdlTypeTestSeqLong();
        doubleSeqLong.getItem().add(Integer.MAX_VALUE);
        doubleSeqLong.getItem().add(Integer.MIN_VALUE);
        IdltowsdlTypeTestSeqLong tripleSeqLong = new IdltowsdlTypeTestSeqLong();
        tripleSeqLong.getItem().add(Integer.MIN_VALUE);
        tripleSeqLong.getItem().add(0);
        tripleSeqLong.getItem().add(Integer.MAX_VALUE);
        IdltowsdlTypeTestSeqLong bigSeqLong = new IdltowsdlTypeTestSeqLong();
        final int BIG_SEQ_LONG_SIZE = 1000;
        for (int i = 0; i < BIG_SEQ_LONG_SIZE; i++) {
            bigSeqLong.getItem().add(i);
        }
        
        
        assertTrue(emptySeqLong.equals(emptySeqLong));
        IdltowsdlTypeTestSeqLong anotherEmptySeqLong = new IdltowsdlTypeTestSeqLong();
        assertTrue(emptySeqLong.getItem().equals(anotherEmptySeqLong.getItem()));
        
        IdltowsdlTypeTestSeqLong valueSets[][] = {
            {emptySeqLong, emptySeqLong},
            {emptySeqLong, zeroSeqLong},
            {zeroSeqLong, singleSeqLong},
            {singleSeqLong, doubleSeqLong},
            {doubleSeqLong, tripleSeqLong},
            {tripleSeqLong, bigSeqLong},
            {bigSeqLong, emptySeqLong}
        };

        for (int i = 0; i < valueSets.length; i++) {
            IdltowsdlTypeTestSeqLong in = valueSets[i][0];
            Holder<IdltowsdlTypeTestSeqLong> inoutOrig = new Holder<IdltowsdlTypeTestSeqLong>(valueSets[i][1]);
            Holder<IdltowsdlTypeTestSeqLong> inout = new Holder<IdltowsdlTypeTestSeqLong>(valueSets[i][1]);
            Holder<IdltowsdlTypeTestSeqLong> out = new Holder<IdltowsdlTypeTestSeqLong>();

            IdltowsdlTypeTestSeqLong ret = client.testSeqLong(in, inout, out);

            assertTrue("testSeqLong(): Incorrect value for out param", inoutOrig.value.getItem().equals(out.value.getItem()));
            assertTrue("testSeqLong(): Incorrect value for inout param", in.getItem().equals(inout.value.getItem()));
            assertTrue("testSeqLong(): Incorrect return value", in.getItem().equals(ret.getItem()));
        }
    }

    public void testString() {
        String empty = new String("");
        String foo = new String("foo");
        String bar = new String("bar");
        String foobar = new String("foobar");
        StringBuffer strBuf = new StringBuffer();
        for (int i = 0; i < 1024; i++) {
            strBuf.append(i);
        }
        String bigString = strBuf.toString();
        
        String valueSets[][] = {
            {empty, empty},
            {empty, foo},
            {foo, bar},
            {bar, foobar},
            {foobar, foobar},
            {foobar, bigString},
            {bigString, bigString}
        };
        
        for (int i = 0; i < valueSets.length; i++) {
            String in = valueSets[i][0];
            Holder<String> inoutOrig = new Holder<String>(valueSets[i][1]);
            Holder<String> inout = new Holder<String>(valueSets[i][1]);
            Holder<String> out = new Holder<String>();

            String ret = client.testString(in, inout, out);

            assertTrue("testString(): Incorrect value for out param", inoutOrig.value.equals(out.value));
            assertTrue("testString(): Incorrect value for inout param", in.equals(inout.value));
            assertTrue("testString(): Incorrect return value", in.equals(ret));
        }        
    }

    public void testWstring() {
        String empty = new String("");
        String foo = new String("foo");
        String bar = new String("bar");
        String foobar = new String("foobar");
        StringBuffer strBuf = new StringBuffer();
        for (int i = 0; i < 1024; i++) {
            strBuf.append(i);
        }
        String bigString = strBuf.toString();
        
        String valueSets[][] = {
            {empty, empty},
            {empty, foo},
            {foo, bar},
            {bar, foobar},
            {foobar, foobar},
            {foobar, bigString},
            {bigString, bigString}
        };
        
        for (int i = 0; i < valueSets.length; i++) {
            String in = valueSets[i][0];
            Holder<String> inoutOrig = new Holder<String>(valueSets[i][1]);
            Holder<String> inout = new Holder<String>(valueSets[i][1]);
            Holder<String> out = new Holder<String>();

            String ret = client.testWstring(in, inout, out);

            assertTrue("testWstring(): Incorrect value for out param", inoutOrig.value.equals(out.value));
            assertTrue("testWstring(): Incorrect value for inout param", in.equals(inout.value));
            assertTrue("testWstring(): Incorrect return value", in.equals(ret));
        }                
    }

    public void testFixedPt() {
        double d0 = 0.01;
        double d1 = 1.01;
        double d2 = 123.12;
        double d3 = 1234567890.12;
        java.math.BigDecimal valueSets[][] = {
            {java.math.BigDecimal.valueOf(d0), java.math.BigDecimal.valueOf(d0)},
            {java.math.BigDecimal.valueOf(d1), java.math.BigDecimal.valueOf(d0)},
            {java.math.BigDecimal.valueOf(d1), java.math.BigDecimal.valueOf(d2)},
            {java.math.BigDecimal.valueOf(d2), java.math.BigDecimal.valueOf(d1)},
            {java.math.BigDecimal.valueOf(d2), java.math.BigDecimal.valueOf(d3)}
        };
        
        for (int i = 0; i < valueSets.length; i++) {
            java.math.BigDecimal in = valueSets[i][0];
            Holder<java.math.BigDecimal> inoutOrig = new Holder<java.math.BigDecimal>(valueSets[i][1]);
            Holder<java.math.BigDecimal> inout = new Holder<java.math.BigDecimal>(valueSets[i][1]);
            Holder<java.math.BigDecimal> out = new Holder<java.math.BigDecimal>();

            java.math.BigDecimal ret = client.testFixedPt(in, inout, out);

            assertTrue("testFixedPt(): Incorrect value for out param", inoutOrig.value.equals(out.value));
            assertTrue("testFixedPt(): Incorrect value for inout param", in.equals(inout.value));
            assertTrue("testFixedPt(): Incorrect return value", in.equals(ret));
        }                
    }

    ////
    // constr_type_spec

    public void testStruct() {
        IdltowsdlTypeTestStruct1 s0 = new IdltowsdlTypeTestStruct1();
        s0.setStruct1Long(0);
        s0.setStruct1Short((short) 0);
        IdltowsdlTypeTestStruct1 s1 = new IdltowsdlTypeTestStruct1();
        s1.setStruct1Long(1);
        s1.setStruct1Short((short) 1);
        IdltowsdlTypeTestStruct1 s2 = new IdltowsdlTypeTestStruct1();
        s2.setStruct1Long(Integer.MAX_VALUE);
        s2.setStruct1Short(Short.MAX_VALUE);
        
        IdltowsdlTypeTestStruct1 valueSets[][] = {
            {s0, s0},
            {s0, s1},
            {s1, s0},
            {s1, s2},
            {s2, s2}
        };
        
        for (int i = 0; i < valueSets.length; i++) {
            IdltowsdlTypeTestStruct1 in = valueSets[i][0];
            Holder<IdltowsdlTypeTestStruct1> inoutOrig = new Holder<IdltowsdlTypeTestStruct1>(valueSets[i][1]);
            Holder<IdltowsdlTypeTestStruct1> inout = new Holder<IdltowsdlTypeTestStruct1>(valueSets[i][1]);
            Holder<IdltowsdlTypeTestStruct1> out = new Holder<IdltowsdlTypeTestStruct1>();

            IdltowsdlTypeTestStruct1 ret = client.testStruct(in, inout, out);

            assertEquals("testStruct(): Incorrect value for out param", inoutOrig.value.getStruct1Long(), out.value.getStruct1Long());
            assertEquals("testStruct(): Incorrect value for out param", inoutOrig.value.getStruct1Short(), out.value.getStruct1Short());
            assertEquals("testStruct(): Incorrect value for inout param", in.getStruct1Long() ,inout.value.getStruct1Long());
            assertEquals("testStruct(): Incorrect value for inout param", in.getStruct1Short() ,inout.value.getStruct1Short());
            assertEquals("testStruct(): Incorrect return value", in.getStruct1Long(), ret.getStruct1Long());
            assertEquals("testStruct(): Incorrect return value", in.getStruct1Short(), ret.getStruct1Short());
        }                        
    }

    public void testUnion() {
        IdltowsdlTypeTestUnion1 u0 = new IdltowsdlTypeTestUnion1();
        u0.setU11(Integer.MAX_VALUE);
        IdltowsdlTypeTestUnion1 u1 = new IdltowsdlTypeTestUnion1();
        u1.setU12(new String("foo"));
        IdltowsdlTypeTestUnion1 u2 = new IdltowsdlTypeTestUnion1();
        u2.setU12(new String("bar"));
        IdltowsdlTypeTestUnion1 u3 = new IdltowsdlTypeTestUnion1();
        u3.setU13(Short.MAX_VALUE);
        
        IdltowsdlTypeTestUnion1 valueSets[][] = {
            {u0, u0},
            {u0, u1},
            {u1, u2},
            {u2, u3}
        };
        
        for (int i = 0; i < valueSets.length; i++) {
            IdltowsdlTypeTestUnion1 in = valueSets[i][0];
            Holder<IdltowsdlTypeTestUnion1> inoutOrig = new Holder<IdltowsdlTypeTestUnion1>(valueSets[i][1]);
            Holder<IdltowsdlTypeTestUnion1> inout = new Holder<IdltowsdlTypeTestUnion1>(valueSets[i][1]);
            Holder<IdltowsdlTypeTestUnion1> out = new Holder<IdltowsdlTypeTestUnion1>();

            IdltowsdlTypeTestUnion1 ret = client.testUnion(in, inout, out);

            assertTrue("testUnion(): Incorrect value for out param", compareUnion(inoutOrig.value, out.value));
            assertTrue("testUnion(): Incorrect value for inout param", compareUnion(in,inout.value));
            assertTrue("testUnion(): Incorrect return value", compareUnion(in, ret));
        }                        
    }

    private boolean compareUnion(IdltowsdlTypeTestUnion1 left, IdltowsdlTypeTestUnion1 right) {
        boolean result = true;
        if (left.getU11() != null) {
            result = left.getU11().equals(right.getU11());
        } else if (left.getU12() != null) {
            result = left.getU12().equals(right.getU12());
        } else {
            result = left.getU13().equals(right.getU13());
        }
        return result;
    }
    
    public void testEnum() {
        IdltowsdlTypeTestEnum1 valueSets[][] = {
            {IdltowsdlTypeTestEnum1.E_1_1, IdltowsdlTypeTestEnum1.E_1_1},
            {IdltowsdlTypeTestEnum1.E_1_1, IdltowsdlTypeTestEnum1.E_1_2},
            {IdltowsdlTypeTestEnum1.E_1_2, IdltowsdlTypeTestEnum1.E_1_3},
            {IdltowsdlTypeTestEnum1.E_1_3, IdltowsdlTypeTestEnum1.E_1_3},            
        };
        
        for (int i = 0; i < valueSets.length; i++) {
            IdltowsdlTypeTestEnum1 in = valueSets[i][0];
            Holder<IdltowsdlTypeTestEnum1> inoutOrig = new Holder<IdltowsdlTypeTestEnum1>(valueSets[i][1]);
            Holder<IdltowsdlTypeTestEnum1> inout = new Holder<IdltowsdlTypeTestEnum1>(valueSets[i][1]);
            Holder<IdltowsdlTypeTestEnum1> out = new Holder<IdltowsdlTypeTestEnum1>();

            IdltowsdlTypeTestEnum1 ret = client.testEnum(in, inout, out);

            assertEquals("testEnum(): Incorrect value for out param", inoutOrig.value, out.value);
            assertEquals("testEnum(): Incorrect value for inout param", in,inout.value);
            assertEquals("testEnum(): Incorrect return value", in, ret);
        }
    }

    public void testUnionSet() {
        IdltowsdlTypeTestUnion1 u0 = new IdltowsdlTypeTestUnion1();
        u0.setU11(Integer.MAX_VALUE);
        IdltowsdlTypeTestUnion1 u1 = new IdltowsdlTypeTestUnion1();
        u1.setU12(new String("foo"));
        IdltowsdlTypeTestUnion1 u2 = new IdltowsdlTypeTestUnion1();
        u2.setU12(new String("bar"));
        IdltowsdlTypeTestUnion1 u3 = new IdltowsdlTypeTestUnion1();
        u3.setU13(Short.MAX_VALUE);

        IdltowsdlTypeTestUnionSet s0 = new IdltowsdlTypeTestUnionSet();
        s0.getItem().add(u0);
        s0.getItem().add(u1);
        IdltowsdlTypeTestUnionSet s1 = new IdltowsdlTypeTestUnionSet();        
        s1.getItem().add(u2);
        IdltowsdlTypeTestUnionSet s2 = new IdltowsdlTypeTestUnionSet();
        s2.getItem().add(u3);
        
        IdltowsdlTypeTestUnionSet valueSets[][] = {
            {s0, s0},
            {s0, s1},
            {s1, s0},
            {s1, s2},
            {s2, s2}
        };
        
        for (int i = 0; i < valueSets.length; i++) {
            IdltowsdlTypeTestUnionSet in = valueSets[i][0];
            Holder<IdltowsdlTypeTestUnionSet> inoutOrig = new Holder<IdltowsdlTypeTestUnionSet>(valueSets[i][1]);
            Holder<IdltowsdlTypeTestUnionSet> inout = new Holder<IdltowsdlTypeTestUnionSet>(valueSets[i][1]);
            Holder<IdltowsdlTypeTestUnionSet> out = new Holder<IdltowsdlTypeTestUnionSet>();

            IdltowsdlTypeTestUnionSet ret = client.testUnionSet(in, inout, out);

            assertTrue("testUnionSet(): Incorrect value for out param", compareUnionSet(inoutOrig.value,
                                                                                        out.value));
            assertTrue("testUnionSet(): Incorrect value for inout param", compareUnionSet(in,
                                                                                          inout.value));
            assertTrue("testUnionSet(): Incorrect return value", compareUnionSet(in,
                                                                                 ret));
        }                    
    }

    private boolean compareUnionSet(IdltowsdlTypeTestUnionSet x, IdltowsdlTypeTestUnionSet y) {
        List<IdltowsdlTypeTestUnion1> xx = x.getItem();
        List<IdltowsdlTypeTestUnion1> yy = y.getItem();
        if (xx.size() != yy.size()) {
            return false;
        }
        for (int i = 0; i < xx.size(); i++) {
            if (!compareUnion(xx.get(i), yy.get(i))) {
                return false;
            }
        }
        return true;
    }


    public void testUnionVariants() {
        IdltowsdlTypeTestUnion1 u0 = new IdltowsdlTypeTestUnion1();
        u0.setU11(Integer.MAX_VALUE);
        IdltowsdlTypeTestUnion2 u20 = new IdltowsdlTypeTestUnion2();
        u20.setU21(u0);
        IdltowsdlTypeTestUnion2 u21 = new IdltowsdlTypeTestUnion2();
        u21.setU22(IdltowsdlTypeTestEnum1.E_1_1);
        IdltowsdlTypeTestUnionSet unionSet = new IdltowsdlTypeTestUnionSet();
        unionSet.getItem().add(u0);
        IdltowsdlTypeTestUnion2 u22 = new IdltowsdlTypeTestUnion2();
        u22.setU23(unionSet);
        IdltowsdlTypeTestEnumSet enumSet = new IdltowsdlTypeTestEnumSet();
        enumSet.getItem().add(IdltowsdlTypeTestEnum1.E_1_2);
        IdltowsdlTypeTestUnion2 u23 = new IdltowsdlTypeTestUnion2();
        u23.setU24(enumSet);
        IdltowsdlTypeTestStringSet stringSet = new IdltowsdlTypeTestStringSet();
        stringSet.getItem().add("Hello There");
        IdltowsdlTypeTestUnion2 u24 = new IdltowsdlTypeTestUnion2();
        u24.setU25(stringSet);
        IdltowsdlTypeTestStruct1 struct1 = new IdltowsdlTypeTestStruct1();
        struct1.setStruct1Long(Integer.MAX_VALUE);
        struct1.setStruct1Short(Short.MAX_VALUE);
        IdltowsdlTypeTestStruct2 struct2 = new IdltowsdlTypeTestStruct2();
        struct2.setStruct2Long(10);
        struct2.setStruct2Struct(struct1);
        IdltowsdlTypeTestStructSet structSet = new IdltowsdlTypeTestStructSet();
        structSet.getItem().add(struct2);
        IdltowsdlTypeTestUnion2 u25 = new IdltowsdlTypeTestUnion2();
        u25.setU26(structSet);
        
        IdltowsdlTypeTestUnion2 valueSets[][] = {
            {u20, u21},
            {u21, u22},
            {u22, u23},
            {u23, u24},
            {u24, u25}
        };
        
        for (int i = 0; i < valueSets.length; i++) {
            IdltowsdlTypeTestUnion2 in = valueSets[i][0];
            Holder<IdltowsdlTypeTestUnion2> inoutOrig = new Holder<IdltowsdlTypeTestUnion2>(valueSets[i][1]);
            Holder<IdltowsdlTypeTestUnion2> inout = new Holder<IdltowsdlTypeTestUnion2>(valueSets[i][1]);
            Holder<IdltowsdlTypeTestUnion2> out = new Holder<IdltowsdlTypeTestUnion2>();

            IdltowsdlTypeTestUnion2 ret = client.testUnionVariants(in, inout, out);
            assertTrue("testUnionVariants(): Incorrect value for out param", compareUnionVariants(inoutOrig.value, out.value));
            assertTrue("testUnionVariants(): Incorrect value for inout param", compareUnionVariants(in,inout.value));
            assertTrue("testUnionVariants(): Incorrect return value", compareUnionVariants(in, ret));
        }                        
    }

    private boolean compareUnionVariants(IdltowsdlTypeTestUnion2 left, IdltowsdlTypeTestUnion2 right) {
        boolean result = false;
        if ((left.getU21() != null) && (right.getU21() != null)) {
            result = compareUnion(left.getU21(), right.getU21());
        } else if ((left.getU22() != null) && (right.getU22() != null)) {
            result = left.getU22().value().equals(right.getU22().value());
        } else if ((left.getU23() != null) && (right.getU23() != null)) {
            result = compareUnionSet(left.getU23(), right.getU23());
        } else if ((left.getU24() != null) && (right.getU24() != null)) {
            result = compareEnumSet(left.getU24(), right.getU24());
        } else if ((left.getU25() != null) && (right.getU25() != null)) {
            result = compareStringSet(left.getU25(), right.getU25());
        } else {
            result = compareStructSet(left.getU26(), right.getU26());
        }
        return result;
    }

    private boolean compareStructSet(IdltowsdlTypeTestStructSet x, IdltowsdlTypeTestStructSet y) {
        boolean result = false;
        List<IdltowsdlTypeTestStruct2> xx = x.getItem();
        List<IdltowsdlTypeTestStruct2> yy = y.getItem();
        if (xx.size() != yy.size()) {
            return result;
        }
        for (int i = 0; i < xx.size(); i++) {
            IdltowsdlTypeTestStruct2 structx = xx.get(i);
            IdltowsdlTypeTestStruct2 structy = yy.get(i);
            if ((structx.getStruct2Long() == structy.getStruct2Long())
                && (structx.getStruct2Struct().getStruct1Long() == structy.getStruct2Struct().getStruct1Long())
                && (structx.getStruct2Struct().getStruct1Short() == structy.getStruct2Struct().getStruct1Short())) {
                result = true;
            }
        }
        return result;
    }

    private boolean compareEnumSet(IdltowsdlTypeTestEnumSet x, IdltowsdlTypeTestEnumSet y) {
        boolean result = true;
        List<IdltowsdlTypeTestEnum1> xx = x.getItem();
        List<IdltowsdlTypeTestEnum1> yy = y.getItem();
        if (xx.size() != yy.size()) {
            return false;
        }
        for (int i = 0; i < xx.size(); i++) {
            if (!xx.get(i).value().equals(yy.get(i).value())) {
                return false;
            }
        }
        return result;
    }

    private boolean compareStringSet(IdltowsdlTypeTestStringSet x, IdltowsdlTypeTestStringSet y) {
        boolean result = true;
        List<String> xx = x.getItem();
        List<String> yy = y.getItem();
        if (xx.size() != yy.size()) {
            return false;
        }
        for (int i = 0; i < xx.size(); i++) {
            if (!xx.get(i).equals(yy.get(i))) {
                return false;
            }
        }
        return result;
    }

    public void testAnonString() {
        String empty = new String("");
        String allo = new String("Allo");
        String yoko = new String("Yoko");
        String test = new String("Test");
        
        String valueSets[][] = {
            {empty, empty},
            {empty, allo},
            {allo, yoko},
            {yoko, test},
            {test, test}
        };
        
        for (int i = 0; i < valueSets.length; i++) {
            String in = valueSets[i][0];
            Holder<String> inoutOrig = new Holder<String>(valueSets[i][1]);
            Holder<String> inout = new Holder<String>(valueSets[i][1]);
            Holder<String> out = new Holder<String>();

            String ret = client.testAnonString(in, inout, out);

            assertTrue("testAnonString(): Incorrect value for out param", 
                       inoutOrig.value.equals(out.value));
            assertTrue("testAnonString(): Incorrect value for inout param", in.equals(inout.value));
            assertTrue("testAnonString(): Incorrect return value", in.equals(ret));
        }        
    }

    public void testAnonWstring() {
        String empty = new String("");
        String allo = new String("Allo");
        String yoko = new String("Yoko");
        String test = new String("Test");
        
        String valueSets[][] = {
            {empty, empty},
            {empty, allo},
            {allo, yoko},
            {yoko, test},
            {test, test}
        };
        
        for (int i = 0; i < valueSets.length; i++) {
            String in = valueSets[i][0];
            Holder<String> inoutOrig = new Holder<String>(valueSets[i][1]);
            Holder<String> inout = new Holder<String>(valueSets[i][1]);
            Holder<String> out = new Holder<String>();

            String ret = client.testAnonWstring(in, inout, out);

            assertTrue("testAnonWstring(): Incorrect value for out param", 
                        inoutOrig.value.equals(out.value));
            assertTrue("testAnonWstring(): Incorrect value for inout param", in.equals(inout.value));
            assertTrue("testAnonWstring(): Incorrect return value", in.equals(ret));
        }                
    }

    private boolean compareRecursiveStructs(IdltowsdlTypeTestRecursiveStruct left, 
                                            IdltowsdlTypeTestRecursiveStruct right) {
        boolean result = true;

        // Make sure the are the same name
        if (!(left.getName().equals(right.getName()))) {
            result = false;
        }

        // Make sure we have the same number of children
        if (!(left.getChildren().getItem().size() == right.getChildren().getItem().size())) {
            result = false;
        } else {
            // Compare each of the children
            for (int i = 0; i < left.getChildren().getItem().size(); ++i) {
                IdltowsdlTypeTestRecursiveStruct leftChild = left.getChildren().getItem().get(i);
                IdltowsdlTypeTestRecursiveStruct rightChild = right.getChildren().getItem().get(i);
                boolean compareChildren = compareRecursiveStructs(leftChild, rightChild);
                if (!compareChildren) {
                    result = false;
                    break; 
                }
            }
        }

        return result;
    }

    public void testRecursiveStruct() {
        // Recursive struct with no children
        IdltowsdlTypeTestRecursiveStruct rStructNoChild1 = new IdltowsdlTypeTestRecursiveStruct();
        rStructNoChild1.setName("RecursiveStruct1");
        rStructNoChild1.setChildren(new IdltowsdlTypeTest1RecursiveStruct());

        // Recursive struct with no children
        IdltowsdlTypeTestRecursiveStruct rStructNoChild2 = new IdltowsdlTypeTestRecursiveStruct();
        rStructNoChild2.setName("RecursiveStruct2");
        rStructNoChild2.setChildren(new IdltowsdlTypeTest1RecursiveStruct());

        // Recursive struct with no children
        IdltowsdlTypeTestRecursiveStruct rStructNoChild3 = new IdltowsdlTypeTestRecursiveStruct();
        rStructNoChild3.setName("RecursiveStruct3");
        rStructNoChild3.setChildren(new IdltowsdlTypeTest1RecursiveStruct());

        // Recursive struct with a single recursive child
        IdltowsdlTypeTestRecursiveStruct rStructSingleChild = new IdltowsdlTypeTestRecursiveStruct();
        rStructSingleChild.setName("RecursiveStructSingleChild");
        IdltowsdlTypeTest1RecursiveStruct rStructSingleChildChildren = 
            new IdltowsdlTypeTest1RecursiveStruct();
        rStructSingleChildChildren.getItem().add(rStructNoChild1);
        rStructSingleChild.setChildren(rStructSingleChildChildren);

        // Recursive struct with two recursive children
        IdltowsdlTypeTestRecursiveStruct rStructDoubleChild = new IdltowsdlTypeTestRecursiveStruct();
        rStructDoubleChild.setName("RecursiveStructDoubleChild");
        IdltowsdlTypeTest1RecursiveStruct rStructDoubleChildChildren = 
            new IdltowsdlTypeTest1RecursiveStruct();
        rStructDoubleChildChildren.getItem().add(rStructNoChild2);
        rStructDoubleChildChildren.getItem().add(rStructNoChild3);
        rStructDoubleChild.setChildren(rStructDoubleChildChildren);

        // Recursive struct with a child which contains a reference to a recursive child
        IdltowsdlTypeTestRecursiveStruct rStructNestedChildren = new IdltowsdlTypeTestRecursiveStruct();
        rStructNestedChildren.setName("RecursiveStructNestedChildren");
        IdltowsdlTypeTest1RecursiveStruct rStructNestedChildrenChildren =
                        new IdltowsdlTypeTest1RecursiveStruct();
        rStructNestedChildrenChildren.getItem().add(rStructSingleChild);
        rStructNestedChildren.setChildren(rStructNestedChildrenChildren);

        IdltowsdlTypeTestRecursiveStruct valueSets[][] = {
            {rStructNoChild1, rStructNoChild1},
            {rStructSingleChild, rStructSingleChild},
            {rStructDoubleChild, rStructDoubleChild},
            {rStructNestedChildren, rStructNestedChildren}
        };

        for (int i = 0; i < valueSets.length; i++) {
            IdltowsdlTypeTestRecursiveStruct in = valueSets[i][0];
            Holder<IdltowsdlTypeTestRecursiveStruct> inoutOrig = 
                new Holder<IdltowsdlTypeTestRecursiveStruct>(valueSets[i][1]);
            Holder<IdltowsdlTypeTestRecursiveStruct> inout = 
                new Holder<IdltowsdlTypeTestRecursiveStruct>(valueSets[i][1]);
            Holder<IdltowsdlTypeTestRecursiveStruct> out = 
                new Holder<IdltowsdlTypeTestRecursiveStruct>();

            IdltowsdlTypeTestRecursiveStruct ret = client.testRecursiveStruct(in, inout, out);

            assertTrue("testRecursiveStruct(): Incorrect value for out param", 
                        compareRecursiveStructs(inoutOrig.value, out.value));
            assertTrue("testRecursiveStruct(): Incorrect value for inout param", 
                        compareRecursiveStructs(in, inout.value));
            assertTrue("testRecursiveStruct(): Incorrect return value", 
                        compareRecursiveStructs(in, ret));
        }                
    }

    private boolean compareRecursiveUnions(IdltowsdlTypeTestRecursiveUnion left,
                                           IdltowsdlTypeTestRecursiveUnion right) {
        boolean result = false;

        if (left.getCase1() != null && right.getCase1() != null) {
            result = left.getCase1().equals(right.getCase1());
        } else if (left.getCaseDef() != null && right.getCaseDef() != null) {
            result = left.getCaseDef().equals(right.getCaseDef());
        } else if (left.getCase2() != null && right.getCase2() != null) {
            if (left.getCase2().getItem().size() == right.getCase2().getItem().size()) {
                for (int i = 0; i < left.getCase2().getItem().size(); i++) {
                    IdltowsdlTypeTestRecursiveUnion l = left.getCase2().getItem().get(i);
                    IdltowsdlTypeTestRecursiveUnion r = right.getCase2().getItem().get(i);
                    result |= compareRecursiveUnions(l, r);
                }
            }
        }

        return result;
    }

    public void testRecursiveUnion() {
        // Union, no recursive children, case 1 set
        IdltowsdlTypeTestRecursiveUnion rUnion1 = new IdltowsdlTypeTestRecursiveUnion();
        rUnion1.setCase1("TestUnion");

        // Union, no recursive children, default case set
        IdltowsdlTypeTestRecursiveUnion rUnion2 = new IdltowsdlTypeTestRecursiveUnion();
        rUnion2.setCaseDef(27);

        // Union, two recursive children, case 2 set
        IdltowsdlTypeTestRecursiveUnion rUnion3 = new IdltowsdlTypeTestRecursiveUnion();
        IdltowsdlTypeTest1RecursiveUnion ru3Children = new IdltowsdlTypeTest1RecursiveUnion();
        ru3Children.getItem().add(rUnion1);
        ru3Children.getItem().add(rUnion2);
        rUnion3.setCase2(ru3Children);

        // Union, one recursive child with two nested recursive children, case 2 set
        IdltowsdlTypeTestRecursiveUnion rUnion4 = new IdltowsdlTypeTestRecursiveUnion();
        IdltowsdlTypeTest1RecursiveUnion ru4Children = new IdltowsdlTypeTest1RecursiveUnion();
        ru4Children.getItem().add(rUnion3);
        rUnion4.setCase2(ru4Children);

        IdltowsdlTypeTestRecursiveUnion valueSets[][] = {
            {rUnion1, rUnion2},
            {rUnion2, rUnion3},
            {rUnion3, rUnion4},
            {rUnion4, rUnion1}
        };

        for (int i = 0; i < valueSets.length; i++) {
            IdltowsdlTypeTestRecursiveUnion in = valueSets[i][0];
            Holder<IdltowsdlTypeTestRecursiveUnion> inoutOrig = 
                new Holder<IdltowsdlTypeTestRecursiveUnion>(valueSets[i][1]);
            Holder<IdltowsdlTypeTestRecursiveUnion> inout = 
                new Holder<IdltowsdlTypeTestRecursiveUnion>(valueSets[i][1]);
            Holder<IdltowsdlTypeTestRecursiveUnion> out =
                new Holder<IdltowsdlTypeTestRecursiveUnion>();

            IdltowsdlTypeTestRecursiveUnion ret = client.testRecursiveUnion(in, inout, out);

            assertTrue("testRecursiveUnion(): Incorrect value for out param", 
                        compareRecursiveUnions(inoutOrig.value, out.value));
            assertTrue("testRecursiveUnion(): Incorrect value for inout param", 
                        compareRecursiveUnions(in, inout.value));
            assertTrue("testRecursiveUnion(): Incorrect return value", 
                        compareRecursiveUnions(in, ret));
        }                
    }
}
