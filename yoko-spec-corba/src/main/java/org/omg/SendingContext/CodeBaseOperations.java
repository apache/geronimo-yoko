/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
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

package org.omg.SendingContext;

public interface CodeBaseOperations extends
		org.omg.SendingContext.RunTimeOperations {
	public java.lang.String implementation(java.lang.String id);

	public java.lang.String[] implementations(java.lang.String[] ids);

	public java.lang.String[] bases(java.lang.String id);

	public org.omg.CORBA.Repository get_ir();

	public org.omg.CORBA.ValueDefPackage.FullValueDescription meta(
			java.lang.String id);

	public org.omg.CORBA.ValueDefPackage.FullValueDescription[] metas(
			java.lang.String id);
}
