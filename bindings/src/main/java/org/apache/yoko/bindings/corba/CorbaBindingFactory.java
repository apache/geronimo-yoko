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

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.cxf.binding.AbstractBindingFactory;
import org.apache.cxf.binding.Binding;

import org.apache.cxf.interceptor.BareInInterceptor;
import org.apache.cxf.interceptor.BareOutInterceptor;

import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.EndpointInfo;


import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.ConduitInitiator;
import org.apache.cxf.transport.ConduitInitiatorManager;
import org.apache.cxf.transport.Destination;
import org.apache.cxf.transport.DestinationFactory;
import org.apache.cxf.transport.DestinationFactoryManager;

import org.apache.cxf.ws.addressing.EndpointReferenceType;

import org.apache.yoko.bindings.corba.interceptors.CorbaStreamFaultInInterceptor;
import org.apache.yoko.bindings.corba.interceptors.CorbaStreamFaultOutInterceptor;
import org.apache.yoko.bindings.corba.interceptors.CorbaStreamInInterceptor;
import org.apache.yoko.bindings.corba.interceptors.CorbaStreamOutInterceptor;

import org.apache.yoko.bindings.corba.utils.OrbConfig;

public class CorbaBindingFactory extends AbstractBindingFactory
    implements ConduitInitiator, DestinationFactory {

    private Collection<String> activationNamespaces;
    private List<String> transportIds;
    private OrbConfig orbConfig = new OrbConfig();

    @Resource(name = "orbClass")
    public void setOrbClass(String cls) {
        orbConfig.setOrbClass(cls);
    }
    
    @Resource(name = "orbSingletonClass")
    public void setOrbSingletonClass(String cls) {
        orbConfig.setOrbSingletonClass(cls);
    }

    @PostConstruct
    void registerWithBindingManager() {
        ConduitInitiatorManager cim = getBus().getExtension(ConduitInitiatorManager.class);
        if (null != cim) {
            for (String ns : activationNamespaces) {
                cim.registerConduitInitiator(ns, this);
            }
        }
        DestinationFactoryManager dfm = getBus().getExtension(DestinationFactoryManager.class);
        if (null != dfm) {
            for (String ns : activationNamespaces) {
                dfm.registerDestinationFactory(ns, this);
            }
        }
    }

    public Binding createBinding(BindingInfo bindingInfo) {
        CorbaBinding binding = new CorbaBinding();

        binding.getInFaultInterceptors().add(new CorbaStreamFaultInInterceptor());
        binding.getOutFaultInterceptors().add(new CorbaStreamFaultOutInterceptor());
        binding.getOutInterceptors().add(new BareOutInterceptor());
        binding.getOutInterceptors().add(new CorbaStreamOutInterceptor());
        binding.getInInterceptors().add(new BareInInterceptor());
        binding.getInInterceptors().add(new CorbaStreamInInterceptor());
        binding.setBindingInfo(bindingInfo);
        return binding;
    }

    public Conduit getConduit(EndpointInfo endpointInfo)
        throws IOException {
        return getConduit(endpointInfo, null);
    }

    public Conduit getConduit(EndpointInfo endpointInfo, EndpointReferenceType target)
        throws IOException {
        return new CorbaConduit(endpointInfo, target, orbConfig);
    }

    public Destination getDestination(EndpointInfo endpointInfo)
        throws IOException {
        return new CorbaDestination(endpointInfo, orbConfig);
    }

    public List<String> getTransportIds() {
        return transportIds;
    }

    @Resource(name = "transportIds")
    public void setTransportIds(List<String> ids) {
        transportIds = ids;
    }

    @Resource
    public void setOrbArgs(List<String> args) {
        orbConfig.setOrbArgs(args);
    }
    
    public Set<String> getUriPrefixes() {
        Set<String> uriPrefixes = new java.util.HashSet<String>();
        uriPrefixes.add("IOR");
        uriPrefixes.add("ior");
        uriPrefixes.add("file");
        uriPrefixes.add("relfile");
        uriPrefixes.add("corba");
        return uriPrefixes;
    }
}
