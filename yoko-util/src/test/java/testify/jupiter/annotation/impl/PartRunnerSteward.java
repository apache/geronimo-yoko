/*
 * =============================================================================
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * =============================================================================
 */
package testify.jupiter.annotation.impl;

import org.junit.jupiter.api.extension.ExtensionContext;
import testify.jupiter.annotation.ConfigurePartRunner;
import testify.parts.PartRunner;

public class PartRunnerSteward extends Steward<ConfigurePartRunner> {
    private final PartRunner partRunner;

    private PartRunnerSteward(Class<?> testClass) {
        super(ConfigurePartRunner.class, testClass);
        this.partRunner = PartRunner.create();
        TracingSteward.addTraceSettings(partRunner, testClass);
    }

    @Override
    // A CloseableResource stored in a context store is closed automatically when the context goes out of scope.
    // Note this happens *before* the correlated extension callback points (e.g. AfterEachCallback/AfterAllCallback)
    public void close() {
        partRunner.join();
    }

    public static PartRunner getPartRunner(ExtensionContext ctx) {
        // PartRunners are always one per test, so get one for the root context
        return Steward.getInstanceForContext(ctx, PartRunnerSteward.class, PartRunnerSteward::new).partRunner;
    }
}
