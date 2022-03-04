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

import org.apache.yoko.orb.OB.OptionFilter;
import org.apache.yoko.orb.OCI.IIOP.Plugin_impl;
import org.apache.yoko.orb.OCI.IIOP.UnifiedConnectionHelper;
import org.apache.yoko.orb.OCI.IIOP.UnifiedConnectionHelperProvider;
import org.apache.yoko.osgi.ProviderLocator;
import org.apache.yoko.util.AssertionFailed;
import org.apache.yoko.util.Exceptions;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StringSeqHolder;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.security.AccessController.doPrivileged;
import static java.util.Optional.ofNullable;
import static java.util.logging.Level.FINE;
import static org.apache.yoko.util.PrivilegedActions.GET_CONTEXT_CLASS_LOADER;
import static org.apache.yoko.util.PrivilegedActions.getNoArgConstructor;

public class iiop implements PluginInit {
    static final Logger logger = Logger.getLogger(iiop.class.getName());
    public static final String ROOT_POAMGR_ENDPOINT_KEY = "yoko.orb.poamanager.RootPOAManager.endpoint";
    public static final String OA_ENDPOINT_KEY = "yoko.orb.oa.endpoint";
    // default settings for loading the connectionHelper "plugin-to-the-plugin"
    private String connectionHelper = "org.apache.yoko.orb.OCI.IIOP.DefaultConnectionHelper";
    private String helperArgs = "";

    //
    // Compatibility check. The plug-in should verify that it is
    // compatible with the given OCI version, and raise an exception
    // if not.
    //
    public void version(ORB orb, String ver) {
        // Nothing to do
    }

    //
    // Initialize the plug-in for an ORB
    //
    public Plugin init(ORB orb, StringSeqHolder args) {
        org.apache.yoko.orb.CORBA.ORB oborb = (org.apache.yoko.orb.CORBA.ORB) orb;
        Properties props = oborb.properties();

        args.value = parse_args(args.value, props);

        try {
            // get the appropriate class for the loading.
            Class<?> c = ProviderLocator.loadClass(connectionHelper, getClass(), doPrivileged(GET_CONTEXT_CLASS_LOADER));
            UnifiedConnectionHelper connectionHelper = ((UnifiedConnectionHelperProvider)doPrivileged(getNoArgConstructor(c)).newInstance())
                    .getUnifiedConnectionHelper();
            connectionHelper.init(orb, helperArgs);
            return new Plugin_impl(orb, connectionHelper);
        } catch (AssertionFailed|INITIALIZE e) {
            throw e;
        } catch (Exception e) {
            throw Exceptions.as(INITIALIZE::new, e, "unable to load IIOP ConnectionHelper plug-in `" + connectionHelper + "'");
        }
    }

    //
    // Parse IIOP arguments. The return value is a new array
    // with the IIOP arguments removed.
    //
    public String[] parse_args(String[] args, Properties props) {
        OptionParser
                .parse(props) // parse properties first since they have lower precedence
                .parse(args) // parse args second so they can override properties
                .apply(this)
                .update(props);

        OptionFilter filter = new OptionFilter("iiop.parse_args", "-OA");
        filter.add("host", 1); // Deprecated
        filter.add("numeric", 0); // Deprecated
        filter.add("port", 1); // Deprecated
        args = filter.filter(args);
        filter = new OptionFilter("iiop.parse_args", "-IIOP");
        filter.add("backlog", 1);
        filter.add("bind", 1);
        filter.add("host", 1);
        filter.add("numeric", 0);
        filter.add("port", 1);
        return filter.filter(args);
    }

    static class OptionParser {
        private String backlog;
        private String bind;
        private String host;
        private boolean numeric;
        private String port;
        private String connHelper;
        private String connHelperArgs;
        private boolean noArgs = true;

        interface ArgParser { IIOPSetter parse(String[] args); }
        interface IIOPSetter { PropertySetter apply(iiop target); }
        interface PropertySetter { void update(Properties props); }

        static ArgParser parse(Properties props) {
            OptionParser op = new OptionParser();
            props.entrySet().forEach(op::parseProperty);
            return op::parseArguments;
        }

