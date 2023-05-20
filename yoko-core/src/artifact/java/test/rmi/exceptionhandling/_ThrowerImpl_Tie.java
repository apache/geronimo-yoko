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
package test.rmi.exceptionhandling;

import java.lang.ClassCastException;
import java.lang.String;
import java.lang.Throwable;
import java.rmi.Remote;
import javax.rmi.CORBA.Tie;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.UnknownException;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

public class _ThrowerImpl_Tie extends org.omg.PortableServer.Servant implements Tie {
    
    private ThrowerImpl target = null;
    
    private static final String[] _type_ids = {
        "RMI:test.rmi.exceptionhandling.Thrower:0000000000000000"
    };
    
    public void setTarget(Remote target) {
        this.target = (ThrowerImpl) target;
    }
    
    public Remote getTarget() {
        return target;
    }
    
    public Object thisObject() {
        return _this_object();
    }
    
    public void deactivate() {
        try {
            _poa().deactivate_object(_poa().servant_to_id(this));
        }
        catch(WrongPolicy e) { }
        catch(ObjectNotActive e) { }
        catch(ServantNotActive e) { }
    }
    
    public ORB orb() {
        return _orb();
    }
    
    public void orb(ORB orb) {
        try {
            ((org.omg.CORBA_2_3.ORB)orb).set_delegate(this);
        }
        catch(ClassCastException e) {
            throw new BAD_PARAM("POA Servant needs an org.omg.CORBA_2_3.ORB");
        }
    }
    
    public String[] _all_interfaces(POA poa, byte[] objectId) { 
        return (String [] )  _type_ids.clone();
    }
    
    public OutputStream _invoke(String method, InputStream _in, ResponseHandler reply) throws SystemException {
        try {
            org.omg.CORBA_2_3.portable.InputStream in = 
                (org.omg.CORBA_2_3.portable.InputStream) _in;
            switch (method.length()) {
                case 17: 
                    if (method.equals("throwAppException")) {
                        return throwAppException(in, reply);
                    }
                case 21: 
                    if (method.equals("throwRuntimeException")) {
                        return throwRuntimeException(in, reply);
                    }
            }
            throw new BAD_OPERATION();
        } catch (SystemException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new UnknownException(ex);
        }
    }
    
    private OutputStream throwAppException(org.omg.CORBA_2_3.portable.InputStream in , ResponseHandler reply) throws Throwable {
        try {
            target.throwAppException();
        } catch (MyAppException ex) {
            String id = "IDL:test/rmi/exceptionhandling/MyAppEx:1.0";
            org.omg.CORBA_2_3.portable.OutputStream out = 
                (org.omg.CORBA_2_3.portable.OutputStream) reply.createExceptionReply();
            out.write_string(id);
            out.write_value(ex,MyAppException.class);
            return out;
        }
        OutputStream out = reply.createReply();
        return out;
    }
    
    private OutputStream throwRuntimeException(org.omg.CORBA_2_3.portable.InputStream in , ResponseHandler reply) throws Throwable {
        target.throwRuntimeException();
        OutputStream out = reply.createReply();
        return out;
    }
}
