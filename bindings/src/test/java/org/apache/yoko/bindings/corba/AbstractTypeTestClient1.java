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
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.ws.Holder;

import org.apache.type_test.types1.AnonymousStruct;
import org.apache.type_test.types1.ArrayWithChoice;
import org.apache.type_test.types1.BinaryChoice;
import org.apache.type_test.types1.BinaryStruct;
import org.apache.type_test.types1.BoundedArray;
import org.apache.type_test.types1.ChoiceWithEnum;
import org.apache.type_test.types1.ChoiceWithStruct;
import org.apache.type_test.types1.CompoundArray;
import org.apache.type_test.types1.EmptyAll;
import org.apache.type_test.types1.EmptyChoice;
import org.apache.type_test.types1.EmptyStruct;
import org.apache.type_test.types1.FixedArray;
import org.apache.type_test.types1.NestedArray;
import org.apache.type_test.types1.NestedStruct;
import org.apache.type_test.types1.SequenceChoiceStruct;
import org.apache.type_test.types1.SequenceStructChoice;
import org.apache.type_test.types1.SimpleAll;
import org.apache.type_test.types1.SimpleChoice;
import org.apache.type_test.types1.SimpleEnum;
import org.apache.type_test.types1.SimpleStruct;
import org.apache.type_test.types1.StructWithChoice;
import org.apache.type_test.types1.StructWithEnum;
import org.apache.type_test.types1.StructWithNillables;
import org.apache.type_test.types1.UnboundedArray;

public abstract class AbstractTypeTestClient1 extends AbstractTypeTestClient {

    public AbstractTypeTestClient1(String name) {
        super(name);
    }

    protected <T> boolean equalsNilable(T x, T y) {
        if (x == null) {
            return y == null;
        } else if (y == null) {
            return false;
        } else {
            return x.equals(y);
        }
    }

    protected <T> boolean notNull(T x, T y) {
        return x != null && y != null;
    }

    //org.apache.type_test.types1.EmptyStruct
    
    public void testEmptyStruct() throws Exception {
        EmptyStruct x = new EmptyStruct();
        EmptyStruct yOrig = new EmptyStruct();
        Holder<EmptyStruct> y = new Holder<EmptyStruct>(yOrig);
        Holder<EmptyStruct> z = new Holder<EmptyStruct>();
        EmptyStruct ret = client.testEmptyStruct(x, y, z);

        if (!perfTestOnly) {
            assertTrue("testEmptyStruct(): Null value for inout param",
                       notNull(x, y.value));
            assertTrue("testEmptyStruct(): Null value for out param",
                       notNull(yOrig, z.value));
            assertTrue("testEmptyStruct(): Null return value", notNull(x, ret));
        }
    }
    
    //org.apache.type_test.types1.SimpleStruct

    protected boolean equals(SimpleStruct x, SimpleStruct y) {
        return (Double.compare(x.getVarFloat(), y.getVarFloat()) == 0)
            && (x.getVarInt().compareTo(y.getVarInt()) == 0)
            && (x.getVarString().equals(y.getVarString()));
        //&& (equalsNilable(x.getVarAttrString(), y.getVarAttrString()));
    }
    
    public void testSimpleStruct() throws Exception {
        SimpleStruct x = new SimpleStruct();
        x.setVarFloat(3.14f);
        x.setVarInt(new BigInteger("42"));
        x.setVarString("Hello There");

        SimpleStruct yOrig = new SimpleStruct();
        yOrig.setVarFloat(1.414f);
        yOrig.setVarInt(new BigInteger("13"));
        yOrig.setVarString("Cheerio");

        Holder<SimpleStruct> y = new Holder<SimpleStruct>(yOrig);
        Holder<SimpleStruct> z = new Holder<SimpleStruct>();
        SimpleStruct ret = client.testSimpleStruct(x, y, z);

        if (!perfTestOnly) {
            assertTrue("testSimpleStruct(): Incorrect value for inout param",
                       equals(x, y.value));
            assertTrue("testSimpleStruct(): Incorrect value for out param",
                       equals(yOrig, z.value));
            assertTrue("testSimpleStruct(): Incorrect return value", equals(x, ret));
        }
    }
    
    //org.apache.type_test.types1.StructWithNillables
    
    protected boolean equals(StructWithNillables x, StructWithNillables y) {
        return equalsNilable(x.getVarFloat(), y.getVarFloat())
            && equalsNilable(x.getVarInt(), x.getVarInt())
            && equalsNilable(x.getVarString(), y.getVarString())
            && equalsNilableStruct(x.getVarStruct(), y.getVarStruct());
    }
    
