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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by nrichard on 11/03/16.
 */
public class SampleData implements Serializable {
    private static class Data2 implements Serializable {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Data2)) return false;
            return true;
        }
    }

    private static class MrBoom implements Serializable {
        private void writeObject(ObjectOutputStream oos) throws IOException {
            throw new IOException("*BOOM*!");
        }
    }

    public static enum DataEnum {
        E1(new Data2()), E2(new MrBoom());
        public final Serializable s;

        private DataEnum(Serializable s) {
            this.s = s;
        }
    }

    public final Serializable f1 = DataEnum.E1;
    public final Serializable f2 = DataEnum.E2;
    public final Serializable f3 = DataEnum.E1.s;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SampleData)) return false;
        SampleData sd = (SampleData)o;
        if (f1 != sd.f1) return false;
        if (f2 != sd.f2) return false;
        return f3.equals(sd.f3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(f1, f2);
    }
}
