/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package testify.util;

import junit.framework.AssertionFailedError;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.function.Consumer;
import java.util.function.Supplier;

public enum SerialUtil {
    ;

    public interface SerializableConsumer<T> extends Consumer<T>, Serializable {}
    public interface SerializableSupplier<T> extends Supplier<T>, Serializable {}

    public static String stringify(Object payload) {
        if (payload == null) return "<null>";
        return Base64.getEncoder().encodeToString(writeObject(payload));
    }

    public static <T> T unstringify(String string) {
        if (string == null || string.equals("<null>")) return null;
        return readObject(Base64.getDecoder().decode(string));
    }

    private static byte[] writeObject(Object payload) {
        final byte[] bytes;
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(byteOut)) {
            out.writeObject(payload);
            out.flush();
            bytes = byteOut.toByteArray();
        } catch (IOException e) {
            throw new IOError(e);
        }
        return bytes;
    }

    @SuppressWarnings("unchecked")
    private static <T> T readObject(byte[] bytes) {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (T)in.readObject();
        } catch (RuntimeException|Error e) {
            throw e;
        } catch (Throwable e) {
            throw (Error)new AssertionFailedError("Unexpected exception:" + e).initCause(e);
        }
    }
}