    public void testStructWithNillables() throws Exception {
        StructWithNillables x = new StructWithNillables();
        SimpleStruct structx = new SimpleStruct();
        structx.setVarFloat(3.14f);
        structx.setVarInt(new BigInteger("42"));
        structx.setVarString("Hello There");
        x.setVarStruct(structx);

        StructWithNillables yOrig = new StructWithNillables();
        yOrig.setVarFloat(new Float(1.414f));
        yOrig.setVarInt(new Integer(13));
        yOrig.setVarString("Cheerio");

        Holder<StructWithNillables> y = new Holder<StructWithNillables>(yOrig);
        Holder<StructWithNillables> z = new Holder<StructWithNillables>();
        StructWithNillables ret = client.testStructWithNillables(x, y, z);
        if (!perfTestOnly) {
            assertTrue("testStructWithNillables(): Incorrect value for inout param",
                       equals(x, y.value));
            assertTrue("testStructWithNillables(): Incorrect value for out param",
                       equals(yOrig, z.value));
            assertTrue("testStructWithNillables(): Incorrect return value", equals(x, ret));
        }
    }

    //org.apache.type_test.types1.NestedStruct

    protected boolean equals(NestedStruct x, NestedStruct y) {
        return (x.getVarInt() == y.getVarInt())
            && (x.getVarFloat().compareTo(y.getVarFloat()) == 0)
            && (x.getVarString().equals(y.getVarString()))
            && equalsNilable(x.getVarEmptyStruct(), y.getVarEmptyStruct())
            && equalsNilableStruct(x.getVarStruct(), y.getVarStruct());
    }

    protected boolean equalsNilable(EmptyStruct x, EmptyStruct y) {
        if (x == null) {
            return y == null;
        }
        return y != null;
    }

    protected boolean equalsNilableStruct(SimpleStruct x, SimpleStruct y) {
        if (x == null) {
            return y == null;
        } else if (y == null) {
            return false;
        } else {
            return equals(x, y);
        }
    }
    
    public void testNestedStruct() throws Exception {
        SimpleStruct xs = new SimpleStruct();
        xs.setVarFloat(30.14);
        xs.setVarInt(new BigInteger("420"));
        xs.setVarString("NESTED Hello There"); 
        NestedStruct x = new NestedStruct();
        x.setVarFloat(new BigDecimal("3.14"));
        x.setVarInt(42);
        x.setVarString("Hello There");
        x.setVarEmptyStruct(new EmptyStruct());
        x.setVarStruct(xs);

        SimpleStruct ys = new SimpleStruct();
        ys.setVarFloat(10.414);
        ys.setVarInt(new BigInteger("130"));
        ys.setVarString("NESTED Cheerio");

        NestedStruct yOrig = new NestedStruct();
        yOrig.setVarFloat(new BigDecimal("1.414"));
        yOrig.setVarInt(13);
        yOrig.setVarString("Cheerio");
        yOrig.setVarEmptyStruct(new EmptyStruct());
        yOrig.setVarStruct(ys);

        Holder<NestedStruct> y = new Holder<NestedStruct>(yOrig);
        Holder<NestedStruct> z = new Holder<NestedStruct>();
        NestedStruct ret = client.testNestedStruct(x, y, z);

        if (!perfTestOnly) {
            assertTrue("testNestedStruct(): Incorrect value for inout param",
                       equals(x, y.value));
            assertTrue("testNestedStruct(): Incorrect value for out param",
                       equals(yOrig, z.value));
            assertTrue("testNestedStruct(): Incorrect return value", equals(x, ret));
        }
    }
    
    //org.apache.type_test.types1.FixedArray
    
    public void testFixedArray() throws Exception {
        FixedArray x = new FixedArray();
        x.getItem().addAll(Arrays.asList(Integer.MIN_VALUE, 0, Integer.MAX_VALUE));

        FixedArray yOrig = new FixedArray();
        yOrig.getItem().addAll(Arrays.asList(-1, 0, 1));

        Holder<FixedArray> y = new Holder<FixedArray>(yOrig);
        Holder<FixedArray> z = new Holder<FixedArray>();
        FixedArray ret = client.testFixedArray(x, y, z);

        if (!perfTestOnly) {
            for (int i = 0; i < 3; i++) {
                assertEquals("testFixedArray(): Incorrect value for inout param",
                             x.getItem().get(i), y.value.getItem().get(i));
                assertEquals("testFixedArray(): Incorrect value for out param",
                             yOrig.getItem().get(i), z.value.getItem().get(i));
                assertEquals("testFixedArray(): Incorrect return value",
                             x.getItem().get(i), ret.getItem().get(i));
            }
        }
    }
    
