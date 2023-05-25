/*
 * Copyright 2023 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.apache.yoko;

import acme.PRODUCT_NOT_EXIST;
import acme.PRODUCT_NOT_EXISTHelper;
import acme.Product;
import acme.ProductHelper;
import acme.ProductListHelper;
import org.apache.yoko.orb.CORBA.OutputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.portable.InputStream;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static testify.hex.HexParser.HEX_STRING;

public class IdlStructIOTest {
    private OutputStream out;

    @BeforeEach
    void createOutputStream() { out = new OutputStream(); }

    @AfterEach
    void closeOutputStream() { try (OutputStream ignored = out) { out = null; } }

    @Test
    void writeProduct() {
        ProductHelper.write(out, new Product("Monitor", 500F));
        String hex = out.getBufferReader().dumpAllData();
        // test it was marshalled correctly (observed with Yoko calling Oracle NEO orb)
        assertThat(hex, is("0000:  00000008 4d6f6e69 746f7200 43fa0000  \"....Monitor.C...\""));
    }

    @Test
    void writeProductWithExtendedAsciiChar() {
        ProductHelper.write(out, new Product("M\u00f6nit\u00f6r", 500F)); // F6 is u-umlaut
        String hex = out.getBufferReader().dumpAllData();
        // test it was marshalled correctly (observed with Yoko calling Oracle NEO orb)
        assertThat(hex, is("0000:  00000008 4df66e69 74f67200 43fa0000  \"....M.nit.r.C...\""));
    }

    @Test
    void readProduct() throws IOException {
        useHexData("000000084d6f6e69746f720043fa0000");
        try (InputStream in = out.create_input_stream()) {
            Product product = ProductHelper.read(in);
            // test it was read in correctly
            assertThat(product.name, is("Monitor"));
            assertThat(product.price, is(500F));
            // test it read to the end of the stream
            assertThat(in.available(), is(0));
        }
    }

    @Test
    void readProductWithExtendedAsciiChar() throws IOException {
        useHexData("000000084df66e6974f6720043fa0000");
        try (InputStream in = out.create_input_stream()) {
            Product product = ProductHelper.read(in);
            // test it was read in correctly
            assertThat(product.name, is("M\u00f6nit\u00f6r"));
            assertThat(product.price, is(500F));
            // test it read to the end of the stream
            assertThat(in.available(), is(0));
        }
    }

    @Test
    void readUserException() throws IOException {
        // Exceptions should start with an IDL exception ID string.
        // Since the generated stub discards the id,
        // use an empty string (i.e. one null char).
        // NEO doesn't always use BD for padding, but it's
        // a nice IBM convention to help the human reader.
        useHexData("00000001 00bdbdbd 00000013 57697265 6c657373 20547261 636b6261 6c6c00");
        try (InputStream in = out.create_input_stream()) {
            PRODUCT_NOT_EXIST exception = PRODUCT_NOT_EXISTHelper.read(in);
            assertThat(exception.productName, is("Wireless Trackball"));
        }
    }

    @Test
    void readUserExceptionWithExtendedAsciiChar() throws IOException {
        // Exceptions should start with an IDL exception ID string.
        // Since the generated stub discards the id,
        // use an empty string (i.e. one null char).
        // NEO doesn't always use BD for padding, but it's
        // a nice IBM convention to help the human reader.
        useHexData("00000001 00bdbdbd 00000013 57697265 6c657373 205472e4 636b62e4 6c6c00");
        try (InputStream in = out.create_input_stream()) {
            PRODUCT_NOT_EXIST exception = PRODUCT_NOT_EXISTHelper.read(in);
            assertThat(exception.productName, is("Wireless Tr\u00e4ckb\u00e4ll"));
        }
    }

    @Test
    void readProductList() throws IOException {
        // List of 4 products in String order as marshalled by NEO.
        // new Product("Keyboard", 100F);
        // new Product("Monitor", 500F);
        // new Product("Mouse", 50F);
        // new Product("M\u00f6nit\u00f6r", 499F);

        useHexData("" +
                "00000004 00000009 4b657962 6f617264" +
                "00554354 42c80000 00000008 4d6f6e69" +
                "746f7200 43fa0000 00000006 4d6f7573" +
                "65007261 42480000 00000008 4df66e69" +
                "74f67200 43f98000");
        try (InputStream in = out.create_input_stream()) {
            Product[] products = ProductListHelper.read(in);
            assertThat(products.length, is(4));
            assertThat(products[0].name, is("Keyboard"));
            assertThat(products[0].price, is(100F));
            assertThat(products[1].name, is("Monitor"));
            assertThat(products[1].price, is(500F));
            assertThat(products[2].name, is("Mouse"));
            assertThat(products[2].price, is(50F));
            assertThat(products[3].name, is("M\u00f6nit\u00f6r"));
            assertThat(products[3].price, is(499F));
        }
    }

    private void useHexData(String hex) {
        byte[] bytes = HEX_STRING.parse(hex);
        // java.io.OutputStream has a handy write(byte[]) method.
        // CORBA output streams write those bytes as longs, which is no use here.
        // So, use write_octet() to populate this output stream.
        out.write_octet_array(bytes, 0, bytes.length);
    }
}
