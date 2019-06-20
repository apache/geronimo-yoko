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

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import testify.bus.Bus;
import testify.bus.LogBus.LogLevel;
import testify.parts.PartRunner;
import testify.parts.ServerPart;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static testify.jupiter.OrbSteward.args;
import static testify.jupiter.OrbSteward.props;

@Repeatable(ConfigureMultiServer.class)
@ExtendWith(ServerExtension.class)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@ConfigureOrb
@ConfigurePartRunner
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigureServer {
    Class<? extends ServerPart> value();
    String name() default "server";
    boolean newProcess() default false;
    /**
     * Define the config for the ORB this server will use.
     */
    ConfigureOrb orb() default @ConfigureOrb;
    Tracing trace() default @Tracing(level = LogLevel.WARN);
}

class ServerSteward extends Steward<ConfigureServer> {
    private final ConfigureServer config;
    private final String name;

    private ServerSteward(Class<?> testClass) {
        super(ConfigureServer.class);
        this.config = getAnnotation(testClass);
        this.name = config.name();
    }

    ServerSteward(ConfigureServer config, String name) {
        super(ConfigureServer.class);
        this.config = config;
        this.name = name;
    }

    Bus getBus(ExtensionContext ctx) {
        return PartRunnerSteward.getPartRunner(ctx).bus(name);
    }

    void startServer(ExtensionContext ctx) {
        PartRunner runner = PartRunnerSteward.getPartRunner(ctx);
        // does this part run in a thread or a new process?
        runner.useProcesses(config.newProcess());
        // enable the specified logging for this part only
        Tracing trc = config.trace();
        runner.enableLogging(trc.level(), trc.maxLevel(), trc.classes(), name);
        ServerPart.launch(runner, config.value(), this.name, props(config.orb()), args(config.orb()));
    }

    static ServerSteward getInstance(ExtensionContext ctx) {
        return Steward.getInstanceForContext(ctx, ServerSteward.class, ServerSteward::new);
    }
}

class ServerExtension implements BeforeAllCallback, SimpleParameterResolver<Bus> {
    @Override
    public void beforeAll(ExtensionContext ctx) throws Exception { ServerSteward.getInstance(ctx).startServer(ctx); }
    @Override
    public boolean supportsParameter(ParameterContext ctx) { return ctx.getParameter().getType() == Bus.class; }
    @Override
    // Since the ServerSteward was retrieved from BeforeAll (i.e. in the test class context),
    // that is the one that will be found and reused from here (even if this is a test method context)
    public Bus resolveParameter(ExtensionContext ctx)  { return ServerSteward.getInstance(ctx).getBus(ctx); }
}
