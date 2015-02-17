package org.apache.yoko.orb.spi.naming;

import org.omg.CORBA.Object;

public interface Resolvable extends Object {

	org.omg.CORBA.Object resolve(); // This is the factory method
}
