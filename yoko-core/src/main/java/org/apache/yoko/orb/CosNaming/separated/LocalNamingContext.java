package org.apache.yoko.orb.CosNaming.separated;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Object;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.NamingContextPackage.NotFound;

public final class LocalNamingContext extends LocalObject implements NamingContext {
	private static final long serialVersionUID = 1L;

	private final CoreNamingContext core;
	
	public LocalNamingContext() throws Exception {
		core = new CoreNamingContext(this);
	}
	
	public LocalNamingContext(org.omg.CORBA.Object rootContext) throws Exception {
		core = new CoreNamingContext(rootContext);
	}
	
	public CoreNamingContext getCore() {
		return core;
	}

	@Override
	public void bind(NameComponent[] n, Object obj) throws NotFound,
			CannotProceed, InvalidName, AlreadyBound {
		core.bind(n, obj);
	}

	@Override
	public void bind_context(NameComponent[] n, NamingContext nc)
			throws NotFound, CannotProceed, InvalidName, AlreadyBound {
		core.bind_context(n, nc);
	}

	@Override
	public void rebind(NameComponent[] n, Object obj) throws NotFound,
			CannotProceed, InvalidName {
		core.rebind(n, obj);
	}

	@Override
	public void rebind_context(NameComponent[] n, NamingContext nc)
			throws NotFound, CannotProceed, InvalidName {
		core.rebind_context(n, nc);
	}

	@Override
	public Object resolve(NameComponent[] n) throws NotFound, CannotProceed,
			InvalidName {
		return core.resolve(n);
	}

	@Override
	public void unbind(NameComponent[] n) throws NotFound, CannotProceed,
			InvalidName {
		core.unbind(n);
	}

	@Override
	public void list(int how_many, BindingListHolder bl,
			BindingIteratorHolder bi) {
		core.list(how_many, bl, bi);
	}

	@Override
	public NamingContext new_context() {
		return core.new_context();
	}

	@Override
	public NamingContext bind_new_context(NameComponent[] n) throws NotFound,
			AlreadyBound, CannotProceed, InvalidName {
		return core.bind_new_context(n);
	}

	@Override
	public void destroy() throws NotEmpty {
		core.destroy();
	}

}
