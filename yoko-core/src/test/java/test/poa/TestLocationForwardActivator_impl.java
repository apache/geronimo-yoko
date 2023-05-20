/*
 * Copyright 2010 IBM Corporation and others.
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
package test.poa;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;

final class TestLocationForwardActivator_impl extends
        org.omg.PortableServer.ServantActivatorPOA {
    private boolean activate_;

    private Servant servant_;

    private org.omg.CORBA.Object forward_;

    TestLocationForwardActivator_impl() {
        activate_ = false;
    }

    public void setActivatedServant(Servant servant) {
        servant_ = servant;
    }

    public void setForwardRequest(org.omg.CORBA.Object forward) {
        forward_ = forward;
    }

    public Servant incarnate(byte[] oid, POA poa) throws ForwardRequest {
        activate_ = !activate_;
        if (!activate_)
            throw new ForwardRequest(forward_);
        return servant_;
    }

    public void etherealize(byte[] oid, POA poa, Servant servant,
            boolean cleanup, boolean remaining) {
    }
}