    //org.apache.type_test.types1.BoundedArray
    
    public void testBoundedArray() throws Exception {
        BoundedArray x = new BoundedArray();
        x.getItem().addAll(Arrays.asList(-100.00f, 0f, 100.00f));
        BoundedArray yOrig = new BoundedArray();
        yOrig.getItem().addAll(Arrays.asList(-1f, 0f, 1f));

        Holder<BoundedArray> y = new Holder<BoundedArray>(yOrig);
        Holder<BoundedArray> z = new Holder<BoundedArray>();
        BoundedArray ret = client.testBoundedArray(x, y, z);

        if (!perfTestOnly) {
            float delta = 0.0f;

            int xSize = x.getItem().size(); 
            int ySize = y.value.getItem().size(); 
            int zSize = z.value.getItem().size(); 
            int retSize = ret.getItem().size(); 
            assertTrue("testBoundedArray() array size incorrect",
                       xSize == ySize && ySize == zSize && zSize == retSize && xSize == 3);
            for (int i = 0; i < xSize; i++) {
                assertEquals("testBoundedArray(): Incorrect value for inout param",
                             x.getItem().get(i), y.value.getItem().get(i), delta);
                assertEquals("testBoundedArray(): Incorrect value for out param",
                             yOrig.getItem().get(i), z.value.getItem().get(i), delta);
                assertEquals("testBoundedArray(): Incorrect return value",
                             x.getItem().get(i), ret.getItem().get(i), delta);
            }
        }
    }
    
    //org.apache.type_test.types1.UnboundedArray

    protected boolean equals(UnboundedArray x, UnboundedArray y) {
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
        return true;
    }
    
    public void testUnboundedArray() throws Exception {
        UnboundedArray x = new UnboundedArray();
        x.getItem().addAll(Arrays.asList("AAA", "BBB", "CCC"));
        UnboundedArray yOrig = new UnboundedArray();
        yOrig.getItem().addAll(Arrays.asList("XXX", "YYY", "ZZZ"));

        Holder<UnboundedArray> y = new Holder<UnboundedArray>(yOrig);
        Holder<UnboundedArray> z = new Holder<UnboundedArray>();
        UnboundedArray ret = client.testUnboundedArray(x, y, z);

        if (!perfTestOnly) {
            for (int i = 0; i < 3; i++) {
                assertTrue("testUnboundedArray(): Incorrect value for inout param", equals(x, y.value));
                assertTrue("testUnboundedArray(): Incorrect value for out param", equals(yOrig, z.value));
                assertTrue("testUnboundedArray(): Incorrect return value", equals(x, ret));
            }
        }
    }
    
    //org.apache.type_test.types1.CompoundArray
    
    protected boolean equals(CompoundArray x, CompoundArray y) {
        return x.getArray1().equals(y.getArray1())
            && x.getArray2().equals(y.getArray2());
    }
    
    public void testCompoundArray() throws Exception {
        CompoundArray x = new CompoundArray();
        x.getArray1().addAll(Arrays.asList("AAA", "BBB", "CCC"));
        x.getArray2().addAll(Arrays.asList("aaa", "bbb", "ccc"));

        CompoundArray yOrig = new CompoundArray();
        yOrig.getArray1().addAll(Arrays.asList("XXX", "YYY", "ZZZ"));
        yOrig.getArray2().addAll(Arrays.asList("xxx", "yyy", "zzz"));

        Holder<CompoundArray> y = new Holder<CompoundArray>(yOrig);
        Holder<CompoundArray> z = new Holder<CompoundArray>();
        CompoundArray ret = client.testCompoundArray(x, y, z);

        if (!perfTestOnly) {
            assertTrue("testCompoundArray(): Incorrect value for inout param",
                       equals(x, y.value));
            assertTrue("testCompoundArray(): Incorrect value for out param",
                       equals(yOrig, z.value));
            assertTrue("testCompoundArray(): Incorrect return value", equals(x, ret));
        }
    }

    //org.apache.type_test.types1.NestedArray
    
