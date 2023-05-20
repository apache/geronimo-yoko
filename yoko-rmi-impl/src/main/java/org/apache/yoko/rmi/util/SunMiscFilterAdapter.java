/*
 * Copyright 2021 IBM Corporation and others.
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
package org.apache.yoko.rmi.util;

import org.apache.yoko.rmi.util.SerialFilterHelper.BaseFilterAdapter;
import org.apache.yoko.rmi.util.SerialFilterHelper.BaseInfo;
import org.apache.yoko.rmi.util.SunMiscFilterAdapter.Info;
import sun.misc.ObjectInputFilter;
import sun.misc.ObjectInputFilter.Config;
import sun.misc.ObjectInputFilter.FilterInfo;
import sun.misc.ObjectInputFilter.Status;

import java.security.PrivilegedAction;

import static java.security.AccessController.doPrivileged;
import static sun.misc.ObjectInputFilter.Status.REJECTED;

final class SunMiscFilterAdapter extends BaseFilterAdapter<ObjectInputFilter, Info, Status> {
    SunMiscFilterAdapter() { super(Config.getSerialFilter(), REJECTED); }

    @Override
    Status checkInput(final Info info) {
        return doPrivileged((PrivilegedAction<Status>) () -> filter.checkInput(info));
    }

    @Override
    Info makeInfo(Class<?> serialClass, long arrayLength, long depth, long references, long streamBytes) {
        return new Info(serialClass, arrayLength, depth, references, streamBytes);
    }

    static final class Info extends BaseInfo implements FilterInfo {
        public Info(Class<?> serialClass, long arrayLength, long depth, long references, long streamBytes) {
            super(serialClass, arrayLength, depth, references, streamBytes);
        }
    }
}
