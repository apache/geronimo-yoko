package org.apache.yoko.orb.CosNaming.separated;

import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;

public interface RemotableObject {
	Servant getServant(POA poa, boolean readOnly) throws Exception;
}
