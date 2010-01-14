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

package org.apache.yoko.orb.OB;

public interface UpcallReturn {
    //
    // Called upon return of the upcall
    //
    void upcallBeginReply(Upcall upcall, org.omg.IOP.ServiceContext[] scl);

    void upcallEndReply(Upcall upcall);

    void upcallBeginUserException(Upcall upcall,
            org.omg.IOP.ServiceContext[] scl);

    void upcallEndUserException(Upcall upcall);

    void upcallUserException(Upcall upcall, org.omg.CORBA.UserException ex,
            org.omg.IOP.ServiceContext[] scl);

    void upcallSystemException(Upcall upcall,
            org.omg.CORBA.SystemException exception,
            org.omg.IOP.ServiceContext[] scl);

    void upcallForward(Upcall upcall, org.omg.IOP.IOR ior, boolean perm,
            org.omg.IOP.ServiceContext[] scl);
}
