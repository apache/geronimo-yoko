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

//
// IDL:orb.yoko.apache.org/OB/UnknownExceptionStrategy:1.0
//
/**
 *
 * The unknown exception strategy interface.
 *
 **/

public interface UnknownExceptionStrategyOperations
{
    //
    // IDL:orb.yoko.apache.org/OB/UnknownExceptionStrategy/unknown_exception:1.0
    //
    /**
     *
     * Handle an unknown exception.
     *
     * @param info Information about the exception.
     *
     **/

    void
    unknown_exception(UnknownExceptionInfo info);

    //
    // IDL:orb.yoko.apache.org/OB/UnknownExceptionStrategy/destroy:1.0
    //
    /**
     *
     * Clean up resources prior to ORB destruction.
     *
     **/

    void
    destroy();
}
