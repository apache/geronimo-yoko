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
package testify.jupiter;

import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;
import testify.util.Stack;

import java.util.function.Function;

class DelegatingExtension<K, V extends CloseableResource> implements Extension {
    private final Function<ExtensionContext, K> keyFactory;
    private final Function<K, V> valueFactory;

    DelegatingExtension(Function<ExtensionContext, K> keyFactory, Function<K, V> valueFactory) {
        this.keyFactory = keyFactory;
        this.valueFactory = valueFactory;
    }

    /**
     * If a delegate has been stored against this context or an ancestor context, then it will be retrieved.
     * Otherwise, a new one will be created and stored against this context. It will be cleaned up when the
     * context for which it is created goes out of scope.
     */
    V getDelegate(ExtensionContext context) {
        K key = keyFactory.apply(context);
        // bus not available yet so enable below statement to debug
        if (false) System.out.printf("%s invoked for %s%n", Stack.getCallingFrame(1), key);
        Namespace namespace = Namespace.create(key);
        final Store store = context.getStore(namespace);
        return (V) store.getOrComputeIfAbsent(key, valueFactory);
    }
}
