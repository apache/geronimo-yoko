/*
 * Copyright 2015 IBM Corporation and others.
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
package org.apache.yoko.orb.CosNaming.tnaming2;

import java.util.Properties;

import org.apache.yoko.orb.spi.naming.NameServiceInitializer;
import org.omg.CORBA.ORB;

/**
 * A transient name service attached to an ORB. This class manages all of the
 * housekeeping for creating a TransientNamingContext and a exposing it using an
 * ORB.
 */
public class TransientNameService implements AutoCloseable {
    // the default registered name service
    static public final String DEFAULT_SERVICE_NAME = "TNameService";
    // the default listening port
    static public final int DEFAULT_SERVICE_PORT = 900;
    // the default host name
    static public final String DEFAULT_SERVICE_HOST = "localhost";
    // initial listening port
    protected int port;
    // initial listening host
    protected String host;
    // the service name (used for registering for the corbaloc:: URL name
    protected String serviceName;
    // the orb instance we're running on
    protected ORB createdOrb;

    /**
     * Create a new TransientNameService, using all default attributes.
     */
    public TransientNameService() {
        this(DEFAULT_SERVICE_HOST, DEFAULT_SERVICE_PORT, DEFAULT_SERVICE_NAME);
    }

    /**
     * Create a default-named name service using the specified host and port
     * parameters.
     * @param host The host to expose this under.
     * @param port The initial listening port.
     */
    public TransientNameService(String host, int port) {
        this(host, port, DEFAULT_SERVICE_NAME);
    }

    /**
     * Create a specifically-named name service using the specified host and
     * port parameters.
     * @param host The host to expose this under.
     * @param port The initial listening port.
     * @param name The name to register this service under using the
     *            BootManager.
     */
    public TransientNameService(String host, int port, String name) {
        this.port = port;
        this.host = host;
        this.serviceName = name;
    }

    /**
     * Start up the name service, including creating an ORB instance to expose
     * it under.
     * @exception TransientServiceException
     */
    public void run() throws TransientServiceException {
        // Create an ORB object
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());

        props.put("org.omg.CORBA.ORBServerId", "1000000");
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass", "org.apache.yoko.orb.CORBA.ORBSingleton");
        props.put("org.omg.PortableInterceptor.ORBInitializerClass." + NameServiceInitializer.class.getName(), "");
        props.put("yoko.orb.oa.endpoint", "iiop --host " + host + " --port " + port);

        createdOrb = ORB.init(new String[]{"ORBNameService=" + serviceName}, props);

        // service initialized by orb initializer
    }

    /**
     * Destroy the created service.
     */
    public void destroy() {
        // only destroy this if we created the orb instance.
        if (createdOrb != null) {
            createdOrb.destroy();
            createdOrb = null;
        }
    }

    @Override
    public void close() throws Exception {
        destroy();
    }
}
