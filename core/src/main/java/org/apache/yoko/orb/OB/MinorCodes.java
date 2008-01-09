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

package org.apache.yoko.orb.OB;

import org.omg.CORBA.OMGVMCID;
import org.apache.yoko.ApacheVMCID;

public final class MinorCodes {
    public final static int OMGVMCID = org.omg.CORBA.OMGVMCID.value;

    public final static int OOCVMCID = org.apache.yoko.ApacheVMCID.value;

    // ----------------------------------------------------------------------
    // Minor error codes for INITIALIZE
    // ----------------------------------------------------------------------

    public final static int MinorORBDestroyed = 1 | OOCVMCID;

    public static String describeInitialize(int minor) {
        String result = null;

        switch (minor) {
        case MinorORBDestroyed:
            result = "ORB already destroyed";
            break;
        }

        return result;
    }

    // ----------------------------------------------------------------------
    // Minor error codes for NO_IMPLEMENT
    // ----------------------------------------------------------------------

    //
    // Standard minor codes
    //
    public final static int MinorMissingLocalValueImplementation = 1 | OMGVMCID;

    public final static int MinorIncompatibleValueImplementationVersion = 2 | OMGVMCID;

    public final static int MinorNotSupportedByLocalObject = 3 | OMGVMCID;

    public final static int MinorDIINotSupportedByLocalObject = 4 | OMGVMCID;

    public static String describeNoImplement(int minor) {
        String result = null;

        switch (minor) {
        case MinorMissingLocalValueImplementation:
            result = "missing local value implementation";
            break;

        case MinorIncompatibleValueImplementationVersion:
            result = "incompatible value implementation version";
            break;

        case MinorNotSupportedByLocalObject:
            result = "operation not supported by local object";
            break;

        case MinorDIINotSupportedByLocalObject:
            result = "DII operation not supported by local object";
            break;
        }

        return result;
    }

    // ----------------------------------------------------------------------
    // Minor error codes for OBJECT_NOT_EXIST
    // ----------------------------------------------------------------------

    //
    // Standard minor codes
    //
    public final static int MinorUnregisteredValue = 1 | OMGVMCID;

    public final static int MinorCannotDispatch = 2 | OMGVMCID;

    public static String describeObjectNotExist(int minor) {
        String result = null;

        switch (minor) {
        case MinorUnregisteredValue:
            result = "attempt to pass an unregistered value as an object "
                    + "reference";
            break;

        case MinorCannotDispatch:
            result = "unable to dispatch - servant or POA not found";
            break;
        }

        return result;
    }

    // ----------------------------------------------------------------------
    // Minor error codes for BAD_PARAM
    // ----------------------------------------------------------------------

    //
    // Standard minor codes
    //
    public final static int MinorValueFactoryError = 1 | OMGVMCID;

    public final static int MinorRepositoryIdExists = 2 | OMGVMCID;

    public final static int MinorNameExists = 3 | OMGVMCID;

    public final static int MinorInvalidContainer = 4 | OMGVMCID;

    public final static int MinorNameClashInInheritedContext = 5 | OMGVMCID;

    public final static int MinorBadAbstractInterfaceType = 6 | OMGVMCID;

    public final static int MinorBadSchemeName = 7 | OMGVMCID;

    public final static int MinorBadAddress = 8 | OMGVMCID;

    public final static int MinorBadSchemeSpecificPart = 9 | OMGVMCID;

    public final static int MinorOther = 10 | OMGVMCID;

    public final static int MinorInvalidAbstractInterfaceInheritance = 11 | OMGVMCID;

    public final static int MinorInvalidValueInheritance = 12 | OMGVMCID;

    public final static int MinorIncompleteTypeCodeParameter = 13 | OMGVMCID;

    public final static int MinorInvalidObjectId = 14 | OMGVMCID;

    public final static int MinorInvalidName = 15 | OMGVMCID;

    public final static int MinorInvalidId = 16 | OMGVMCID;

    public final static int MinorInvalidMemberName = 17 | OMGVMCID;

    public final static int MinorDuplicateLabel = 18 | OMGVMCID;

    public final static int MinorIncompatibleLabelType = 19 | OMGVMCID;

    public final static int MinorInvalidDiscriminatorType = 20 | OMGVMCID;

    public final static int MinorNoExceptionInAny = 21 | OMGVMCID;

    public final static int MinorUnlistedUserException = 22 | OMGVMCID;

    public final static int MinorNoWcharCodeSet = 23 | OMGVMCID;

    public final static int MinorServiceContextIdOutOfRange = 24 | OMGVMCID;

    public final static int MinorEnumValueOutOfRange = 25 | OMGVMCID;

    public final static int MinorInvalidServiceContextId = 26 | OMGVMCID;

    public final static int MinorObjectIsNull = 27 | OMGVMCID;