        private void parseProperty(Entry<Object, Object> property) {
            String key = String.valueOf(property.getKey());
            String value = String.valueOf(property.getValue());
            switch (key) {
                case "org.omg.CORBA.ORBInitialHost":
                case "yoko.iiop.host":
                    this.setHost(value);
                    logger.fine("Using " + key + " value of " + value);
                    break;
                case "org.omg.CORBA.ORBInitialPort":
                case "yoko.iiop.port":
                    this.setPort(value);
                    logger.fine("Using " + key + " value of " + value);
                    break;
                case "org.omg.CORBA.ORBListenEndpoints":
                    // both specified on one property
                    int sep = value.indexOf(':');
                    if (sep != -1) {
                        this.setHost(value.substring(0, sep));
                        this.setPort(value.substring(sep + 1));
                        logger.fine("Using " + key + " value of " + value);
                    }
                    break;
                case "yoko.iiop.numeric":
                    this.setNumeric();
                    logger.fine("Using " + key + " value of " + value);
                    break;
                default:
                    if (key.startsWith("yoko.iiop.")) throw new INITIALIZE("iiop: unknown property " + key);
            }
        }

        private IIOPSetter parseArguments(String[] args) {
            Consumer<String> pending = null;
            for (String arg: args) {
                if (null == pending) {
                    pending = parseArgument(arg);
                    continue;
                }
                pending.accept(arg);
                pending = null;
            }
            if (null == pending) return this::applyConnectionHelperSettings;
            throw new INITIALIZE("iiop: argument expected for " + args[args.length - 1]);
        }

        private Consumer<String> parseArgument(String arg) {
            switch(arg) {
                case "-IIOPbacklog": return this::setBacklog;
                case "-IIOPbind": return this::setBind;
                case "-IIOPhost": // fallthru
                case "-OAhost": return this::setHost;
                case "-IIOPport": // fallthru
                case "-OAport": return this::setPort;
                case "-IIOPconnectionHelper": return this::setConnHelper;
                case "-IIOPconnectionHelperArgs": return this::setConnHelperArgs;
                case "-IIOPnumeric":
                case "-OAnumeric":
                    this.setNumeric();
                    return null;
                default:
                    if (arg.startsWith("-IIOP")) throw new INITIALIZE("iiop: unknown option `" + arg + "'");
                    return null;
            }
        }

        private PropertySetter applyConnectionHelperSettings(iiop target) {
            if (null != connHelper) target.connectionHelper = connHelper;
            if (null != connHelperArgs) target.helperArgs = connHelperArgs;
            return this::updateProperties;
        }

        private void updateProperties(Properties target) {
            if (noArgs) return;

            // choose the property key to use:
            String key = Stream.of(ROOT_POAMGR_ENDPOINT_KEY, OA_ENDPOINT_KEY)
                    .filter(target::containsKey)
                    .findFirst()
                    .orElse(OA_ENDPOINT_KEY);

            // Start with the existing value if there is one
            StringBuilder value = new StringBuilder(
                    Optional.of(key)
                            .map(target::getProperty)
                            .map(oldValue -> oldValue + ", ")
                            .orElse(""));

            value.append(numeric ? "iiop --numeric" : "iiop");
            ofNullable(host).map(s -> " --host \"" + s + "\"").map(value::append);
            ofNullable(port).map(s -> " --port " + s).map(value::append);
            ofNullable(backlog).map(s -> " --backlog " + s).map(value::append);
            ofNullable(bind).map(s -> " --bind " + s).map(value::append);

            if (logger.isLoggable(FINE)) logger.fine(String.format("Setting endpoint property \"%s\" to \"%s\"", key, value));
            target.put(key, value);
        }

        private void setBacklog(String s) { noArgs = false; backlog = s; }
        private void setBind(String s) { noArgs = false; bind = s; }
        private void setHost(String s) { noArgs = false; host = s; }
        private void setPort(String s) { noArgs = false; port = s; }
        private void setNumeric() { noArgs = false; numeric = true; }
        private void setConnHelper(String s) { connHelper = s; }
        private void setConnHelperArgs(String s) { connHelperArgs = s; }
    }


}
