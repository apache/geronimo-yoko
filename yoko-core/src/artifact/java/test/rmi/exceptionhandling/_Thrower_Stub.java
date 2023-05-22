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

import java.lang.String;
import java.lang.Throwable;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ServantObject;

public class _Thrower_Stub extends Stub implements Thrower {
    
    private static final String[] _type_ids = {
        "RMI:test.rmi.exceptionhandling.Thrower:0000000000000000"
    };
    
    public String[] _ids() { 
        return (String [] )  _type_ids.clone();
    }
    
    public void throwAppException() throws RemoteException, MyAppException {
        while(true) {
            if (!Util.isLocal(this)) {
                org.omg.CORBA_2_3.portable.InputStream in = null;
                try {
                    try {
                        OutputStream out = _request("throwAppException", true);
                        _invoke(out);
                        return;
                    } catch (ApplicationException ex) {
                        in = (org.omg.CORBA_2_3.portable.InputStream) ex.getInputStream();
                        String id = in.read_string();
                        if (id.equals("IDL:test/rmi/exceptionhandling/MyAppEx:1.0")) {
                            throw (MyAppException) in.read_value(MyAppException.class);
                        }
                        throw new UnexpectedException(id);
                    } catch (RemarshalException ex) {
                        continue;
                    }
                } catch (SystemException ex) {
                    throw Util.mapSystemException(ex);
                } finally {
                    _releaseReply(in);
                }
            } else {
                ServantObject so = _servant_preinvoke("throwAppException",test.rmi.exceptionhandling.Thrower.class);
                if (so == null) {
                    continue;
                }
                try {
                    ((test.rmi.exceptionhandling.Thrower)so.servant).throwAppException();
                    return;
                } catch (Throwable ex) {
                    Throwable exCopy = (Throwable)Util.copyObject(ex,_orb());
                    if (exCopy instanceof MyAppException) {
                        throw (MyAppException)exCopy;
                    }
                    throw Util.wrapException(exCopy);
                } finally {
                    _servant_postinvoke(so);
                }
            }
        }
    }
    
    public void throwRuntimeException() throws RemoteException {
        while(true) {
            if (!Util.isLocal(this)) {
                InputStream in = null;
                try {
                    try {
                        OutputStream out = _request("throwRuntimeException", true);
                        _invoke(out);
                        return;
                    } catch (ApplicationException ex) {
                        in = ex.getInputStream();
                        String id = in.read_string();
                        throw new UnexpectedException(id);
                    } catch (RemarshalException ex) {
                        continue;
                    }
                } catch (SystemException ex) {
                    throw Util.mapSystemException(ex);
                } finally {
                    _releaseReply(in);
                }
            } else {
                ServantObject so = _servant_preinvoke("throwRuntimeException",test.rmi.exceptionhandling.Thrower.class);
                if (so == null) {
                    continue;
                }
                try {
                    ((test.rmi.exceptionhandling.Thrower)so.servant).throwRuntimeException();
                    return;
                } catch (Throwable ex) {
                    Throwable exCopy = (Throwable)Util.copyObject(ex,_orb());
                    throw Util.wrapException(exCopy);
                } finally {
                    _servant_postinvoke(so);
                }
            }
        }
    }
}