    public final static int MinorInvalidComponentId = 28 | OMGVMCID;

    public final static int MinorInvalidProfileId = 29 | OMGVMCID;

    public final static int MinorDuplicatePolicyType = 30 | OMGVMCID;

    //
    // Yoko specific minor codes
    //
    public final static int MinorDuplicateDeclarator = 1 | OOCVMCID;

    public final static int MinorInvalidValueModifier = 2 | OOCVMCID;

    public final static int MinorDuplicateValueInit = 3 | OOCVMCID;

    public final static int MinorAbstractValueInit = 4 | OOCVMCID;

    public final static int MinorDuplicateBaseType = 5 | OOCVMCID;

    public final static int MinorSingleThreadedOnly = 6 | OOCVMCID;

    public final static int MinorNameRedefinitionInImmediateScope = 7 | OOCVMCID;

    public final static int MinorInvalidValueBoxType = 8 | OOCVMCID;

    public final static int MinorInvalidLocalInterfaceInheritance = 9 | OOCVMCID;

    public final static int MinorConstantTypeMismatch = 10 | OOCVMCID;

    public final static int MinorInvalidPattern = 11 | OOCVMCID;

    public final static int MinorInvalidScope = 12 | OOCVMCID;
    
    public final static int MinorInvalidContextID = 13 | OOCVMCID;
    
    public final static int MinorIncompatibleObjectType = 14 | OOCVMCID;
    
    public static String describeBadParam(int minor) {
        String result = null;

        switch (minor) {
        case MinorValueFactoryError:
            result = "failure to register, unregister or lookup value factory";
            break;

        case MinorRepositoryIdExists:
            result = "repository id already exists";
            break;

        case MinorNameExists:
            result = "name already exists";
            break;

        case MinorInvalidContainer:
            result = "target is not a valid container";
            break;

        case MinorNameClashInInheritedContext:
            result = "name clash in inherited context";
            break;

        case MinorBadAbstractInterfaceType:
            result = "incorrect type for abstract interface";
            break;

        case MinorBadSchemeName:
            result = "bad scheme name";
            break;

        case MinorBadAddress:
            result = "bad address";
            break;

        case MinorBadSchemeSpecificPart:
            result = "bad scheme specific part";
            break;

        case MinorOther:
            result = "other";
            break;

        case MinorInvalidAbstractInterfaceInheritance:
            result = "invalid abstract interface inheritance";
            break;

        case MinorInvalidValueInheritance:
            result = "invalid valuetype inheritance";
            break;

        case MinorIncompleteTypeCodeParameter:
            result = "incomplete TypeCode parameter";
            break;

        case MinorInvalidObjectId:
            result = "invalid object id";
            break;

        case MinorInvalidName:
            result = "invalid name in TypeCode operation";
            break;

        case MinorInvalidId:
            result = "invalid repository id in TypeCode operation";
            break;

        case MinorInvalidMemberName:
            result = "invalid member name in TypeCode operation";
            break;

        case MinorDuplicateLabel:
            result = "duplicate union label value";
            break;

        case MinorIncompatibleLabelType:
            result = "incompatible union label value";
            break;

        case MinorInvalidDiscriminatorType:
            result = "invalid union discriminator type";
            break;

        case MinorNoExceptionInAny:
            result = "exception does not contain an any";
            break;

        case MinorUnlistedUserException:
            result = "unlisted user exception";
            break;

        case MinorNoWcharCodeSet:
            result = "wchar transmission code set not in service context";
            break;

        case MinorServiceContextIdOutOfRange:
            result = "service context is not in OMG-defined range";
            break;

        case MinorEnumValueOutOfRange:
            result = "enum value out of range";
            break;

        case MinorInvalidServiceContextId:
            result = "invalid service context ID";
            break;

        case MinorObjectIsNull:
            result = "Object parameter to register_initial_reference is null";
            break;

        case MinorInvalidComponentId:
            result = "invalid component ID";
            break;

        case MinorInvalidProfileId:
            result = "invalid profile ID";
            break;

        case MinorDuplicatePolicyType:
            result = "duplicate policy types";
            break;

        case MinorDuplicateDeclarator:
            result = "duplicate declarator";
            break;

        case MinorInvalidValueModifier:
            result = "invalid valuetype modifier";
            break;

        case MinorDuplicateValueInit:
            result = "duplicate valuetype initializer";
            break;

        case MinorAbstractValueInit:
            result = "abstract valuetype cannot have initializers";
            break;

        case MinorDuplicateBaseType:
            result = "base type appears more than once";
            break;

        case MinorSingleThreadedOnly:
            result = "ORB doesn't support multiple threads";
            break;

        case MinorNameRedefinitionInImmediateScope:
            result = "invalid name redefinition in an immediate scope";
            break;

        case MinorInvalidValueBoxType:
            result = "invalid type for valuebox";
            break;

        case MinorInvalidLocalInterfaceInheritance:
            result = "invalid local interface inheritance";
            break;

        case MinorConstantTypeMismatch:
            result = "constant type doesn't match definition";
            break;

        case MinorInvalidPattern:
            result = "invalid pattern";
            break;

        case MinorInvalidScope:
            result = "invalid scope";
            break;

        case MinorInvalidContextID:
            result = "invalid context ID";
            break;

        case MinorIncompatibleObjectType:
            result = "incompatible object type";
            break;
        }

        return result;
    }

