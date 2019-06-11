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
package testify.parts;

import org.omg.CORBA.ORB;
import testify.bus.Bus;
import testify.bus.EventBus.TypeRef;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public abstract class Server implements Serializable {
    private enum ClassParam implements TypeRef<Class<? extends Server>> {SERVER_CLASS}
    private enum PropsParam implements TypeRef<Properties> {ORB_PROPS}
    private enum ArgsParam implements TypeRef<String[]> {ORB_ARGS}
    private enum Event implements TypeRef<Void> {STOP}

    private String[] args;
    private Properties props;
    private transient ORB orb;

    protected abstract void run(ORB orb, Bus bus) throws Exception;

    private void stop(Bus bus) {
        bus.log("Calling orb.shutdown(true)");
        orb.shutdown(true);
        bus.log("ORB shutdown complete, calling orb.destroy()");
        orb.destroy();
    }

    private void stopRemotely(Bus bus) {
        bus.put(Event.STOP);
    }

    private void run(Bus bus) throws Exception {
        this.orb = ORB.init(args, props);
        bus.onMsg(Event.STOP, nul -> stop(bus));
        run(orb, bus);

        // Give up control to the ORB
        orb.run();
        bus.log("orb.run() completed.");
    }

    public static final void launch(PartRunner runner, Class<? extends Server> serverClass, String name, Properties props, String... args) {
        final Server server;
        try {
            server = serverClass.getConstructor().newInstance();
            server.props = props;
            server.args = args;
            runner.fork(name, server::run).endWith(name, server::stopRemotely);
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new Error("Could not construct " + serverClass + ". Make sure it has an accessible default constructor", e);
        }
    }
}
