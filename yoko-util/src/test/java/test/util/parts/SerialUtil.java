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
package test.util.parts;

import junit.framework.AssertionFailedError;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

enum SerialUtil {
    ;
    public static String stringify(Object payload) {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(byteOut)) {
            out.writeObject(payload);
            out.flush();
            final byte[] bytes = byteOut.toByteArray();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    public static <T> T unstringify(String string) {
        try { return readObject(Base64.getDecoder().decode(string)); }
        catch (IOException e) { throw new IOError(e); }
        catch (ClassNotFoundException e) { throw wrapAsError(e); }
    }

    private static NoClassDefFoundError wrapAsError(ClassNotFoundException e) {
        return (NoClassDefFoundError) new NoClassDefFoundError(e.getMessage()).initCause(e);
    }

    @SuppressWarnings("unchecked")
    private static <T> T readObject(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return throwIfException((T)in.readObject());
        } catch (RuntimeException|Error e) {
            throw e;
        } catch (Throwable e) {
            throw (Error)new AssertionFailedError("Unexpected exception:" + e).initCause(e);
        }
    }

    private static <T> T throwIfException(T t) throws Throwable {
        if (t instanceof Throwable) throw (Throwable)t;
        return t;
    }
}
