/*
 * Copyright 2019 IBM Corporation and others.
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
package org.apache.yoko.orb.OB;

import org.apache.yoko.orb.IOP.ServiceContexts;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.UserException;
import org.omg.IOP.IOR;

public interface UpcallReturn {
    // Called upon return of the upcall
    void upcallBeginReply(Upcall upcall, ServiceContexts contexts);
    void upcallEndReply(Upcall upcall);
    void upcallBeginUserException(Upcall upcall, ServiceContexts contexts);
    void upcallEndUserException(Upcall upcall);
    void upcallUserException(Upcall upcall, UserException ex, ServiceContexts contexts);
    void upcallSystemException(Upcall upcall, SystemException exception, ServiceContexts contexts);
    void upcallForward(Upcall upcall, IOR ior, boolean perm, ServiceContexts contexts);
    boolean replySent();
}
