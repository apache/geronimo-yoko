/*
 * Copyright 2022 IBM Corporation and others.
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
package org.apache.yoko.rmi.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TypeRepositoryTest {
    TypeRepository repo;

    @BeforeEach
    public void setup() {
        this.repo = TypeRepository.get();
    }

    @Test
    public void testTypeRepository() {
        class X implements Serializable {
            private static final long serialVersionUID = 0xDEADBEEF0FACEF00L;
        }
        final TypeDescriptor descriptor = repo.getDescriptor(X.class);
        assertThat(descriptor.getRepositoryID(), is(equalTo("RMI:org.apache.yoko.rmi.impl.TypeRepositoryTest\\U00241X:56D0CD61110C30C5:DEADBEEF0FACEF00")));
    }

    @Test
    public void testTypeRepositoryMultiThreaded() throws Exception {
        class X implements Serializable {
            private static final long serialVersionUID = 0xDEADBEEF0FACEF00L;
            byte b;
            char c;
            double d;
            float f;
            int i;
            long l;
        }
        final String expectedTypeInfo = getTypeRepoInfo(X.class, 1).iterator().next();
        range(0, 1024).forEach(unused -> {
            Set<String> typeInfos = getTypeRepoInfo(new OverridingLoader().loadClass(X.class.getName()), Runtime.getRuntime().availableProcessors());
            for (String info: typeInfos) {
                assertThat(info, is(expectedTypeInfo));
            }
            assertThat(typeInfos, contains(expectedTypeInfo));
        });
    }

    public Set<String> getTypeRepoInfo(Class type, int numThreads) {
        CountDownLatch latch = new CountDownLatch(numThreads);
        ForkJoinPool pool = new ForkJoinPool(numThreads);
        try {
            return range(0, numThreads)
                .mapToObj(i -> pool.submit(() -> {
                    try {
                        latch.await();
                    } catch (InterruptedException e) { }
                    final ValueDescriptor descriptor = (ValueDescriptor)repo.getDescriptor(type);
                    StringBuilder sb = new StringBuilder();
                    for (FieldDescriptor fd: descriptor._fields) {
                        final String fType = fd == null ? "null" : fd.getType().getName();
                        final String fName = fd == null ? "null" : fd.getIDLName();
                        sb.append(String.format("%-20s ", fType + ' ' + fName + ';'));
                    }
                    return sb.toString();
                }))
                .peek(dummy -> latch.countDown())
                .collect(toList()).stream()
                .map(ForkJoinTask::join)
                .collect(toSet());
        } finally {
            pool.shutdown();
        }
    }

    private static class OverridingLoader extends ClassLoader {
        OverridingLoader() {
            super(OverridingLoader.class.getClassLoader());
        }

        @Override
        public Class<?> loadClass(String name) {
            try {
                if (name.startsWith(TypeRepositoryTest.class.getName() + "$")) {
                    // get the bytes from the parent loader
                    InputStream in = getResourceAsStream(name.replace('.', '/') + ".class");
                    byte[] bytes = new byte[in.available()];
                    in.read(bytes);
                    // define the class locally and return it
                    return defineClass(name, bytes, 0, bytes.length);
                }
                return super.loadClass(name);
            } catch (Exception e) {
                throw (NoClassDefFoundError) new NoClassDefFoundError("Caught IOException").initCause(e);
            }
        }
    }
}
