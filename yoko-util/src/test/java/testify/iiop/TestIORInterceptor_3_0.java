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
import org.omg.PortableInterceptor.IORInterceptor_3_0;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;

public interface TestIORInterceptor_3_0 extends TestIORInterceptor, IORInterceptor_3_0 {
     default void components_established(IORInfo info) {}
     default void adapter_manager_state_changed(String id, short state) {}
     default void adapter_state_changed(ObjectReferenceTemplate[] templates, short state) {}
 }
