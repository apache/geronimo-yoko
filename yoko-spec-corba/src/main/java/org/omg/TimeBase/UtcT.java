/*
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
package org.omg.TimeBase;

//
// IDL:omg.org/TimeBase/UtcT:1.0
//
/***/

final public class UtcT implements org.omg.CORBA.portable.IDLEntity
{
    private static final String _ob_id = "IDL:omg.org/TimeBase/UtcT:1.0";

    public
    UtcT()
    {
    }

    public
    UtcT(long time,
         int inacclo,
         short inacchi,
         short tdf)
    {
        this.time = time;
        this.inacclo = inacclo;
        this.inacchi = inacchi;
        this.tdf = tdf;
    }

    public long time;
    public int inacclo;
    public short inacchi;
    public short tdf;
}
