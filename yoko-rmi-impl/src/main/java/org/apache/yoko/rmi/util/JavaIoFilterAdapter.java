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
package org.apache.yoko.rmi.util;

import org.apache.yoko.rmi.util.JavaIoFilterAdapter.Info;
import org.apache.yoko.rmi.util.SerialFilterHelper.BaseFilterAdapter;
import org.apache.yoko.rmi.util.SerialFilterHelper.BaseInfo;

import java.io.ObjectInputFilter;
import java.io.ObjectInputFilter.Config;
import java.io.ObjectInputFilter.FilterInfo;
import java.io.ObjectInputFilter.Status;

import static java.io.ObjectInputFilter.Status.REJECTED;

final class JavaIoFilterAdapter extends BaseFilterAdapter<ObjectInputFilter, Info, Status> {
    JavaIoFilterAdapter() { super(Config.getSerialFilter(), REJECTED); }

    @Override
    Status checkInput(Info info) { return filter.checkInput(info); }

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