    // ----------------------------------------------------------------------
    // Minor error codes for BAD_INV_ORDER
    // ----------------------------------------------------------------------

    //
    // Standard minor codes
    //
    public final static int MinorDependencyPreventsDestruction = 1 | OMGVMCID;

    public final static int MinorIndestructibleObject = 2 | OMGVMCID;

    public final static int MinorDestroyWouldBlock = 3 | OMGVMCID;

    public final static int MinorShutdownCalled = 4 | OMGVMCID;

    public final static int MinorDuplicateSend = 5 | OMGVMCID;

    public final static int MinorServantManagerAlreadySet = 6 | OMGVMCID;

    public final static int MinorInvalidUseOfDSIArguments = 7 | OMGVMCID;

    public final static int MinorInvalidUseOfDSIContext = 8 | OMGVMCID;

    public final static int MinorInvalidUseOfDSIResult = 9 | OMGVMCID;

    public final static int MinorRequestAlreadySent = 10 | OMGVMCID;

    public final static int MinorRequestNotSent = 11 | OMGVMCID;

    public final static int MinorResponseAlreadyReceived = 12 | OMGVMCID;

    public final static int MinorSynchronousRequest = 13 | OMGVMCID;

    public final static int MinorInvalidPICall = 14 | OMGVMCID;

    public final static int MinorServiceContextExists = 15 | OMGVMCID;

    public final static int MinorPolicyFactoryExists = 16 | OMGVMCID;

    public final static int MinorNoCreatePOA = 17 | OMGVMCID;

    //
    // Yoko specific minor codes
    //
    public final static int MinorBadConcModel = 1 | OOCVMCID;

    public final static int MinorORBRunning = 2 | OOCVMCID;

    public static String describeBadInvOrder(int minor) {
        String result = null;

        switch (minor) {
        case MinorDependencyPreventsDestruction:
            result = "dependency prevents destruction of object";
            break;

        case MinorIndestructibleObject:
            result = "destroy invoked on indestructible object";
            break;

        case MinorDestroyWouldBlock:
            result = "operation would deadlock";
            break;

        case MinorShutdownCalled:
            result = "ORB has shutdown";
            break;

        case MinorDuplicateSend:
            result = "request has already been sent";
            break;

        case MinorServantManagerAlreadySet:
            result = "servant manager already set";
            break;

        case MinorInvalidUseOfDSIArguments:
            result = "invalid use of DSI arguments";
            break;

        case MinorInvalidUseOfDSIContext:
            result = "invalid use of DSI context";
            break;

        case MinorInvalidUseOfDSIResult:
            result = "invalid use of DSI result";
            break;

        case MinorRequestAlreadySent:
            result = "DII request has already been sent";
            break;

        case MinorRequestNotSent:
            result = "DII request has not been sent yet";
            break;

        case MinorResponseAlreadyReceived:
            result = "DII response has already been received";
            break;

        case MinorSynchronousRequest:
            result = "operation not supported on synchronous DII request";
            break;

        case MinorInvalidPICall:
            result = "invalid Portable Interceptor call";
            break;

        case MinorServiceContextExists:
            result = "a service context already exists with the given ID";
            break;

        case MinorPolicyFactoryExists:
            result = "a factory already exists for that PolicyType";
            break;

        case MinorNoCreatePOA:
            result = "cannot create POA while undergoing destruction";
            break;

        case MinorBadConcModel:
            result = "invalid concurrency model";
            break;

        case MinorORBRunning:
            result = "ORB::run already called";
            break;
        }

        return result;
    }

    // ----------------------------------------------------------------------
    // Minor error codes for COMM_FAILURE
    // ----------------------------------------------------------------------

    //
    // Standard minor codes
    //
    /* None yet */

    //
    // Yoko specific minor codes
    //
    public final static int MinorRecv = 1 | OOCVMCID;

    public final static int MinorSend = 2 | OOCVMCID;

    public final static int MinorRecvZero = 3 | OOCVMCID;

    public final static int MinorSendZero = 4 | OOCVMCID;

    public final static int MinorSocket = 5 | OOCVMCID;

    public final static int MinorSetsockopt = 6 | OOCVMCID;

    public final static int MinorGetsockopt = 7 | OOCVMCID;

    public final static int MinorBind = 8 | OOCVMCID;

