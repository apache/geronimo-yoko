package org.apache.yoko.orb.CosNaming.tnaming2;

import org.apache.yoko.orb.spi.naming.RemoteAccess;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;

public interface RemotableObject {
    Servant getServant(POA poa, RemoteAccess remoteAccess) throws Exception;
}
