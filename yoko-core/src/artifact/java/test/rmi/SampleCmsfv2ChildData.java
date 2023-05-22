/*
 * Copyright 2022 IBM Corporation and others.
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
package test.rmi;

public class SampleCmsfv2ChildData extends SampleCmsfv2ParentData {
    private static final long serialVersionUID = 1L;
    private final String value = "splat";
    
    @Override
    public int hashCode() {
        return 31*super.hashCode() + value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;
        if (!(obj instanceof SampleCmsfv2ChildData))
            return false;
        return value.equals(((SampleCmsfv2ChildData) obj).value);
    }
    
    @Override
    public String toString() {
        return String.format("%s:\"%s\"", super.toString(), value);
    }
}
