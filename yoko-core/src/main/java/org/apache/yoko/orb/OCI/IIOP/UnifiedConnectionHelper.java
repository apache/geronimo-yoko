package org.apache.yoko.orb.OCI.IIOP;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.CSIIOP.TransportAddress;
import org.omg.IOP.IOR;
import org.omg.IOP.TaggedComponent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Set;

public interface UnifiedConnectionHelper {
    void init(ORB orb, String params);
    Socket createSocket(String host, int port, IOR ior, Policy... policies) throws IOException;
    Socket createSelfConnection(InetAddress address, int port) throws IOException;
    ServerSocket createServerSocket(int port, int backlog, String... params)  throws IOException;
    ServerSocket createServerSocket(int port, int backlog, InetAddress address, String... params) throws IOException;
    default Set<Integer> tags() { return Collections.emptySet(); }
    default TransportAddress[] getEndpoints(TaggedComponent taggedComponent, Policy... policies) { return new TransportAddress[0]; }
    default boolean isExtended() { return false; }
}
