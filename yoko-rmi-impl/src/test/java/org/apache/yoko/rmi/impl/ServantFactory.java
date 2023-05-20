/*
 * Copyright 2020 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.apache.yoko.rmi.impl;

import org.omg.CORBA.ORB;

import java.rmi.Remote;

public enum ServantFactory {
    ;
    // TODO: fix the horribly broken PRO.exportObject
    //       so that it doesn't create a new ORB,
    //       and then remove this scaffolding
    public static RMIServant getServant(Remote target, ORB orb) {
        // Note: each RMIState will create a new, anonymous POA_Manager
        RMIState state = new RMIState(orb, "test-only rmi state object");
        RMIServant tie = new RMIServant(state);
        tie.setTarget(target);
        return tie;
    }
}
