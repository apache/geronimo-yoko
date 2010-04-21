/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.apache.yoko.osgi.locator;

import java.util.logging.Logger;

import org.osgi.framework.Bundle;

/**
 * @version $Rev$ $Date$
 */
public class ServiceBean {
    private static final Logger log = Logger.getLogger(ServiceBean.class.getName());

    private Register providerRegistry;

    private BundleProviderLoader bundleProviderLoader;

    public ServiceBean(String key,
                       String className,
                       Bundle bundle,
                       Register providerRegistry) {
        this(key, className, bundle, providerRegistry, -1);
    }
    
    public ServiceBean(String key,
                       String className,
                       Bundle bundle,
                       Register providerRegistry,
                       Integer priority) {
        bundleProviderLoader = new BundleProviderLoader(key, className, bundle, priority == null? -1: priority);
        log.finer("ServiceBean: " + bundleProviderLoader);
        this.providerRegistry = providerRegistry;
    }



    public void start() {
        providerRegistry.registerService(bundleProviderLoader);
    }

    public void stop() {
        providerRegistry.unregisterService(bundleProviderLoader);
    }
}