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
package org.apache.yoko;

import java.io.Serializable;

import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;

import junit.framework.TestCase;

import org.junit.Assert;
import org.omg.CORBA.ValueDefPackage.FullValueDescription;
import org.omg.SendingContext.CodeBase;


public class MetaTest extends TestCase {

    public void testMetaForClassWithASelfReference() {
        ValueHandler vh = Util.createValueHandler();
        CodeBase codebase = (CodeBase)vh.getRunTimeCodeBase();
        String dataClassRepid = vh.getRMIRepositoryID(Data.class);
        FullValueDescription fvd = codebase.meta(dataClassRepid);
        Assert.assertNotNull(fvd);
    }

    public void testMetaForClassWithTwoSelfReferences() {
        ValueHandler vh = Util.createValueHandler();
        CodeBase codebase = (CodeBase)vh.getRunTimeCodeBase();
        String dataClassRepid = vh.getRMIRepositoryID(X.class);
        System.out.println(dataClassRepid);
        FullValueDescription fvd = codebase.meta(dataClassRepid);
        Assert.assertNotNull(fvd);
    }

    public static class Data implements Serializable {
        private static final long serialVersionUID = 1L;
        public Data d;
    }

    public static class X {
        X x1;
        X x2;
    }
}