    public final static int MinorListen = 9 | OOCVMCID;

    public final static int MinorConnect = 10 | OOCVMCID;

    public final static int MinorAccept = 11 | OOCVMCID;

    public final static int MinorSelect = 12 | OOCVMCID;

    public final static int MinorSetSoTimeout = 26 | OOCVMCID;

    public final static int MinorGetsockname = 27 | OOCVMCID;

    public final static int MinorGetpeername = 28 | OOCVMCID;

    public final static int MinorGethostname = 13 | OOCVMCID;

    public final static int MinorGethostbyname = 14 | OOCVMCID;

    public final static int MinorWSAStartup = 15 | OOCVMCID;

    public final static int MinorWSACleanup = 16 | OOCVMCID;

    public final static int MinorNoGIOP = 17 | OOCVMCID;

    public final static int MinorUnknownMessage = 18 | OOCVMCID;

    public final static int MinorWrongMessage = 19 | OOCVMCID;

    public final static int MinorMessageError = 21 | OOCVMCID;

    public final static int MinorFragment = 22 | OOCVMCID;

    public final static int MinorUnknownReqId = 24 | OOCVMCID;

    public final static int MinorVersion = 25 | OOCVMCID;

    public final static int MinorPipe = 23 | OOCVMCID;

    public final static int MinorUnknownReplyMessage = 29 | OOCVMCID;

    //
    // Yoko deprecated minor codes
    //
    public final static int MinorDeprecatedCloseConnection = 20 | OOCVMCID;

    public static String describeCommFailure(int minor) {
        String result = null;

        switch (minor) {
        case MinorRecv:
            result = "recv() failed";
            break;

        case MinorSend:
            result = "send() failed";
            break;

        case MinorRecvZero:
            result = "recv() returned zero";
            break;

        case MinorSendZero:
            result = "send() returned zero";
            break;

        case MinorSocket:
            result = "socket() failed";
            break;

        case MinorSetsockopt:
            result = "setsockopt() failed";
            break;

        case MinorGetsockopt:
            result = "getsockopt() failed";
            break;

        case MinorBind:
            result = "bind() failed";
            break;

        case MinorListen:
            result = "listen() failed";
            break;

        case MinorConnect:
            result = "connect() failed";
            break;

        case MinorAccept:
            result = "accept() failed";
            break;

        case MinorSelect:
            result = "select() failed";
            break;

        case MinorSetSoTimeout:
            result = "setSoTimeout() failed";
            break;

        case MinorGethostname:
            result = "gethostname() failed";
            break;

        case MinorGethostbyname:
            result = "gethostbyname() failed";
            break;

        case MinorWSAStartup:
            result = "WSAStartup() failed";
            break;

        case MinorWSACleanup:
            result = "WSACleanup() failed";
            break;

        case MinorNoGIOP:
            result = "not a GIOP message";
            break;

        case MinorUnknownMessage:
            result = "unknown GIOP message";
            break;

        case MinorWrongMessage:
            result = "wrong GIOP message";
            break;

        case MinorMessageError:
            result = "got a `MessageError' message";
            break;

        case MinorFragment:
            result = "invalid fragment message";
            break;

        case MinorUnknownReqId:
            result = "unknown request id";
            break;

        case MinorVersion:
            result = "unsupported GIOP version";
            break;

        case MinorPipe:
            result = "pipe() failed";
            break;

        case MinorUnknownReplyMessage:
            result = "unknown GIOP message in reply";
            break;

        case MinorDeprecatedCloseConnection:
            result = "got a `CloseConnection' message";
            break;
        }

        return result;
    }

    // ----------------------------------------------------------------------
    // Minor error codes for INTF_REPOS
    // ----------------------------------------------------------------------

    //
    // Standard minor codes
    //
    /* None yet */

    //
    // Yoko specific minor codes
    //
    public final static int MinorNoIntfRepos = 1 | OOCVMCID;

    public final static int MinorLookupAmbiguous = 2 | OOCVMCID;

    public final static int MinorIllegalRecursion = 3 | OOCVMCID;

    public final static int MinorNoEntry = 4 | OOCVMCID;

    public static String describeIntfRepos(int minor) {
        String result = null;

        switch (minor) {
        case MinorNoIntfRepos:
            result = "interface repository is not available";
            break;

        case MinorLookupAmbiguous:
            result = "search name for lookup() is ambiguous";
            break;

        case MinorIllegalRecursion:
            result = "illegal recursion";
            break;

        case MinorNoEntry:
            result = "repository ID not found";
            break;
        }

        return result;
    }

    // ----------------------------------------------------------------------
    // Minor error codes for MARSHAL
    // ----------------------------------------------------------------------

    //
    // Standard minor codes
    //
    public final static int MinorNoValueFactory = 1 | OMGVMCID;

