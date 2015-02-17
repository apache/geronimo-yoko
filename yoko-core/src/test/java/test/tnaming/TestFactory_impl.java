package test.tnaming;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.yoko.orb.spi.naming.Resolvable;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.PortableServer.POA;

public class TestFactory_impl extends LocalObject implements Resolvable{

	POA _poa;
	ORB _orb;
	String _baseName;
	static final AtomicInteger _count = new AtomicInteger (0); 
	
	public TestFactory_impl (POA poa, ORB orb, String baseName) { 
		_poa = poa;
		_orb = orb;
	}
	
	@Override
	public Object resolve() {
		String thisOnesName = "_baseName" + _count.incrementAndGet();
		return new Test_impl(_poa, thisOnesName)._this_object(_orb);
	}
	
}
