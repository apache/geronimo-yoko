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

package org.apache.yoko.orb.OCI;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.yoko.orb.OCI.IIOP.ConnectionHelper;

public class iiop implements PluginInit {
    static final Logger logger = Logger.getLogger(iiop.class.getName());
    // default settings for loading the connectionHelper "plugin-to-the-plugin"
    private String connectionHelper = "org.apache.yoko.orb.OCI.IIOP.DefaultConnectionHelper";
    private String helperArgs = "";

    //
    // Compatibility check. The plug-in should verify that it is
    // compatible with the given OCI version, and raise an exception
    // if not.
    //
    public void version(org.omg.CORBA.ORB orb, String ver) {
        // Nothing to do
    }

    //
    // Initialize the plug-in for an ORB
    //
    public org.apache.yoko.orb.OCI.Plugin init(org.omg.CORBA.ORB orb,
            org.omg.CORBA.StringSeqHolder args) {
        org.apache.yoko.orb.CORBA.ORB oborb = (org.apache.yoko.orb.CORBA.ORB) orb;
        java.util.Properties props = oborb.properties();

        //
        // Parse arguments
        //
        args.value = parse_args(args.value, props);

        ConnectionHelper helper = null;

        try {
            // get the appropriate class for the loading.
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader == null) {
                loader = this.getClass().getClassLoader();
            }

            Class c = loader.loadClass(connectionHelper);
            helper = (org.apache.yoko.orb.OCI.IIOP.ConnectionHelper) c.newInstance();
            // give this a chance to initializer
            helper.init(orb, helperArgs);
        } catch (Exception ex) {
            throw new org.omg.CORBA.INITIALIZE("unable to load IIOP ConnectionHelper plug-in `" + connectionHelper + "': " + ex.getMessage());
        }