    public void testNestedArray() throws Exception {
        String[][] xs = {{"AAA", "BBB", "CCC"},
                         {"aaa", "bbb", "ccc"},
                         {"a_a_a", "b_b_b", "c_c_c"},
                         {"", "", ""}};
        String[][] ys = {{"XXX", "YYY", "ZZZ"},
                         {"xxx", "yyy", "zzz"},
                         {"x_x_x", "y_y_y", "z_z_z"},
                         {"", "", ""}};

        NestedArray x = new NestedArray();
        NestedArray yOrig = new NestedArray();

        List<UnboundedArray> xList = x.getSubarray();
        List<UnboundedArray> yList = yOrig.getSubarray();
        
        for (int i = 0; i < xs.length; i++) {
            UnboundedArray xx = new UnboundedArray();
            xx.getItem().addAll(Arrays.asList(xs[i]));
            xList.add(xx);
            UnboundedArray yy = new UnboundedArray();
            yy.getItem().addAll(Arrays.asList(ys[i]));
            yList.add(yy);
        }

        Holder<NestedArray> y = new Holder<NestedArray>(yOrig);
        Holder<NestedArray> z = new Holder<NestedArray>();
        NestedArray ret = client.testNestedArray(x, y, z);

        if (!perfTestOnly) {
            assertTrue("testNestedArray(): Incorrect value for inout param",
                       equals(x, y.value));
            assertTrue("testNestedArray(): Incorrect value for out param",
                       equals(yOrig, z.value));
            assertTrue("testNestedArray(): Incorrect return value",
                        equals(x, ret));

        }
    }
    
    protected boolean equals(AnonymousStruct x, AnonymousStruct y) {
        return (x.getVarFloat() == y.getVarFloat())
            && (x.getVarInt() == y.getVarInt())
            && (x.getVarString().equals(y.getVarString()));
    }
    
    public void testAnonymousStruct() throws Exception {
        AnonymousStruct x = new AnonymousStruct();
        x.setVarInt(100);
        x.setVarString("hello");
        x.setVarFloat(1.1f);

        AnonymousStruct yOrig = new AnonymousStruct();
        yOrig.setVarInt(11);
        yOrig.setVarString("world");
        yOrig.setVarFloat(10.1f);

        Holder<AnonymousStruct> y = new Holder<AnonymousStruct>(yOrig);
        Holder<AnonymousStruct> z = new Holder<AnonymousStruct>();
        AnonymousStruct ret = client.testAnonymousStruct(x, y, z);

        if (!perfTestOnly) {
            assertTrue("testAnonymousStruct(): Incorrect value for inout param",
                       equals(x, y.value));
            assertTrue("testAnonymousStruct(): Incorrect value for out param",
                       equals(yOrig, z.value));
            assertTrue("testAnonymousStruct(): Incorrect return value", equals(x, ret));
        }
    }

    //org.apache.type_test.types1.EmptyChoice

    public void testEmptyChoice() throws Exception {
        EmptyChoice x = new EmptyChoice();
        EmptyChoice yOrig = new EmptyChoice();
        Holder<EmptyChoice> y = new Holder<EmptyChoice>(yOrig);
        Holder<EmptyChoice> z = new Holder<EmptyChoice>();
        EmptyChoice ret = client.testEmptyChoice(x, y, z);
        if (!perfTestOnly) {
            assertTrue("testEmptyChoice(): Null value for inout param",
                       notNull(x, y.value));
            assertTrue("testEmptyChoice(): Null value for out param",
                       notNull(yOrig, z.value));
            assertTrue("testEmptyChoice(): Null return value", notNull(x, ret));
        }
    }
    
    //org.apache.type_test.types1.SimpleChoice
    
    protected boolean equals(SimpleChoice x, SimpleChoice y) {
        if (x.getVarFloat() != null && y.getVarFloat() != null) {
            return x.getVarFloat().compareTo(y.getVarFloat()) == 0;
        } else if (x.getVarInt() != null && y.getVarInt() != null) {
            return x.getVarInt().compareTo(y.getVarInt()) == 0;
        } else if (x.getVarString() != null && y.getVarString() != null) {
            return x.getVarString().equals(y.getVarString());
        } else {
            return false;
        }
    }

    public void testSimpleChoice() throws Exception {
        SimpleChoice x = new SimpleChoice();
        x.setVarFloat(-3.14f);
        SimpleChoice yOrig = new SimpleChoice();
        yOrig.setVarString("Cheerio");

        Holder<SimpleChoice> y = new Holder<SimpleChoice>(yOrig);
        Holder<SimpleChoice> z = new Holder<SimpleChoice>();

        SimpleChoice ret = client.testSimpleChoice(x, y, z);
        if (!perfTestOnly) {
            assertTrue("testSimpleChoice(): Incorrect value for inout param",
                       equals(x, y.value));
            assertTrue("testSimpleChoice(): Incorrect value for out param",
                       equals(yOrig, z.value));
            assertTrue("testSimpleChoice(): Incorrect return value", equals(x, ret));
        }
    }
    
    //org.apache.type_test.types1.EmptyAll

