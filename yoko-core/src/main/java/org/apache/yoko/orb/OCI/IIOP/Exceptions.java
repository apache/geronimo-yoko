package org.apache.yoko.orb.OCI.IIOP;

import static org.apache.yoko.orb.OB.MinorCodes.*;

import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.TRANSIENT;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import static org.apache.yoko.orb.OB.MinorCodes.MinorSetsockopt;
import static org.apache.yoko.orb.OB.MinorCodes.describeCommFailure;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;

enum Exceptions {;
    static COMM_FAILURE asCommFailure(SocketException e) {return asCommFailure(e, MinorSetsockopt);}
    static COMM_FAILURE asCommFailure(UnknownHostException e) {return asCommFailure(e, MinorGethostbyname);}

    static COMM_FAILURE asCommFailure(IOException e, int minor) {
        String msg = String.format("%s: %s", describeCommFailure(minor), e.getMessage());
        return (COMM_FAILURE) new COMM_FAILURE(msg, minor, COMPLETED_NO).initCause(e);
    }

    static COMM_FAILURE asCommFailure(Exception e, int minor, String message) {
        String msg = String.format("%s: %s: %s", describeCommFailure(minor), message, e.getMessage());
        return (COMM_FAILURE) new COMM_FAILURE(msg, minor, COMPLETED_NO).initCause(e);
    }

    static TRANSIENT asTransient(Exception e, int minor) {
        String msg = String.format("%s: %s", describeTransient(minor), e.getMessage());
        return (TRANSIENT) new TRANSIENT(msg, minor, COMPLETED_NO).initCause(e);
    }
}