    public final static int MinorDSIResultBeforeContext = 2 | OMGVMCID;

    public final static int MinorDSIInvalidParameterList = 3 | OMGVMCID;

    public final static int MinorLocalObject = 4 | OMGVMCID;

    public final static int MinorWcharSentByClient = 5 | OMGVMCID;

    public final static int MinorWcharSentByServer = 6 | OMGVMCID;

    //
    // Yoko specific minor codes
    //
    public final static int MinorReadOverflow = 1 | OOCVMCID;

    public final static int MinorReadBooleanOverflow = 2 | OOCVMCID;

    public final static int MinorReadCharOverflow = 3 | OOCVMCID;

    public final static int MinorReadWCharOverflow = 4 | OOCVMCID;

    public final static int MinorReadOctetOverflow = 5 | OOCVMCID;

    public final static int MinorReadShortOverflow = 6 | OOCVMCID;

    public final static int MinorReadUShortOverflow = 7 | OOCVMCID;

    public final static int MinorReadLongOverflow = 8 | OOCVMCID;

    public final static int MinorReadULongOverflow = 9 | OOCVMCID;

    public final static int MinorReadLongLongOverflow = 10 | OOCVMCID;

    public final static int MinorReadULongLongOverflow = 11 | OOCVMCID;

    public final static int MinorReadFloatOverflow = 12 | OOCVMCID;

    public final static int MinorReadDoubleOverflow = 13 | OOCVMCID;

    public final static int MinorReadLongDoubleOverflow = 14 | OOCVMCID;

    public final static int MinorReadStringOverflow = 15 | OOCVMCID;

    public final static int MinorReadStringZeroLength = 16 | OOCVMCID;

    public final static int MinorReadStringNullChar = 17 | OOCVMCID;

    public final static int MinorReadStringNoTerminator = 18 | OOCVMCID;

    public final static int MinorReadWStringOverflow = 19 | OOCVMCID;

    public final static int MinorReadWStringZeroLength = 20 | OOCVMCID;

    public final static int MinorReadWStringNullWChar = 21 | OOCVMCID;

    public final static int MinorReadWStringNoTerminator = 22 | OOCVMCID;

    public final static int MinorReadFixedOverflow = 23 | OOCVMCID;

    public final static int MinorReadFixedInvalid = 24 | OOCVMCID;

    public final static int MinorReadBooleanArrayOverflow = 25 | OOCVMCID;

    public final static int MinorReadCharArrayOverflow = 26 | OOCVMCID;

    public final static int MinorReadWCharArrayOverflow = 27 | OOCVMCID;

    public final static int MinorReadOctetArrayOverflow = 28 | OOCVMCID;

    public final static int MinorReadShortArrayOverflow = 29 | OOCVMCID;

    public final static int MinorReadUShortArrayOverflow = 30 | OOCVMCID;

    public final static int MinorReadLongArrayOverflow = 31 | OOCVMCID;

    public final static int MinorReadULongArrayOverflow = 32 | OOCVMCID;

    public final static int MinorReadLongLongArrayOverflow = 33 | OOCVMCID;

    public final static int MinorReadULongLongArrayOverflow = 34 | OOCVMCID;

    public final static int MinorReadFloatArrayOverflow = 35 | OOCVMCID;

    public final static int MinorReadDoubleArrayOverflow = 36 | OOCVMCID;

    public final static int MinorReadLongDoubleArrayOverflow = 37 | OOCVMCID;

    public final static int MinorReadInvTypeCodeIndirection = 38 | OOCVMCID;

    public final static int MinorLongDoubleNotSupported = 40 | OOCVMCID;
    
    public final static int MinorNativeNotSupported = 41 | OOCVMCID;

    public final static int MinorReadInvalidIndirection = 42 | OOCVMCID;

    public final static int MinorReadIDMismatch = 43 | OOCVMCID;

    public final static int MinorReadUnsupported = 44 | OOCVMCID;

    public final static int MinorWriteUnsupported = 45 | OOCVMCID;
    
    public final static int MinorLoadStub = 46 | OOCVMCID;

    //
    // Yoko deprecated minor codes
    //
    public final static int MinorDeprecatedWriteObjectLocal = 39 | OOCVMCID;

