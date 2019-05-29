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

import java.util.Properties;

public abstract class Server {
    private enum ClassParam implements TypeRef<Class<? extends Server>> {SERVER_CLASS}
    private enum PropsParam implements TypeRef<Properties> {ORB_PROPS}
    private enum ArgsParam implements TypeRef<String[]> {ORB_ARGS}
    private enum Event implements TypeRef<Void> {STOP}

    private final String name;
    private String[] args;
    private Properties props;
    private ORB orb;

    protected Server() { this.name = this.getClass().getSimpleName(); }
    protected Server(String name) { this.name = name; }

    protected abstract void run(ORB orb, Bus bus);

    private void stop(Bus bus) {
        bus.log("Calling orb.destroy()");
        orb.destroy();
        bus.log("Calling orb.shutdown(true)");
        orb.shutdown(true);
        bus.log("ORB shutdown complete");
    };

    private void stopRemotely(Bus bus) {
        bus.forUser(name).put(Event.STOP);
    }

    private void run(Bus bus) throws Exception {
        Class<? extends Server> clazz = (Class<? extends Server>)bus.get(ClassParam.SERVER_CLASS);
        Server server = clazz.getConstructor().newInstance();
        server.props = bus.get(PropsParam.ORB_PROPS);
        server.args = bus.get(ArgsParam.ORB_ARGS);
        server.orb = ORB.init(server.args, server.props);
        bus.onMsg(Event.STOP, nul -> server.stop(bus));
        server.run(server.orb, bus);
    }

    public final void launch(PartRunner runner, Class<? extends Server> serverClass, Properties props, String... args) throws Exception {
        final Server server = serverClass.getConstructor().newInstance();
        String name = server.name;
        runner
                .fork(name, server::run)
                .here(name, bus -> bus.put(ClassParam.SERVER_CLASS, serverClass))
                .here(name, bus -> bus.put(PropsParam.ORB_PROPS, props))
                .here(name, bus -> bus.put(ArgsParam.ORB_ARGS, args))
                .onStop(name, server::stopRemotely);
    }
}