    public void testEmptyAll() throws Exception {
        EmptyAll x = new EmptyAll();
        EmptyAll yOrig = new EmptyAll();
        Holder<EmptyAll> y = new Holder<EmptyAll>(yOrig);
        Holder<EmptyAll> z = new Holder<EmptyAll>();
        EmptyAll ret = client.testEmptyAll(x, y, z);

        if (!perfTestOnly) {
            assertTrue("testEmptyAll(): Null value for inout param",
                       notNull(x, y.value));
            assertTrue("testEmptyAll(): Null value for out param",
                       notNull(yOrig, z.value));
            assertTrue("testEmptyAll(): Null return value", notNull(x, ret));
        }
    }
    
    //org.apache.type_test.types1.SimpleAll

    protected boolean equals(SimpleAll x, SimpleAll y) {
        return (x.getVarFloat() == y.getVarFloat())
            && (x.getVarInt() == y.getVarInt())
            && (x.getVarString().equals(y.getVarString()))
            && (x.getVarAttrString().equals(y.getVarAttrString()));
    }
    
    public void testSimpleAll() throws Exception {
        SimpleAll x = new SimpleAll();
        x.setVarFloat(3.14f);
        x.setVarInt(42);
        x.setVarString("Hello There");
        x.setVarAttrString("Attr-x");

        SimpleAll yOrig = new SimpleAll();
        yOrig.setVarFloat(-9.14f);
        yOrig.setVarInt(10);
        yOrig.setVarString("Cheerio");
        yOrig.setVarAttrString("Attr-y");

        Holder<SimpleAll> y = new Holder<SimpleAll>(yOrig);
        Holder<SimpleAll> z = new Holder<SimpleAll>();

        SimpleAll ret = client.testSimpleAll(x, y, z);

        if (!perfTestOnly) {
            assertTrue("testSimpleAll(): Incorrect value for inout param",
                       equals(x, y.value));
            assertTrue("testSimpleAll(): Incorrect value for out param",
                       equals(yOrig, z.value));
            assertTrue("testSimpleAll(): Incorrect return value", equals(x, ret));
        }
    }

    //org.apache.type_test.types1.SimpleStruct

    protected boolean equals(BinaryStruct x, BinaryStruct y) {
        return Arrays.equals(x.getVarBinary(), y.getVarBinary());
    }
    
    public void testBinaryStruct() throws Exception {
        BinaryStruct x = new BinaryStruct();
        byte[] bytes = "hello".getBytes();
        x.setVarBinary(bytes);

        BinaryStruct yOrig = new BinaryStruct();
        yOrig.setVarBinary("goodbye".getBytes());

        Holder<BinaryStruct> y = new Holder<BinaryStruct>(yOrig);
        Holder<BinaryStruct> z = new Holder<BinaryStruct>();
        BinaryStruct ret = client.testBinaryStruct(x, y, z);

        if (!perfTestOnly) {
            assertTrue("testBinaryStruct(): Incorrect value for inout param",
                       equals(x, y.value));
            assertTrue("testBinaryStruct(): Incorrect value for out param",
                       equals(yOrig, z.value));
            assertTrue("testBinaryStruct(): Incorrect return value", equals(x, ret));
        }
    }

    protected boolean equals(BinaryChoice x, BinaryChoice y) {
        if (x.getVarBinary() != null && y.getVarBinary() != null) {
            return Arrays.equals(x.getVarBinary(), y.getVarBinary());
        } else if (x.getVarString() != null && y.getVarString() != null) {
            return x.getVarString().equals(y.getVarString());
        } else {
            return false;
        }                                         
    }
    
    public void testBinaryChoice() throws Exception {
        BinaryChoice x = new BinaryChoice();
        byte[] bytes = "hello".getBytes();
        x.setVarBinary(bytes);

        BinaryChoice yOrig = new BinaryChoice();
        yOrig.setVarBinary("goodbye".getBytes());

        Holder<BinaryChoice> y = new Holder<BinaryChoice>(yOrig);
        Holder<BinaryChoice> z = new Holder<BinaryChoice>();
        BinaryChoice ret = client.testBinaryChoice(x, y, z);

        if (!perfTestOnly) {
            assertTrue("testBinaryChoice(): Incorrect value for inout param",
                       equals(x, y.value));
            assertTrue("testBinaryChoice(): Incorrect value for out param",
                       equals(yOrig, z.value));
            assertTrue("testBinaryChoice(): Incorrect return value", equals(x, ret));
        }
    }

