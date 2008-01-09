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

public abstract class Request {
    public abstract org.omg.CORBA.Object target();

    public abstract String operation();

    public abstract NVList arguments();

    public abstract NamedValue result();

    public abstract Environment env();

    public abstract ExceptionList exceptions();

    public abstract ContextList contexts();

    public abstract Context ctx();

    public abstract void ctx(Context c);

    public abstract Any add_in_arg();

    public abstract Any add_named_in_arg(String name);

    public abstract Any add_inout_arg();

    public abstract Any add_named_inout_arg(String name);

    public abstract Any add_out_arg();

    public abstract Any add_named_out_arg(String name);

    public abstract void set_return_type(TypeCode tc);

    public abstract Any return_value();

    public abstract void invoke();

    public abstract void send_oneway();

    public abstract void send_deferred();

    public abstract void get_response() throws org.omg.CORBA.WrongTransaction;

    public abstract boolean poll_response();
}
