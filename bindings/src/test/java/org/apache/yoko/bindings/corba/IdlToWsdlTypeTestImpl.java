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

import javax.jws.WebService;

import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTest;
import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTestEnum1;
import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTestRecursiveStruct;
import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTestRecursiveUnion;
import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTestSeqLong;
import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTestStruct1;
import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTestStructSet;
import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTestUnion1;
import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTestUnion2;
import org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTestUnionSet;

@WebService(serviceName = "idltowsdlTypeTestCORBAService", portName = "idltowsdlTypeTestCORBAPort",
            endpointInterface = "org.apache.schemas.yoko.idl.idltowsdl_type_test.IdltowsdlTypeTest",
            targetNamespace = "http://schemas.apache.org/yoko/idl/idltowsdl_type_test")
public class IdlToWsdlTypeTestImpl implements IdltowsdlTypeTest {
    
        
    
    public java.lang.String greetMe(java.lang.String name) {
        return new String("Hallo there " + name);
    }

    // base       

    public int testAlias(int inDuration,
                         javax.xml.ws.Holder<java.lang.Integer> inoutDuration,
                         javax.xml.ws.Holder<java.lang.Integer> outDuration) {
        outDuration.value = inoutDuration.value;
        inoutDuration.value = inDuration;
        return inDuration;
    }
    
    public float testFloat(float inFloat,
                           javax.xml.ws.Holder<java.lang.Float> inoutFloat,
                           javax.xml.ws.Holder<java.lang.Float> outFloat) {
        outFloat.value = inoutFloat.value;
        inoutFloat.value = inFloat;
        return inFloat;
    }

    public double testDouble(double inDouble,
                             javax.xml.ws.Holder<java.lang.Double> inoutDouble,
                             javax.xml.ws.Holder<java.lang.Double> outDouble) {
        outDouble.value = inoutDouble.value;
        inoutDouble.value = inDouble;
        return inDouble;
    }

    public short testShort(short inShort,
                           javax.xml.ws.Holder<java.lang.Short> inoutShort,
                           javax.xml.ws.Holder<java.lang.Short> outShort) {
        outShort.value = inoutShort.value;
        inoutShort.value = inShort;
        return inShort;
    }


    public int testLong(int inLong,
                        javax.xml.ws.Holder<java.lang.Integer> inoutLong,
                        javax.xml.ws.Holder<java.lang.Integer> outLong) {
        outLong.value = inoutLong.value;
        inoutLong.value = inLong;
        return inLong;
    }

    
    public long testLongLong(long inLongLong,
                             javax.xml.ws.Holder<java.lang.Long> inoutLongLong,
                             javax.xml.ws.Holder<java.lang.Long> outLongLong) {
        outLongLong.value = inoutLongLong.value;
        inoutLongLong.value = inLongLong;
        return inLongLong;            
    }

    public int testUnsignedShort(int inUnsignedShort,
                                 javax.xml.ws.Holder<java.lang.Integer> inoutUnsignedShort,
                                 javax.xml.ws.Holder<java.lang.Integer> outUnsignedShort) {
        outUnsignedShort.value = inoutUnsignedShort.value;
        inoutUnsignedShort.value = inUnsignedShort;
        return inUnsignedShort;
    }

    public long testUnsignedLong(long inUnsignedLong,
                                 javax.xml.ws.Holder<java.lang.Long> inoutUnsignedLong,
                                 javax.xml.ws.Holder<java.lang.Long> outUnsignedLong) {
        outUnsignedLong.value = inoutUnsignedLong.value;
        inoutUnsignedLong.value = inUnsignedLong;
        return inUnsignedLong;
    }

    public java.math.BigInteger testUnsignedLongLong(java.math.BigInteger inUnsignedLongLong,
                                                     javax.xml.ws.Holder<java.math.BigInteger> inoutUnsignedLongLong,
                                                     javax.xml.ws.Holder<java.math.BigInteger> outUnsignedLongLong) {
        outUnsignedLongLong.value = inoutUnsignedLongLong.value;
        inoutUnsignedLongLong.value = inUnsignedLongLong;
        return inUnsignedLongLong;
    }

    public byte testChar(byte inChar,
                         javax.xml.ws.Holder<java.lang.Byte> inoutChar,
                         javax.xml.ws.Holder<java.lang.Byte> outChar) {
        outChar.value = inoutChar.value;
        inoutChar.value = inChar;
        return inChar;
    }

    public java.lang.String testWchar(java.lang.String inWchar,
                                      javax.xml.ws.Holder<java.lang.String> inoutWchar,
                                      javax.xml.ws.Holder<java.lang.String> outWchar) {
        outWchar.value = inoutWchar.value;
        inoutWchar.value = inWchar;
        return inWchar;
    }

    public boolean testBoolean(boolean inBoolean,
                               javax.xml.ws.Holder<java.lang.Boolean> inoutBoolean,
                               javax.xml.ws.Holder<java.lang.Boolean> outBoolean) {
        outBoolean.value = inoutBoolean.value;
        inoutBoolean.value = inBoolean;
        return inBoolean;
    }


    public short testOctet(short inOctet,
                           javax.xml.ws.Holder<java.lang.Short> inoutOctet,
                           javax.xml.ws.Holder<java.lang.Short> outOctet) {
        outOctet.value = inoutOctet.value;
        inoutOctet.value = inOctet;
        return inOctet;
    }


    public java.lang.Object testAny(java.lang.Object inAny,
                                    javax.xml.ws.Holder<java.lang.Object> inoutAny,
                                    javax.xml.ws.Holder<java.lang.Object> outAny) {
        outAny.value = inoutAny.value;
        inoutAny.value = inAny;
        return inAny;
    }

    
    
