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

package org.omg.PortableServer.portable;

import org.omg.PortableServer.Servant;
import org.omg.PortableServer.POA;

public interface Delegate {
    org.omg.CORBA.ORB orb(Servant self);

    org.omg.CORBA.Object this_object(Servant self);

    POA poa(Servant self);

    byte[] object_id(Servant self);

    POA default_POA(Servant self);

    boolean is_a(Servant self, String repository_id);

    boolean non_existent(Servant self);

    org.omg.CORBA.InterfaceDef get_interface(Servant self);

    org.omg.CORBA.Object get_interface_def(Servant self);
}