    protected boolean equals(StructWithChoice x, StructWithChoice y) {
        return (x.getVarInt() == y.getVarInt())
            && x.getVarString().equals(y.getVarString())
            && equals(x.getVarChoice(), y.getVarChoice());
    }
    
    public void testStructWithChoice() throws Exception {
        StructWithChoice x = new StructWithChoice();

        SimpleChoice choicex = new SimpleChoice();
        choicex.setVarFloat(-3.14f);

        x.setVarInt(10);
        x.setVarString("Hello There");
        x.setVarChoice(choicex);

        StructWithChoice yOrig = new StructWithChoice();
        yOrig.setVarInt(13);
        yOrig.setVarString("Cheerio");
        SimpleChoice choicey = new SimpleChoice();
        choicey.setVarString("Cheerio");
        yOrig.setVarChoice(choicey);

        Holder<StructWithChoice> y = new Holder<StructWithChoice>(yOrig);
        Holder<StructWithChoice> z = new Holder<StructWithChoice>();
        StructWithChoice ret = client.testStructWithChoice(x, y, z);

        if (!perfTestOnly) {
            assertTrue("testStructWithChoice(): Incorrect value for inout param",
                       equals(x, y.value));
            assertTrue("testStructWithChoice(): Incorrect value for out param",
                       equals(yOrig, z.value));
            assertTrue("testStructWithChoice(): Incorrect return value", equals(x, ret));
        }
    }

    protected boolean equals(ChoiceWithStruct x, ChoiceWithStruct y) {
        if (x.getVarInt() != null && y.getVarInt() != null) {
            return x.getVarInt() == y.getVarInt();
        } else if (x.getVarString() != null && y.getVarString() != null) {
            return x.getVarString().equals(y.getVarString());
        } else if (x.getVarEmptyStruct() != null && y.getVarEmptyStruct() != null) {
            return notNull(x.getVarEmptyStruct(), y.getVarEmptyStruct());
        } else if (x.getVarStruct() != null && y.getVarStruct() != null) {
            return equals(x.getVarStruct(), y.getVarStruct());
        } else {
            return false;
        }
    }
    
    public void testChoiceWithStruct() throws Exception {
        ChoiceWithStruct x = new ChoiceWithStruct();
        SimpleStruct structx = new SimpleStruct();
        structx.setVarFloat(3.14f);
        structx.setVarInt(new BigInteger("42"));
        structx.setVarString("Hello There");
        x.setVarStruct(structx);

        ChoiceWithStruct yOrig = new ChoiceWithStruct();
        yOrig.setVarInt(13);

        Holder<ChoiceWithStruct> y = new Holder<ChoiceWithStruct>(yOrig);
        Holder<ChoiceWithStruct> z = new Holder<ChoiceWithStruct>();
        ChoiceWithStruct ret = client.testChoiceWithStruct(x, y, z);

        if (!perfTestOnly) {
            assertTrue("testChoiceWithStruct(): Incorrect value for inout param",
                       equals(x, y.value));
            assertTrue("testChoiceWithStruct(): Incorrect value for out param",
                       equals(yOrig, z.value));
            assertTrue("testChoiceWithStruct(): Incorrect return value", equals(x, ret));
        }
    }

    protected boolean equals(StructWithEnum x, StructWithEnum y) {
        return (x.getVarInt() == y.getVarInt())
            && x.getVarString().equals(y.getVarString())
            && x.getVarEnum().value().equals(y.getVarEnum().value());
    }
    
    public void testStructWithEnum() throws Exception {
        StructWithEnum x = new StructWithEnum();

        SimpleEnum enumx = SimpleEnum.fromValue("abc");

        x.setVarInt(10);
        x.setVarString("Hello There");
        x.setVarEnum(enumx);

        StructWithEnum yOrig = new StructWithEnum();
        yOrig.setVarInt(13);
        yOrig.setVarString("Cheerio");
        SimpleEnum enumy = SimpleEnum.fromValue("def");
        yOrig.setVarEnum(enumy);

        Holder<StructWithEnum> y = new Holder<StructWithEnum>(yOrig);
        Holder<StructWithEnum> z = new Holder<StructWithEnum>();
        StructWithEnum ret = client.testStructWithEnum(x, y, z);

        if (!perfTestOnly) {
            assertTrue("testStructWithEnum(): Incorrect value for inout param",
                       equals(x, y.value));
            assertTrue("testStructWithEnum(): Incorrect value for out param",
                       equals(yOrig, z.value));
            assertTrue("testStructWithEnum(): Incorrect return value", equals(x, ret));
        }
    }

