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

package org.apache.yoko.bindings.corba.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.yoko.bindings.corba.CorbaBindingException;
import org.omg.CORBA.ORB;

public class CorbaBindingHelper {

    private static final Logger LOG = LogUtils.getL7dLogger(CorbaBindingHelper.class);
    private static Map<String, ORB> orbList = new HashMap<String, ORB>();
    private static Map<String, Integer> orbUseCount = new HashMap<String, Integer>();
    private static ORB defaultORB;
    
    public static ORB getDefaultORB(OrbConfig config) {        
        if (defaultORB == null) {
            Properties props = System.getProperties();
            props.put("org.omg.CORBA.ORBClass", config.getOrbClass());
            props.put("org.omg.CORBA.ORBSingletonClass", config.getOrbSingletonClass());
            props.put("yoko.orb.id", "Yoko-Binding");
            List<String> orbArgs = config.getOrbArgs();
            defaultORB = ORB.init(orbArgs.toArray(new String[orbArgs.size()]), props);
            if (defaultORB == null) {
                LOG.severe("Could not create instance of the ORB");
                throw new CorbaBindingException("Could not create instance of the ORB");
            }
        }
        return defaultORB;
    }
    
    public static ORB createAddressSpecificORB(String address, OrbConfig config) {
        ORB orb = null;
        
        URI addressURI = null;
        try {
            addressURI = new URI(address);
        } catch (URISyntaxException ex) {
            throw new CorbaBindingException("Unable to create ORB with address " + address);
        }

        Properties props = System.getProperties();
        props.put("org.omg.CORBA.ORBClass", config.getOrbClass());
        props.put("org.omg.CORBA.ORBSingletonClass", config.getOrbSingletonClass());
        props.put("yoko.orb.id", "Yoko-Binding-" + address);
        List<String> orbArgs = config.getOrbArgs();
        
        String scheme = addressURI.getScheme();
        // A corbaloc address gives us host and port information to use when setting up the
        // endpoint for the ORB.  Other types of references will just create ORBs on the 
        // host and port used when no preference has been specified.
        if (scheme.equals("corbaloc")) {
            String schemeSpecificPart = addressURI.getSchemeSpecificPart();
            int keyIndex = schemeSpecificPart.indexOf('/');
            String corbaAddr = schemeSpecificPart.substring(0, keyIndex);

            int index = corbaAddr.indexOf(':');
            String protocol = "iiop";
            if (index != 0) {
                protocol = corbaAddr.substring(0, index);
            }
            int oldIndex = index;
            index = corbaAddr.indexOf(':', oldIndex + 1);
            String host = corbaAddr.substring(oldIndex + 1, index);
            String port = corbaAddr.substring(index + 1);
            
            props.put("yoko.orb.oa.endpoint", new String(protocol + " --host " + host + " --port " + port));
            
            orb = ORB.init(orbArgs.toArray(new String[orbArgs.size()]), props);
            
            orbList.put(getORBNameFromAddress(address), orb);
        } else if (scheme.equals("file") || scheme.equals("relfile") 
                   || scheme.equals("IOR") || scheme.equals("ior")) {
            orb = ORB.init(orbArgs.toArray(new String[orbArgs.size()]), props);
            
            orbList.put(getORBNameFromAddress(address), orb);
        } else {
            throw new CorbaBindingException("Unsupported address scheme type " + scheme);
        }

        return orb;
    }
    
    public static ORB getAddressSpecificORB(String address) {
        return orbList.get(getORBNameFromAddress(address));
    }

    public static void addAddressSpecificORB(String address, ORB orb) {
        orbList.put(getORBNameFromAddress(address), orb);
    }
    
    public static String getORBNameFromAddress(String address) {
        String name = null;
       
        URI addressURI = null;
        try {
            addressURI = new URI(address);
        } catch (URISyntaxException ex) {
            throw new CorbaBindingException("Unable to locate ORB with address " + address);
        }
        
        String scheme = addressURI.getScheme();
        if (scheme.equals("corbaloc") || scheme.equals("corbaname")) {
            String schemeSpecificPart = addressURI.getSchemeSpecificPart();
            int keyIndex = schemeSpecificPart.indexOf('/');
            name = schemeSpecificPart.substring(0, keyIndex);        
        } else if (scheme.equals("IOR") || scheme.equals("ior")) {        
            name = addressURI.toString();
        } else if (scheme.equals("file") || scheme.equals("relfile")) {
            name = addressURI.getPath();
            if (name == null) {
                name = addressURI.getSchemeSpecificPart();
            }
        } else {
            throw new CorbaBindingException("Unsupported address scheme type " + scheme);
        }

        return name;
    }

    // This indicates that we need to keep the ORB alive.  This allows multiple objects to share the
    // same ORB and not have one of the objects destroy it while other objects are using it.
    public static void keepORBAlive(String address) {
        Integer count = orbUseCount.get(getORBNameFromAddress(address));

        if (count == null) {
            orbUseCount.put(getORBNameFromAddress(address), 1);
        } else {
            orbUseCount.put(getORBNameFromAddress(address), count + 1);
        }
    }

    // Signals that the ORB should be tested to see if it can be destroyed.  Actual destruction will
    // only occur if the ORB is not being used by someone else.  If it is, then we simply decrement
    // the count.
    public static void destroyORB(String address, ORB orb) throws CorbaBindingException {
        Integer count = orbUseCount.get(getORBNameFromAddress(address));

        if (count == null) {
            return;
        }

        count = count - 1;

        if (count < 1) {
            // We shouldn't have anyone waiting on this ORB.  Destroy it.
            orbUseCount.remove(getORBNameFromAddress(address));
            orbList.remove(getORBNameFromAddress(address));
            try {
                orb.destroy();
            } catch (Exception ex) {
                throw new CorbaBindingException(ex);
            }
        } else {
            orbUseCount.put(getORBNameFromAddress(address), count);
        }

    }

}
