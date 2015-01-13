package org.apache.yoko.orb.CosNaming.tnaming2;

import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;

public interface RemotableObject {
	Servant getServant(POA poa, boolean readOnly) throws Exception;
}
