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
package test.codesets;

import java.util.Properties;

import org.apache.yoko.util.Assert;
import org.omg.CORBA.*;

final public class Client {
    public static int run(ORB orb, String[] args)
            throws org.omg.CORBA.UserException {
        org.omg.CORBA.Object obj = orb
                .string_to_object("relfile:TestCodeSets.ref");
        if (obj == null) {
            System.out.println("Can't read IOR from TestCodeSets.ref");
            return 0;
        }

        TestCodeSets p = TestCodeSetsHelper.narrow(obj);
        Assert.ensure(p != null);

        {
            char wc, wcRet;

            wc = 'a';
            wcRet = p.testWChar(wc);
            Assert.ensure(wc == wcRet);

            wc = (char) 0x1234;
            wcRet = p.testWChar(wc);
            Assert.ensure(wc == wcRet);
        }

        {
            String ws, wsRet;

            ws = "Hello, World!";
            wsRet = p.testWString(ws);
            Assert.ensure(ws.equals(wsRet));
        }

        p.deactivate();

        return 0;
    }

    public static void main(String[] args) {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");

        int status = 0;
        ORB orb = null;
        try {
            orb = ORB.init(args, props);
            status = run(orb, args);
        } catch (Exception ex) {
            ex.printStackTrace();
            status = 1;
        }

        if (orb != null) {
            try {
                orb.destroy();
            } catch (Exception ex) {
                ex.printStackTrace();
                status = 1;
            }
        }

        System.exit(status);
    }
}
