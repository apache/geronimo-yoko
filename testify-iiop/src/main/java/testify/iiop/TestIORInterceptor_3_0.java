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
package testify.iiop;

import org.omg.PortableInterceptor.IORInfo;
import org.omg.PortableInterceptor.IORInterceptor_3_0;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;

public interface TestIORInterceptor_3_0 extends TestIORInterceptor, IORInterceptor_3_0 {
     default void components_established(IORInfo info) {}
     default void adapter_manager_state_changed(String id, short state) {}
     default void adapter_state_changed(ObjectReferenceTemplate[] templates, short state) {}
 }
