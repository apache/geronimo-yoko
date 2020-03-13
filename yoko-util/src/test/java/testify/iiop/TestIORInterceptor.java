/*
 * =============================================================================
 * Copyright (c) 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * =============================================================================
 */
package testify.iiop;

import org.omg.PortableInterceptor.IORInfo;
import org.omg.PortableInterceptor.IORInterceptor;

public interface TestIORInterceptor extends TestORBInitializer, TestInterceptor, IORInterceptor {
    default void establish_components(IORInfo info) {}
}

