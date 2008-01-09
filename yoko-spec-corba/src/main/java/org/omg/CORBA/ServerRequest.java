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
package org.omg.CORBA;

public abstract class ServerRequest {
    /**
     * @deprecated use operation()
     */
    public String op_name() {
        return operation();
    }

    public String operation() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public abstract Context ctx();

    /**
     * @deprecated use arguments()
     */
    public void params(NVList parms) {
        arguments(parms);
    }

    public void arguments(NVList nv) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /**
     * @deprecated use set_result()
     */
    public void result(Any a) {
        set_result(a);
    }

    public void set_result(Any a) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    /**
     * @deprecated use set_exception()
     */
    public void except(Any a) {
        set_exception(a);
    }

    public void set_exception(Any val) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }
}
