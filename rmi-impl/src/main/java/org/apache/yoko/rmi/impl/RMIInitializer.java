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

package org.apache.yoko.rmi.impl;

import java.util.logging.Logger;
import java.util.logging.Level;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.UserException;
import org.omg.IOP.Codec;
import org.omg.IOP.CodecFactory;
import org.omg.IOP.ENCODING_CDR_ENCAPS;
import org.omg.IOP.Encoding;
import org.omg.PortableInterceptor.ORBInitInfo;
import org.omg.PortableInterceptor.ORBInitializer;

public class RMIInitializer extends LocalObject implements ORBInitializer {
    static final Logger logger = Logger.getLogger(RMIInitializer.class
            .getName());

    public void pre_init(ORBInitInfo info) {
        Encoding encoding = new Encoding(ENCODING_CDR_ENCAPS.value, (byte)1,(byte) 0);
        CodecFactory codecFactory = info.codec_factory();
        try {
            Codec codec = codecFactory.create_codec(encoding);
            RMIInterceptor rmiInterceptor = new RMIInterceptor(codec);
            info.add_ior_interceptor(rmiInterceptor);
        } catch (UserException e) {
            logger.log(Level.FINER, "cannot register RMI Interceptor" + e.getMessage(), e);
        }
    }

    public void post_init(ORBInitInfo info) {
    }
}