    public static String describeMarshal(int minor) {
        String result = null;

        switch (minor) {
        case MinorNoValueFactory:
            result = "no valuetype factory";
            break;

        case MinorDSIResultBeforeContext:
            result = "DSI result cannot be set before context";
            break;

        case MinorDSIInvalidParameterList:
            result = "DSI argument list does not describe all parameters";
            break;

        case MinorLocalObject:
            result = "attempt to marshal local object";
            break;

        case MinorWcharSentByClient:
            result = "wchar data sent by client on GIOP 1.0 connection";
            break;

        case MinorWcharSentByServer:
            result = "wchar data returned by server on GIOP 1.0 connection";
            break;

        case MinorReadOverflow:
            result = "input stream buffer overflow";
            break;

        case MinorReadBooleanOverflow:
            result = "overflow while reading boolean";
            break;

        case MinorReadCharOverflow:
            result = "overflow while reading char";
            break;

        case MinorReadWCharOverflow:
            result = "overflow while reading wchar";
            break;

        case MinorReadOctetOverflow:
            result = "overflow while reading octet";
            break;

        case MinorReadShortOverflow:
            result = "overflow while reading short";
            break;

        case MinorReadUShortOverflow:
            result = "overflow while reading ushort";
            break;

        case MinorReadLongOverflow:
            result = "overflow while reading long";
            break;

        case MinorReadULongOverflow:
            result = "overflow while reading ulong";
            break;

        case MinorReadLongLongOverflow:
            result = "overflow while reading longlong";
            break;

        case MinorReadULongLongOverflow:
            result = "overflow while reading ulonglong";
            break;

        case MinorReadFloatOverflow:
            result = "overflow while reading float";
            break;

        case MinorReadDoubleOverflow:
            result = "overflow while reading double";
            break;

        case MinorReadLongDoubleOverflow:
            result = "overflow while reading longdouble";
            break;

        case MinorReadStringOverflow:
            result = "overflow while reading string";
            break;

        case MinorReadStringZeroLength:
            result = "encountered zero-length string";
            break;

        case MinorReadStringNullChar:
            result = "encountered null char in string";
            break;

        case MinorReadStringNoTerminator:
            result = "terminating null char missing in string";
            break;

        case MinorReadWStringOverflow:
            result = "overflow while reading wstring";
            break;

        case MinorReadWStringZeroLength:
            result = "encountered zero-length wstring";
            break;

        case MinorReadWStringNullWChar:
            result = "encountered null wchar in wstring";
            break;

        case MinorReadWStringNoTerminator:
            result = "terminating null wchar missing in wstring";
            break;

        case MinorReadFixedOverflow:
            result = "overflow while reading fixed";
            break;

        case MinorReadFixedInvalid:
            result = "invalid encoding for fixed value";
            break;

        case MinorReadBooleanArrayOverflow:
            result = "overflow while reading boolean array";
            break;

        case MinorReadCharArrayOverflow:
            result = "overflow while reading char array";
            break;

        case MinorReadWCharArrayOverflow:
            result = "overflow while reading wchar array";
            break;

        case MinorReadOctetArrayOverflow:
            result = "overflow while reading octet array";
            break;

        case MinorReadShortArrayOverflow:
            result = "overflow while reading short array";
            break;

        case MinorReadUShortArrayOverflow:
            result = "overflow while reading ushort array";
            break;

        case MinorReadLongArrayOverflow:
            result = "overflow while reading long array";
            break;

        case MinorReadULongArrayOverflow:
            result = "overflow while reading ulong array";
            break;

        case MinorReadLongLongArrayOverflow:
            result = "overflow while reading longlong array";
            break;

        case MinorReadULongLongArrayOverflow:
            result = "overflow while reading ulonglong array";
            break;

        case MinorReadFloatArrayOverflow:
            result = "overflow while reading float array";
            break;

        case MinorReadDoubleArrayOverflow:
            result = "overflow while reading double array";
            break;

        case MinorReadLongDoubleArrayOverflow:
            result = "overflow while reading longdouble array";
            break;

        case MinorReadInvTypeCodeIndirection:
            result = "invalid TypeCode indirection";
            break;

        case MinorLongDoubleNotSupported:
            result = "long double is not supported";
            break;

        case MinorNativeNotSupported:
            result = "long double is not supported";
            break;

        case MinorDeprecatedWriteObjectLocal:
            result = "attempt to marshal a locality-constrained object";
            break;

        case MinorReadInvalidIndirection:    
            result = "invalid indirection location";
            break;

        case MinorReadIDMismatch:    
            result = "type ID mismatch";
            break;

        case MinorReadUnsupported:    
            result = "reading unsupported type";
            break;

        case MinorWriteUnsupported:    
            result = "writing unsupported type";
            break;

        case MinorLoadStub:    
            result = "error loading stub class";
            break;
        }

        return result;
    }

    // ----------------------------------------------------------------------
    // Minor error codes for IMP_LIMIT
    // ----------------------------------------------------------------------

    //
    // Standard minor codes
    //
    public final static int MinorNoUsableProfile = 1 | OMGVMCID;

    //
    // Yoko specific minor codes
    //
    public final static int MinorMessageSizeLimit = 1 | OOCVMCID;

    public final static int MinorThreadLimit = 2 | OOCVMCID;

