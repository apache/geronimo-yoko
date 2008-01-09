/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.yoko.bindings.corba.interceptors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.WebFault;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.databinding.DataWriter;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.FaultInfo;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.service.model.MessagePartInfo;
import org.apache.cxf.service.model.OperationInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.service.model.ServiceModelUtil;
import org.apache.schemas.yoko.bindings.corba.OperationType;
import org.apache.schemas.yoko.bindings.corba.RaisesType;

import org.apache.yoko.bindings.corba.CorbaBindingException;
import org.apache.yoko.bindings.corba.CorbaDestination;
import org.apache.yoko.bindings.corba.CorbaMessage;
import org.apache.yoko.bindings.corba.CorbaStreamable;
import org.apache.yoko.bindings.corba.CorbaTypeMap;
import org.apache.yoko.bindings.corba.runtime.CorbaFaultStreamWriter;
import org.apache.yoko.bindings.corba.types.CorbaObjectHandler;

import org.apache.yoko.wsdl.CorbaConstants;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ServerRequest;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.SystemExceptionHelper;

public class CorbaStreamFaultOutInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final Logger LOG = LogUtils.getL7dLogger(CorbaStreamFaultOutInterceptor.class);
    private CorbaTypeMap typeMap;
    private ServiceInfo service;
    private ORB orb;

    public CorbaStreamFaultOutInterceptor() {
        super(Phase.MARSHAL);
    }

    public void handleMessage(Message msg) {
        CorbaMessage message = (CorbaMessage) msg;
        Exchange exchange = message.getExchange();
        CorbaDestination destination;
        if (message.getDestination() != null) {
            destination = (CorbaDestination)message.getDestination();
        } else {
            destination = (CorbaDestination)exchange.getDestination();
        }

        orb = (ORB) message.get(CorbaConstants.ORB);
        if (orb == null) {
            orb = (ORB) exchange.get(ORB.class); 
        }

        service = exchange.get(ServiceInfo.class);
        typeMap = message.getCorbaTypeMap();
        
        DataWriter<XMLStreamWriter> writer = getDataWriter(message);

        Throwable ex = message.getContent(Exception.class);
        ex = ex.getCause();
        if (ex instanceof InvocationTargetException) {
            ex = ex.getCause();
        }

        if (ex instanceof SystemException) {
            setSystemException(message, ex);
            return;
        }

        //REVISIT, we should not have to depend on WebFault annotation
        //Try changing the fault name to the proper mangled java exception classname.
        WebFault fault = ex.getClass().getAnnotation(WebFault.class);
        if (fault == null) {
            throw new CorbaBindingException(ex);
        }
        String exClassName = fault.name();

        // Get information about the operation being invoked from the WSDL
        // definition.
        // We need this to marshal data correctly

        BindingInfo bInfo = destination.getBindingInfo();
        InterfaceInfo info = bInfo.getInterface();        
        
        String opName = message.getExchange().get(String.class);
                
        Iterator iter = bInfo.getOperations().iterator();

        BindingOperationInfo bopInfo = null;
        OperationType opType = null;           
        while (iter.hasNext()) {
            bopInfo = (BindingOperationInfo)iter.next();
            if (bopInfo.getName().getLocalPart().equals(opName)) {
                opType = bopInfo.getExtensor(OperationType.class);
                break;
            }
        }
        if (opType == null) {
            throw new CorbaBindingException("Unable to find binding operation for " + opName);
        }

        OperationInfo opInfo = bopInfo.getOperationInfo();

        RaisesType exType = null;
        List<RaisesType> exList = opType.getRaises();
        for (Iterator<RaisesType> i = exList.iterator(); i.hasNext();) {
            // REVISIT: Note that this assumes that exception names need to
            // be unique. We should make
            // sure that this is really the case.
            RaisesType raises = i.next();
            if (raises.getException().getLocalPart().equals(exClassName)) {
                exType = raises;
                break;
            }
        }
        try {
            if (exType != null) {
                setUserException(message, ex, exType, opInfo, writer);
            } else {
                throw new CorbaBindingException(ex);
            }
        } catch (Exception exp) {
            throw new CorbaBindingException(exp);
        }
    }

    protected void setSystemException(CorbaMessage message,
                                      Throwable ex) {
        SystemException sysEx = (SystemException)ex;
        message.setSystemException(sysEx);
        ServerRequest request  = message.getExchange().get(ServerRequest.class);
        Any exAny = orb.create_any();
        SystemExceptionHelper.insert(exAny, sysEx);
        request.set_exception(exAny);
    }

    protected void setUserException(CorbaMessage message,
                                    Throwable ex,
                                    RaisesType exType,
                                    OperationInfo opInfo,
                                    DataWriter<XMLStreamWriter> writer)
        throws Exception {
        QName exIdlType = exType.getException();
        QName elName = new QName("", exIdlType.getLocalPart());
        MessagePartInfo faultPart = getFaultMessagePartInfo(opInfo, elName);
        if (faultPart == null) {
            throw new CorbaBindingException("Coulnd't find the message fault part : " + elName);
        }

        Method faultMethod = ex.getClass().getMethod("getFaultInfo");
        if (faultMethod == null) {
            return;
        }
        Object fault = faultMethod.invoke(ex);

        // This creates a default instance of the class representing the exception schema type if
        // one has not been created on the servant side which throws the UserException.
        if (fault == null) {
            Class faultClass = faultMethod.getReturnType();
            fault = faultClass.newInstance();
        }
        
        CorbaFaultStreamWriter faultWriter = new CorbaFaultStreamWriter(orb, exType, typeMap, service);
        writer.write(fault, faultPart, faultWriter);

        CorbaObjectHandler[] objs = faultWriter.getCorbaObjects();      
        CorbaStreamable streamable = message.createStreamableObject(objs[0], elName);
        message.setStreamableException(streamable);
    }

    protected DataWriter<XMLStreamWriter> getDataWriter(CorbaMessage message) {
        Service serviceModel = ServiceModelUtil.getService(message.getExchange());

        DataWriter<XMLStreamWriter> dataWriter = 
            serviceModel.getDataBinding().createWriter(XMLStreamWriter.class);
        if (dataWriter == null) {
            throw new CorbaBindingException("Couldn't create data writer for outgoing fault message");
        }
        return dataWriter;
    }

    protected MessagePartInfo getFaultMessagePartInfo(OperationInfo opInfo, QName faultName) {
        Iterator<FaultInfo> faults = opInfo.getFaults().iterator();
        while (faults.hasNext()) {
            FaultInfo fault = faults.next();
            if (fault.getFaultName().getLocalPart().equals(faultName.getLocalPart())) {
                return fault.getMessageParts().get(0);
            }
        }
        return null;
    }
    

}