    protected boolean equals(ChoiceWithEnum x, ChoiceWithEnum y) {
        if (x.getVarInt() != null && y.getVarInt() != null) {
            return x.getVarInt() == y.getVarInt();
        } else if (x.getVarString() != null && y.getVarString() != null) {
            return x.getVarString().equals(y.getVarString());
        } else if (x.getVarEnum() != null && y.getVarEnum() != null) {
            return x.getVarEnum().equals(y.getVarEnum());
        } else {
            return false;
        }
    }
    
    public void testChoiceWithEnum() throws Exception {
        ChoiceWithEnum x = new ChoiceWithEnum();
        SimpleEnum enumx = SimpleEnum.fromValue("abc");
        x.setVarEnum(enumx);

        ChoiceWithEnum yOrig = new ChoiceWithEnum();
        yOrig.setVarInt(13);

        Holder<ChoiceWithEnum> y = new Holder<ChoiceWithEnum>(yOrig);
        Holder<ChoiceWithEnum> z = new Holder<ChoiceWithEnum>();
        ChoiceWithEnum ret = client.testChoiceWithEnum(x, y, z);

        if (!perfTestOnly) {
            assertTrue("testChoiceWithEnum(): Incorrect value for inout param",
                       equals(x, y.value));
            assertTrue("testChoiceWithEnum(): Incorrect value for out param",
                       equals(yOrig, z.value));
            assertTrue("testChoiceWithEnum(): Incorrect return value", equals(x, ret));
        }
    }