    public static String describeImpLimit(int minor) {
        String result = null;

        switch (minor) {
        case MinorNoUsableProfile:
            result = "no usable profile in IOR";
            break;

        case MinorMessageSizeLimit:
            result = "maximum message size exceeded";
            break;

        case MinorThreadLimit:
            result = "can't create new thread";
            break;
        }

        return result;
    }

    // ----------------------------------------------------------------------
    // Minor error codes for NO_MEMORY
    // ----------------------------------------------------------------------

    //
    // Standard minor codes
    //
    /* None yet */

    //
    // Yoko specific minor codes
    //
    public final static int MinorAllocationFailure = 1 | OOCVMCID;

    public static String describeNoMemory(int minor) {
        String result = null;

        switch (minor) {
        case MinorAllocationFailure:
            result = "memory allocation failure";
            break;
        }

        return result;
    }

    // ----------------------------------------------------------------------
    // Minor error codes for TRANSIENT
    // ----------------------------------------------------------------------

    //
    // PortableInterceptor minor codes
    //
    public final static int MinorRequestDiscarded = 1 | OMGVMCID;

    public final static int MinorNoUsableProfileInIOR = 2 | OMGVMCID;

    public final static int MinorRequestCancelled = 3 | OMGVMCID;

    public final static int MinorPOADestroyed = 4 | OMGVMCID;

    //
    // Yoko specific minor codes
    //
    public final static int MinorConnectFailed = 1 | OOCVMCID;

    public final static int MinorCloseConnection = 2 | OOCVMCID;

    public final static int MinorActiveConnectionManagement = 3 | OOCVMCID;

    public final static int MinorForcedShutdown = 4 | OOCVMCID;

    public final static int MinorLocationForwardHopCountExceeded = 5 | OOCVMCID;

    public static String describeTransient(int minor) {
        String result = null;

        switch (minor) {
        case MinorRequestDiscarded:
            result = "request has been discarded";
            break;

        case MinorNoUsableProfileInIOR:
            result = "no usable profile in IOR";
            break;

        case MinorRequestCancelled:
            result = "request has been cancelled";
            break;

        case MinorPOADestroyed:
            result = "POA has been destroyed";
            break;

        case MinorConnectFailed:
            result = "attempt to establish connection failed";
            break;

        case MinorCloseConnection:
            result = "got a `CloseConnection' message";
            break;

        case MinorActiveConnectionManagement:
            result = "active connection management closed connection";
            break;

        case MinorForcedShutdown:
            result = "forced connection shutdown because of timeout";
            break;

        case MinorLocationForwardHopCountExceeded:
            result = "maximum forwarding count (10) exceeded";
            break;
        }

        return result;
    }

    // ----------------------------------------------------------------------
    // Minor error codes for NO_RESOURCES
    // ----------------------------------------------------------------------

    //
    // PortableInterceptor minor codes
    //
    public final static int MinorInvalidBinding = 1 | OMGVMCID;

    public static String describeNoResources(int minor) {
        String result = null;

        switch (minor) {
        case MinorInvalidBinding:
            result = "Portable Interceptor operation not supported in "
                    + "binding";
            break;
        }

        return result;
    }

    // ----------------------------------------------------------------------
    // Minor error codes for UNKNOWN
    // ----------------------------------------------------------------------

    //
    // Standard minor codes
    //
    public final static int MinorUnknownUserException = 1 | OMGVMCID;

    public final static int MinorSystemExceptionNotSupported = 2 | OMGVMCID;

    public static String describeUnknown(int minor) {
        String result = null;

        switch (minor) {
        case MinorUnknownUserException:
            result = "an unknown user exception was raised";
            break;

        case MinorSystemExceptionNotSupported:
            result = "an unsupported system exception was raised";
            break;
        }

        return result;
    }

    // ----------------------------------------------------------------------
    // Minor error codes for INV_POLICY
    // ----------------------------------------------------------------------

    //
    // Standard minor codes
    //
    public final static int MinorCannotReconcilePolicy = 1 | OMGVMCID;

    public final static int MinorInvalidPolicyType = 2 | OMGVMCID;

    public final static int MinorNoPolicyFactory = 3 | OMGVMCID;

    //
    // Yoko specific minor codes
    //
    public final static int MinorNoPolicy = 1 | OOCVMCID;

    public static String describeInvPolicy(int minor) {
        String result = null;

        switch (minor) {
        case MinorCannotReconcilePolicy:
            result = "cannot reconcile IOR policy with effective policy "
                    + "override";
            break;

        case MinorInvalidPolicyType:
            result = "invalid PolicyType";
            break;

        case MinorNoPolicyFactory:
            result = "no PolicyFactory for the PolicyType has been registered";
            break;

        case MinorNoPolicy:
            result = "no policy for the PolicyType is available";
            break;
        }

        return result;
    }

    // ----------------------------------------------------------------------
    // Minor error codes for INV_OBJREF
    // ----------------------------------------------------------------------

