package org.apache.yoko.orb.CosNaming.separated;

import java.util.concurrent.CountDownLatch;

import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.BindingIteratorHelper;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingIteratorPOA;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.BindingTypeHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExtPOA;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.PortableServer.POA;

public class POANamingContext extends NamingContextBase {
	private final LocalNamingContext local;
	private final CoreNamingContext core;
	private final POA poa;
	private final boolean readOnly;
	private volatile CountDownLatch latch;
	private final Object thisObject;

	public POANamingContext(final POA poa, final LocalNamingContext local) throws Exception {
		this(poa, local, false);
	}
	
	public POANamingContext(final POA poa, final LocalNamingContext local, boolean readOnly) throws Exception {
		this.poa = poa;
		this.local = local;
		this.core = local.getCore();
		this.readOnly = readOnly;
		latch = new CountDownLatch(1);
		try {
			byte[] objectId  = poa.activate_object(this);
			thisObject = NamingContextHelper.narrow(poa.id_to_reference(objectId));
		} finally {
			latch.countDown();
			latch = null;
		}
		
	}

	@Override
	public NamingContext new_context() throws SystemException {
        try {
    		NamingContextExtPOA newContext = 
    				new POANamingContext(poa, (LocalNamingContext)core.new_context(), readOnly);
            byte[] objectId = poa.activate_object(newContext);
            org.omg.CORBA.Object obj = poa.id_to_reference(objectId);
            return NamingContextHelper.narrow(obj);
        } catch (SystemException e) {
            // just propagate system exceptions
            throw e;
        } catch (Exception e) {
            throw (INTERNAL)(new INTERNAL("Unable to create new naming context").initCause(e));
        }
	}

	@Override
	protected Object resolveObject(NameComponent n, BindingTypeHolder type)
			throws SystemException {
		try {
			Object o = core.resolveObject(n, type);
			if (o == local) {
				final CountDownLatch latch = this.latch;
				if (latch != null) latch.await();
				return thisObject;
			} else if (!!!(o instanceof LocalNamingContext)) {
				return o;
			}
			NamingContextExtPOA poaNamingContext = 
					new POANamingContext(poa, (LocalNamingContext)o, readOnly);
			byte[] objectId = poa.activate_object(poaNamingContext);
			org.omg.CORBA.Object obj = poa.id_to_reference(objectId);
			return NamingContextHelper.narrow(obj);
		} catch (SystemException e) {
			// just propagate system exceptions
			throw e;
		} catch (Exception e) {
			throw (INTERNAL)(new INTERNAL("Unable to create new naming context").initCause(e));
		}
	}

	@Override
	protected void bindObject(NameComponent n, Object obj,
			BindingTypeHolder type) throws SystemException, CannotProceed {
		if (readOnly) {
			throw new CannotProceed();
		}
		core.bindObject(n, obj, type);
	}

	@Override
	protected Object unbindObject(NameComponent n) throws SystemException, CannotProceed {
		if (readOnly) {
			throw new CannotProceed();
		}
		return core.unbindObject(n);
	}

	@Override
	public void destroy() throws NotEmpty {
        try {
            // we need to deactivate this from the POA.
            byte[] objectId = poa.servant_to_id(this);
            if (objectId != null) {
                poa.deactivate_object(objectId);
            }
        } catch (Exception e ) {
        }
	}

	@Override
	public void list(int how_many, BindingListHolder bl,
			BindingIteratorHolder bi) throws SystemException {
		core.list(how_many, bl, bi);
		BindingIteratorPOA iterator = 
				new POABindingIterator(poa, (LocalBindingIterator)bi.value);
        try {
            byte[] objectId = poa.activate_object(iterator);
            org.omg.CORBA.Object obj = poa.id_to_reference(objectId);

            bi.value = BindingIteratorHelper.narrow(obj);
        } catch (SystemException e) {
            // just propagate system exceptions
            throw e;
        } catch (Exception e) {
            throw (INTERNAL)(new INTERNAL("Unable to activate BindingIterator").initCause(e));
        }
	}
}