    protected boolean equals(ArrayWithChoice x, ArrayWithChoice y) {
        List<SimpleChoice> xx = x.getSubarray();
        List<SimpleChoice> yy = y.getSubarray();
        if (xx.size() != yy.size()) {
            return false;
        }
        for (int i = 0; i < xx.size(); i++) {
            if (!equals(xx.get(i), yy.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    public void testArrayWithChoice() throws Exception {
        ArrayWithChoice x = new ArrayWithChoice();

        SimpleChoice choicex = new SimpleChoice();
        choicex.setVarFloat(-3.14f);

        x.getSubarray().add(choicex);

        ArrayWithChoice yOrig = new ArrayWithChoice();
        SimpleChoice choicey = new SimpleChoice();
        choicey.setVarString("Cheerio");
        yOrig.getSubarray().add(choicey);

        Holder<ArrayWithChoice> y = new Holder<ArrayWithChoice>(yOrig);
        Holder<ArrayWithChoice> z = new Holder<ArrayWithChoice>();
        ArrayWithChoice ret = client.testArrayWithChoice(x, y, z);

        if (!perfTestOnly) {
            assertTrue("testArrayWithChoice(): Incorrect value for inout param",
                       equals(x, y.value));
            assertTrue("testArrayWithChoice(): Incorrect value for out param",
                       equals(yOrig, z.value));
            assertTrue("testArrayWithChoice(): Incorrect return value", equals(x, ret));
        }
    }

    protected boolean equals(NestedArray x, NestedArray y) {
        List<UnboundedArray> xx = x.getSubarray();
        List<UnboundedArray> yy = y.getSubarray();
        if (xx.size() != yy.size()) {
            return false;
        }
        for (int i = 0; i < xx.size(); i++) {
            if (!equals(xx.get(i), yy.get(i))) {
                return false;
            }
        }
        return true;
    }

    protected boolean equals(SequenceChoiceStruct x, SequenceChoiceStruct y) {
        return (x.getVarInt() == y.getVarInt())
            && (x.getVarString().equals(y.getVarString()))
            && equals(x.getVarSequence(), y.getVarSequence())
            && equals(x.getVarChoice(), y.getVarChoice());
    }
    
    public void testSequenceChoiceStruct() throws Exception {
        SequenceChoiceStruct x = new SequenceChoiceStruct();
        x.setVarInt(45);
        x.setVarString("Cheerio");
        NestedArray arrayx = new NestedArray();
        UnboundedArray uarrayx = new UnboundedArray();
        uarrayx.getItem().add("AAA");
        arrayx.getSubarray().add(uarrayx);
        x.setVarSequence(arrayx);
        SimpleChoice choicex = new SimpleChoice();
        choicex.setVarFloat(-3.14f);
        x.setVarChoice(choicex);

        SequenceChoiceStruct yOrig = new SequenceChoiceStruct();
        yOrig.setVarInt(13);
        yOrig.setVarString("Hello There");
        NestedArray arrayy = new NestedArray();
        UnboundedArray uarrayy = new UnboundedArray();
        uarrayy.getItem().add("BBB");
        arrayy.getSubarray().add(uarrayy);
        yOrig.setVarSequence(arrayy);
        SimpleChoice choicey = new SimpleChoice();
        choicey.setVarString("Cheerio");
        yOrig.setVarChoice(choicex);

        Holder<SequenceChoiceStruct> y = new Holder<SequenceChoiceStruct>(yOrig);
        Holder<SequenceChoiceStruct> z = new Holder<SequenceChoiceStruct>();
        SequenceChoiceStruct ret = client.testSequenceChoiceStruct(x, y, z);

        if (!perfTestOnly) {
            assertTrue("testSequenceChoiceStruct(): Incorrect value for inout param",
                       equals(x, y.value));
            assertTrue("testSequenceChoiceStruct(): Incorrect value for out param",
                       equals(yOrig, z.value));
            assertTrue("testSequenceChoiceStruct(): Incorrect return value", equals(x, ret));
        }
    }

    protected boolean equals(SequenceStructChoice x, SequenceStructChoice y) {
        if (x.getVarString() != null && y.getVarString() != null) {
            return x.getVarString().equals(y.getVarString());
        } else if (x.getVarStruct() != null && y.getVarStruct() != null) {
            return equals(x.getVarStruct(), y.getVarStruct());
        } else if (x.getVarSequence() != null && y.getVarSequence() != null) {
            return equals(x.getVarSequence(), y.getVarSequence());
        } else {
            return false;
        }
    }
    
    public void testSequenceStructChoice() throws Exception {
        SequenceStructChoice x = new SequenceStructChoice();
        NestedArray arrayx = new NestedArray();
        UnboundedArray uarrayx = new UnboundedArray();
        uarrayx.getItem().add("AAA");
        arrayx.getSubarray().add(uarrayx);
        x.setVarSequence(arrayx);

        SequenceStructChoice yOrig = new SequenceStructChoice();
        SimpleStruct ys = new SimpleStruct();
        ys.setVarFloat(30.14);
        ys.setVarInt(new BigInteger("420"));
        ys.setVarString("NESTED Hello There"); 
        NestedStruct nestedy = new NestedStruct();
        nestedy.setVarFloat(new BigDecimal("3.14"));
        nestedy.setVarInt(42);
        nestedy.setVarString("Hello There");
        nestedy.setVarEmptyStruct(new EmptyStruct());
        nestedy.setVarStruct(ys);
        yOrig.setVarStruct(nestedy);

        Holder<SequenceStructChoice> y = new Holder<SequenceStructChoice>(yOrig);
        Holder<SequenceStructChoice> z = new Holder<SequenceStructChoice>();
        SequenceStructChoice ret = client.testSequenceStructChoice(x, y, z);

        if (!perfTestOnly) {
            assertTrue("testSequenceStructChoice(): Incorrect value for inout param",
                       equals(x, y.value));
            assertTrue("testSequenceStructChoice(): Incorrect value for out param",
                       equals(yOrig, z.value));
            assertTrue("testSequenceStructChoice(): Incorrect return value", equals(x, ret));
        }
    }

    public void testNillableStruct() throws Exception {
        SimpleStruct x = new SimpleStruct();
        x.setVarFloat(3.14f);
        x.setVarInt(new BigInteger("42"));
        x.setVarString("Hello There");

        SimpleStruct yOrig = null;
        Holder<SimpleStruct> y = new Holder<SimpleStruct>(yOrig);
        Holder<SimpleStruct> z = new Holder<SimpleStruct>();
        SimpleStruct ret = client.testNillableStruct(x, y, z);

        if (!perfTestOnly) {
            assertTrue("testNillableStruct(): Incorrect value for inout param",
                       equalsNilableStruct(x, y.value));
            assertTrue("testNillableStruct(): Incorrect value for out param",
                       equalsNilableStruct(yOrig, z.value));
            assertTrue("testNillableStruct(): Incorrect return value", equalsNilableStruct(x, ret));
        }

        x = null;

        yOrig = new SimpleStruct();
        yOrig.setVarFloat(1.414f);
        yOrig.setVarInt(new BigInteger("13"));
        yOrig.setVarString("Cheerio");

        y = new Holder<SimpleStruct>(yOrig);
        z = new Holder<SimpleStruct>();
        ret = client.testNillableStruct(x, y, z);

        if (!perfTestOnly) {
            assertTrue("testNillableStruct(): Incorrect value for inout param",
                       equalsNilableStruct(x, y.value));
            assertTrue("testNillableStruct(): Incorrect value for out param",
                       equalsNilableStruct(yOrig, z.value));
            assertTrue("testNillableStruct(): Incorrect return value", equalsNilableStruct(x, ret));
        }
    }

}