    //
    // Standard minor codes
    //
    public final static int MinorNoWcharSupport = 1 | OMGVMCID;

    public final static int MinorWcharCodeSetRequired = 2 | OMGVMCID;

    public static String describeInvObjref(int minor) {
        String result = null;

        switch (minor) {
        case MinorNoWcharSupport:
            result = "wchar code set support not specified";
            break;

        case MinorWcharCodeSetRequired:
            result = "code set component required for wchar/wstring";
            break;
        }

        return result;
    }

    // ----------------------------------------------------------------------
    // Minor error codes for BAD_TYPECODE
    // ----------------------------------------------------------------------

    //
    // Standard minor codes
    //
    public final static int MinorIncompleteTypeCode = 1 | OMGVMCID;

    public final static int MinorInvalidMemberType = 2 | OMGVMCID;

    //
    // Yoko specific minor codes
    //
    public final static int MinorInvalidUnionDiscriminator = 1 | OOCVMCID;
    
    public final static int MinorInvalidPropertyType = 2 | OOCVMCID; 

    public static String describeBadTypecode(int minor) {
        String result = null;

        switch (minor) {
        case MinorIncompleteTypeCode:
            result = "attempt to marshal incomplete TypeCode";
            break;

        case MinorInvalidMemberType:
            result = "invalid member type in TypeCode operation";
            break;
            
        case MinorInvalidUnionDiscriminator:
            result = "invalid union discriminator type";
            break;

        case MinorInvalidPropertyType:
            result = "property value does not contain a string";
            break;
        }

        return result;
    }

    // ----------------------------------------------------------------------
    // Minor error codes for OBJ_ADAPTER
    // ----------------------------------------------------------------------

    //
    // Standard minor codes
    //
    public final static int MinorSystemExceptionInUnknownAdapter = 1 | OMGVMCID;

    public final static int MinorServantNotFound = 2 | OMGVMCID;

    public final static int MinorNoDefaultServant = 3 | OMGVMCID;

    public final static int MinorNoServantManager = 4 | OMGVMCID;

    public final static int MinorIncarnateViolatedPOAPolicy = 5 | OMGVMCID;

    public static String describeObjAdapter(int minor) {
        String result = null;

        switch (minor) {
        case MinorSystemExceptionInUnknownAdapter:
            result = "unknown_adapter raised a system exception";
            break;

        case MinorServantNotFound:
            result = "servant not found";
            break;

        case MinorNoDefaultServant:
            result = "no default servant available";
            break;

        case MinorNoServantManager:
            result = "no servant manager available";
            break;

        case MinorIncarnateViolatedPOAPolicy:
            result = "incarnate violated a POA policy";
            break;
        }

        return result;
    }

    // ----------------------------------------------------------------------
    // Minor error codes for DATA_CONVERSION
    // ----------------------------------------------------------------------

    //
    // Standard minor codes
    //
    public final static int MinorNoCharacterMapping = 1 | OMGVMCID;

    //
    // Yoko specific minor codes
    //
    
    public final static int MinorNoAlias = 1 | OOCVMCID;
    
    public final static int MinorUTF8Overflow = 2 | OOCVMCID;
    
    public final static int MinorUTF8Encoding = 3 | OOCVMCID;

    public static String describeDataConversion(int minor) {
        String result = null;

        switch (minor) {
        case MinorNoCharacterMapping:
            result = "character does not map to negotiated transmission "
                    + "code set";
            break;
        case MinorNoAlias:
            result = "alias types not supported";
            break; 
        case MinorUTF8Overflow:
            result = "UTF8 overflow";
            break; 
        case MinorUTF8Encoding:
            result = "invalid UTF8 character1";
            break; 
        }

        return result;
    }

    // ----------------------------------------------------------------------
    // Minor error codes for BAD_OPERATION
    // ----------------------------------------------------------------------

    //
    // Standard minor codes
    //

    //
    // Yoko specific minor codes
    //
    public final static int MinorTypeMismatch = 1 | OOCVMCID;

    public final static int MinorNullValueNotAllowed = 2 | OOCVMCID;

    public static String describeBadOperation(int minor) {
        String result = null;

        switch (minor) {
        case MinorTypeMismatch:
            result = "Type mismatch";
            break;

        case MinorNullValueNotAllowed:
            result = "Null value not allowed";
            break;
        }

        return result;
    }

    // ----------------------------------------------------------------------
    // Minor error codes for BAD_CONTEXT   
    // ----------------------------------------------------------------------

    //
    // Standard minor codes
    //

    //
    // Yoko specific minor codes
    //
    public final static int MinorNoPatternMatch = 1 | OOCVMCID;

    public static String describeBadContext(int minor) {
        String result = null;

        switch (minor) {
        case MinorNoPatternMatch:
            result = "No match found";
            break;
        }

        return result;
    }
}
