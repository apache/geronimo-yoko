package org.apache.yoko.orb.CosNaming.separated;

import java.util.Collection;

import org.apache.yoko.orb.CosNaming.separated.CoreNamingContext.BoundObject;
import org.omg.CORBA.LocalObject;
import org.omg.CosNaming.BindingHolder;
import org.omg.CosNaming.BindingIterator;
import org.omg.CosNaming.BindingListHolder;

public final class LocalBindingIterator extends LocalObject implements
		BindingIterator {
	private static final long serialVersionUID = 1L;

	private final CoreBindingIterator core;
	
	public LocalBindingIterator(final Collection<BoundObject> boundObjects) {
		assert boundObjects != null;
		core = new CoreBindingIterator(boundObjects);
	}
	
	public final CoreBindingIterator getCore() {
		return core;
	}

	@Override
	public boolean next_one(BindingHolder b) {
		assert b != null;
		return core.next_one(b);
	}

	@Override
	public boolean next_n(int how_many, BindingListHolder bl) {
		assert bl != null;
		return core.next_n(how_many, bl);
	}

	@Override
	public void destroy() {
		core.destroy();
	}

}