    // template
    
    public IdltowsdlTypeTestSeqLong testSeqLong(IdltowsdlTypeTestSeqLong inSeqLong,
                                                javax.xml.ws.Holder<IdltowsdlTypeTestSeqLong> inoutSeqLong,
                                                javax.xml.ws.Holder<IdltowsdlTypeTestSeqLong> outSeqLong) {
        outSeqLong.value = inoutSeqLong.value;
        inoutSeqLong.value = inSeqLong;  
        return inSeqLong;
    }

    public java.lang.String testString(java.lang.String inString,
                                       javax.xml.ws.Holder<java.lang.String> inoutString,
                                       javax.xml.ws.Holder<java.lang.String> outString) {
        outString.value = inoutString.value;
        inoutString.value = inString;
        return inString;
    }

    public java.lang.String testWstring(java.lang.String inWstring,
                                        javax.xml.ws.Holder<java.lang.String> inoutWstring,
                                        javax.xml.ws.Holder<java.lang.String> outWstring) {
        outWstring.value = inoutWstring.value;
        inoutWstring.value = inWstring;
        return inWstring;
    }

    public java.math.BigDecimal testFixedPt(java.math.BigDecimal inFixedPt,
                                            javax.xml.ws.Holder<java.math.BigDecimal> inoutFixedPt,
                                            javax.xml.ws.Holder<java.math.BigDecimal> outFixedPt) {
        outFixedPt.value = inoutFixedPt.value;
        inoutFixedPt.value = inFixedPt;
        return inFixedPt;
    }

    
    // constr
    
    public IdltowsdlTypeTestStruct1 testStruct(IdltowsdlTypeTestStruct1 inStruct1,
                                               javax.xml.ws.Holder<IdltowsdlTypeTestStruct1> inoutStruct1,
                                               javax.xml.ws.Holder<IdltowsdlTypeTestStruct1> outStruct1) {
        outStruct1.value = inoutStruct1.value;
        inoutStruct1.value = inStruct1;
        return inStruct1;
    }

    public IdltowsdlTypeTestUnion1 testUnion(IdltowsdlTypeTestUnion1 inUnion1,
                                             javax.xml.ws.Holder<IdltowsdlTypeTestUnion1> inoutUnion1,
                                             javax.xml.ws.Holder<IdltowsdlTypeTestUnion1> outUnion1) {
        outUnion1.value = inoutUnion1.value;
        inoutUnion1.value = inUnion1;
        return inUnion1;
    }

    public IdltowsdlTypeTestEnum1 testEnum(IdltowsdlTypeTestEnum1 inEnum1,
                                           javax.xml.ws.Holder<IdltowsdlTypeTestEnum1> inoutEnum1,
                                           javax.xml.ws.Holder<IdltowsdlTypeTestEnum1> outEnum1) {
        outEnum1.value = inoutEnum1.value;
        inoutEnum1.value = inEnum1;
        return inEnum1;
    }

    public IdltowsdlTypeTestUnionSet testUnionSet(IdltowsdlTypeTestUnionSet inUnionSet,
                                                  javax.xml.ws.Holder<IdltowsdlTypeTestUnionSet> inoutUnionSet,
                                                  javax.xml.ws.Holder<IdltowsdlTypeTestUnionSet> outUnionSet) {
        outUnionSet.value = inoutUnionSet.value;
        inoutUnionSet.value = inUnionSet;
        return inUnionSet;
    }

    public IdltowsdlTypeTestUnion2 testUnionVariants(IdltowsdlTypeTestUnion2 inUnion2,
                                                     javax.xml.ws.Holder<IdltowsdlTypeTestUnion2> inoutUnion2,
                                                     javax.xml.ws.Holder<IdltowsdlTypeTestUnion2> outUnion2) {
        outUnion2.value = inoutUnion2.value;
        inoutUnion2.value = inUnion2;
        return inUnion2;
    }

    public java.lang.String testAnonString(java.lang.String inAnonString,
                                           javax.xml.ws.Holder<java.lang.String> inoutAnonString,
                                           javax.xml.ws.Holder<java.lang.String> outAnonString) {
        outAnonString.value = inoutAnonString.value;
        inoutAnonString.value = inAnonString;
        return inAnonString;
    }

    public java.lang.String testAnonWstring(java.lang.String inAnonWstring,
                                            javax.xml.ws.Holder<java.lang.String> inoutAnonWstring,
                                            javax.xml.ws.Holder<java.lang.String> outAnonWstring) {
        outAnonWstring.value = inoutAnonWstring.value;
        inoutAnonWstring.value = inAnonWstring;
        return inAnonWstring;
    }

    public IdltowsdlTypeTestRecursiveStruct testRecursiveStruct(IdltowsdlTypeTestRecursiveStruct inStruct,
                                        javax.xml.ws.Holder<IdltowsdlTypeTestRecursiveStruct> inoutStruct,
                                        javax.xml.ws.Holder<IdltowsdlTypeTestRecursiveStruct> outStruct) {
        outStruct.value = inoutStruct.value;
        inoutStruct.value = inStruct;
        return inStruct;
    }

    public IdltowsdlTypeTestRecursiveUnion testRecursiveUnion(IdltowsdlTypeTestRecursiveUnion inUnion,
                                        javax.xml.ws.Holder<IdltowsdlTypeTestRecursiveUnion> inoutUnion,
                                        javax.xml.ws.Holder<IdltowsdlTypeTestRecursiveUnion> outUnion) {
        outUnion.value = inoutUnion.value;
        inoutUnion.value = inUnion;
        return inUnion;
    }
}