        return new org.apache.yoko.orb.OCI.IIOP.Plugin_impl(orb, helper);
    }

    //
    // Parse IIOP arguments. The return value is the a new array
    // with the IIOP arguments removed.
    //
    public String[] parse_args(String[] args, java.util.Properties props) {
        String backlog = null;
        String bind = null;
        String host = null;
        boolean numeric = false;
        String port = null;
        boolean haveArgs = false;

        //
        // First check deprecated properties, which have lowest precedence
        //
        {
            java.util.Enumeration keys = props.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                // we'll recognize and process the some of the 
                // portable CORBA properties for cross-orb compatibility
                if (key.startsWith("org.omg.CORBA.")) {
                    String value = props.getProperty(key);
                    if (key.equals("org.omg.CORBA.ORBInitialHost")) {
                        host = value; 
                        haveArgs = true; 
                        logger.fine("Using ORBInitialHost value of " + host); 
                    }
                    else if (key.equals("org.omg.CORBA.ORBInitialPort")) {
                        port = value; 
                        haveArgs = true; 
                        logger.fine("Using ORBInitialPort value of " + port); 
                    }
                    else if (key.equals("org.omg.CORBA.ORBListenEndpoints")) {
                        // both specified on one property 
                        int sep = value.indexOf(':'); 
                        if (sep != -1) {
                            host = value.substring(0, sep); 
                            port = value.substring(sep + 1); 
                            haveArgs = true; 
                            logger.fine("Using ORBListenEndpoints values of " + host + "/" + port); 
                        }
                    }
                }
                else if (key.startsWith("yoko.iiop.")) {
                    String value = props.getProperty(key);

                    if (key.equals("yoko.iiop.host")) {
                        host = value;
                        haveArgs = true;
                        logger.fine("Using yoko.iiop.host value of " + host); 
                    } else if (key.equals("yoko.iiop.numeric")) {
                        numeric = true;
                        haveArgs = true;
                        logger.fine("Using yoko.iiop.numeric value"); 
                    } else if (key.equals("yoko.iiop.port")) {
                        port = value;
                        haveArgs = true;
                        logger.fine("Using yoko.iiop.port value of " + port); 
                    } else {
                        throw new org.omg.CORBA.INITIALIZE("iiop: unknown "
                                + "property " + key);
                    }
                }
            }
        }

        //
        // Check command-line arguments
        //
        int i = 0;
        while (i < args.length) {
            if (args[i].equals("-IIOPbacklog")) {
                if (i + 1 >= args.length)
                    throw new org.omg.CORBA.INITIALIZE("iiop: argument "
                            + "expected for " + "-IIOPbacklog");
                backlog = args[i + 1];
                haveArgs = true;
                i += 2;
            } else if (args[i].equals("-IIOPbind")) {
                if (i + 1 >= args.length)
                    throw new org.omg.CORBA.INITIALIZE("iiop: argument "
                            + "expected for " + "-IIOPbind");
                bind = args[i + 1];
                haveArgs = true;
                i += 2;
            } else if (args[i].equals("-IIOPhost") || args[i].equals("-OAhost")) {
                if (i + 1 >= args.length)
                    throw new org.omg.CORBA.INITIALIZE("iiop: argument "
                            + "expected for " + args[i]);
                host = args[i + 1];
                haveArgs = true;
                i += 2;
            } else if (args[i].equals("-IIOPnumeric")
                    || args[i].equals("-OAnumeric")) {
                numeric = true;
                haveArgs = true;
                i++;
            } else if (args[i].equals("-IIOPport") || args[i].equals("-OAport")) {
                if (i + 1 >= args.length)
                    throw new org.omg.CORBA.INITIALIZE("iiop: argument "
                            + "expected for " + args[i]);
                port = args[i + 1];
                haveArgs = true;
                i += 2;
            } else if (args[i].equals("-IIOPconnectionHelper")) {
                if (i + 1 >= args.length)
                    throw new org.omg.CORBA.INITIALIZE("iiop: argument "
                            + "expected for " + args[i]);
                connectionHelper = args[i + 1];
                // NB:  We strip out the connection helper related arguments, so we don't set the
                // haveArgs flag for this.
                i += 2;
            } else if (args[i].equals("-IIOPconnectionHelperArgs")) {
                if (i + 1 >= args.length)
                    throw new org.omg.CORBA.INITIALIZE("iiop: argument "
                            + "expected for " + args[i]);
                helperArgs = args[i + 1];
                // NB:  We strip out the connection helper related arguments, so we don't set the
                // haveArgs flag for this.
                i += 2;
            } else if (args[i].startsWith("-IIOP")) {
                throw new org.omg.CORBA.INITIALIZE("iiop: unknown option `"
                        + args[i] + "'");
            } else
                i++;
        }

        if (haveArgs) {
            String propName = "yoko.orb.poamanager.RootPOAManager.endpoint";
            String value = props.getProperty(propName);
            if (value == null) {
                propName = "yoko.orb.oa.endpoint";
                value = props.getProperty(propName);
            }

            String str = "iiop";
            if (backlog != null) {
                str += " --backlog ";
                str += backlog;
            }
            if (bind != null) {
                str += " --bind ";
                str += bind;
            }
            if (host != null) {
                str += " --host ";
                //
                // If host contains a comma, then we must put the value
                // in quotes
                //
                if (host.indexOf(',') != -1) {
                    str += '"';
                    str += host;
                    str += '"';
                } else
                    str += host;
            }
            if (numeric) {
                str += " --numeric";
            }
            if (port != null) {
                str += " --port ";
                str += port;
            }

            if (value == null) {
                logger.fine("Setting endpoint property " + propName + " to " + str); 
                props.put(propName, str);
            }
            else {
                //
                // Append to existing property value
                //
                logger.fine("Setting endpoint property " + propName + " to " + value + ", " + str); 
                props.put(propName, value + ", " + str);
            }
        }

        //
        // Filter arguments
        //
        org.apache.yoko.orb.OB.OptionFilter filter;
        filter = new org.apache.yoko.orb.OB.OptionFilter("iiop.parse_args",
                "-OA");
        filter.add("host", 1); // Deprecated
        filter.add("numeric", 0); // Deprecated
        filter.add("port", 1); // Deprecated
        args = filter.filter(args);
        filter = new org.apache.yoko.orb.OB.OptionFilter("iiop.parse_args",
                "-IIOP");
        filter.add("backlog", 1);
        filter.add("bind", 1);
        filter.add("host", 1);
        filter.add("numeric", 0);
        filter.add("port", 1);
        return filter.filter(args);
    }
}
