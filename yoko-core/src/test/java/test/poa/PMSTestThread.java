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

package test.poa;

import java.util.concurrent.CountDownLatch;

final class PMSTestThread extends Thread {
    private final Test test_;

    private final CountDownLatch startLatch = new CountDownLatch(1);
    public volatile Result result = null;

    public enum Result { SUCCESS, FAILURE, ERROR };

    public void run() {
        startLatch.countDown();
        try {
            test_.aMethod();
            result = Result.SUCCESS;
        } catch (org.omg.CORBA.TRANSIENT ex) {
            result = Result.FAILURE;
            return;
        } catch (org.omg.CORBA.SystemException ex) {
            result = Result.ERROR;
            System.err.println("Unexpected: " + ex);
        }
    }

    public void waitForStart() {
        do {
            try {
                startLatch.await();
                return;
            } catch (InterruptedException ie) {}
        } while (true);
    }

    public void waitForEnd() {
        while (isAlive()) {
            try {
                join();
            } catch (InterruptedException ie) {}
        }
    }

    PMSTestThread(Test test) {
        test_ = test;
    }
}
