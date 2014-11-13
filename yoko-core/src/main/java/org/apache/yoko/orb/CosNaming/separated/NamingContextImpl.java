package org.apache.yoko.orb.CosNaming.separated;

import java.util.HashMap;
import java.util.Objects;

import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.BindingIteratorHelper;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.BindingTypeHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;

public final class NamingContextImpl extends LocalObject implements NamingContext, RemotableObject {
	private static final long serialVersionUID = 1L;

	private static final class Core extends NamingContextBase {
		// the bindings maintained by this context
		private final HashMap<BindingKey, BoundObject> bindings = new HashMap<BindingKey, BoundObject>();
		// the root context object
		private final org.omg.CORBA.Object rootContext;

		/**
		 * Construct a TransientNamingContext subcontext.
		 *
		 * @param orb    The orb this context is associated with.
		 * @param poa    The POA the root context is activated under.
		 * @param root   The root context.
		 *
		 * @exception Exception
		 */
		Core(org.omg.CORBA.Object root) throws Exception {
			rootContext = root;
		}

		// abstract methods part of the interface contract that the implementation is required
		// to supply.

		/**
		 * Create a new context of the same type as the
		 * calling context.
		 *
		 * @return A new NamingContext item.
		 * @exception org.omg.CosNaming.NamingContextPackage.NotFound
		 * @exception SystemException
		 */
		@Override
		public NamingContext new_context() throws SystemException {
			try {
				// create a new context.
				NamingContextImpl newContext = new NamingContextImpl(rootContext);

				return newContext;
			} catch (SystemException e) {
				// just propagate system exceptions
				throw e;
			} catch (Exception e) {
				throw (INTERNAL)(new INTERNAL("Unable to create new naming context").initCause(e));
			}
		}


		/**
		 * Destroy a context.  This method should clean up
		 * any backing resources associated with the context.
		 *
		 * @exception org.omg.CosNaming.NamingContextPackage.NotEmpty
		 */
		@Override
		public synchronized void destroy () throws org.omg.CosNaming.NamingContextPackage.NotEmpty {
			// still holding bound objects?  Not allowed to destroy
			if (!bindings.isEmpty()) {
				throw new NotEmpty();
			}
		}


		/**
		 * Create a list of bound objects an contexts contained
		 * within this context.
		 *
		 * @param how_many The count of elements to return as a BindingList.
		 * @param bl       A holder element for returning the source binding list.
		 * @param bi       A holder for returning a BindingIterator.  Any extra
		 *                 elements not returned in the BindingList are returned
		 *                 in the BindingIterator.
		 *
		 * @exception SystemException
		 */
		@Override
		public synchronized void list(int how_many, org.omg.CosNaming.BindingListHolder bl, org.omg.CosNaming.BindingIteratorHolder bi) throws SystemException {
			BindingIteratorImpl iterator = new BindingIteratorImpl(bindings.values());
			// have the iterator fill in the entries here
			iterator.next_n(how_many, bl);
			bi.value = iterator;
		}

		// lower level functions that are used by the base class


		/**
		 * Resolve an object in this context (single level
		 * resolution).
		 *
		 * @param n      The name of the target object.
		 * @param type   A type holder for returning the bound object type
		 *               information.
		 *
		 * @return The bound object.  Returns null if the object does not
		 *         exist in the context.
		 * @exception SystemException
		 */
		@Override
		protected org.omg.CORBA.Object resolveObject(NameComponent n, BindingTypeHolder type) throws SystemException {
			// special call to resolve the root context.  This is the only one that goes backwards.
			if (n.id.length() == 0 && n.kind.length() == 0) {
				// this is a name context item, so set it properly.
				type.value = BindingType.ncontext;
				return rootContext;
			}

			BindingKey key = new BindingKey(n);
			BoundObject obj = (BoundObject)bindings.get(key);
			// if not in the table, just return null
			if (obj == null) {
				return null;
			}
			// update the type information and return the bound object reference.
			type.value = obj.type;
			return obj.boundObject;
		}


		/**
		 * Bind an object into the current context.  This can
		 * be either an object or a naming context.
		 *
		 * @param n      The single-level name of the target object.
		 * @param obj    The object or context to be bound.
		 * @param type
		 *
		 * @exception SystemException
		 */
		@Override
		protected void bindObject(NameComponent n, org.omg.CORBA.Object obj, BindingTypeHolder type) throws SystemException {
			// fairly simple table put...
			bindings.put(new BindingKey(n), new BoundObject(n, obj, type.value));
		}


		/**
		 * Unbind an object from the current context.
		 *
		 * @param n      The name of the target object (single level).
		 *
		 * @return The object associated with the binding.  Returns null
		 *         if there was no binding currently associated with this
		 *         name.
		 * @exception SystemException
		 */
		@Override
		protected org.omg.CORBA.Object unbindObject(NameComponent n) throws SystemException {
			//remove the object from the hash table, returning the bound object if it exists.
			BindingKey key = new BindingKey(n);
			BoundObject obj = (BoundObject)bindings.remove(key);

			if (obj != null) {
				return obj.boundObject;
			}
			return null;
		}

		/**
		 * Internal class used for HashMap lookup keys.
		 */
		private static final class BindingKey {
			// the name component this is a HashMap key for.
			private final NameComponent name;
			private final int hashval;

			/**
			 * Create a new BindingKey for a NameComponent.
			 *
			 * @param n      The lookup name.
			 */
			public BindingKey(NameComponent n) {
				name = n;
				hashval = Objects.hashCode(name.id) + Objects.hashCode(name.kind);
			}

			/**
			 * Return the hashcode associated with this binding key.  The
			 * hashcode is created using the NameComponent id and
			 * kind fields.
			 *
			 * @return The lookup hashvalue associated with this key.
			 */
			@Override
			public int hashCode() {
				return hashval;
			}

			/**
			 * Compare two BindingKeys for equality (used for HashMap
			 * lookups).
			 *
			 * @param other  The comparison partner.
			 *
			 * @return True if the keys are equivalent, false otherwise.
			 */
			@Override
			public boolean equals(Object other) {
				// if not given or the wrong type, this is false.
				if (!!!(other instanceof BindingKey)) {
					return false;
				}

				BindingKey otherKey = (BindingKey)other;

				return (Objects.equals(name.id, otherKey.name.id) && Objects.equals(name.kind, otherKey.name.kind));
			}
		}

	}

	private static final class POAServant extends NamingContextBase {
		private final NamingContextImpl local;
		private final Core core;
		private final POA poa;
		private final boolean readOnly;
		
		public POAServant(final POA poa, final NamingContextImpl local, final Core core, boolean readOnly) throws Exception {
			this.poa = poa;
			this.local = local;
			this.core = core;
			this.readOnly = readOnly;
			poa.activate_object(this);
		}

		@Override
		public NamingContext new_context() throws SystemException {
	        try {
	    		Servant newContext = 
	    				((NamingContextImpl)core.new_context()).getServant(poa, readOnly);
	            return NamingContextHelper.narrow(newContext._this_object());
	        } catch (SystemException e) {
	            // just propagate system exceptions
	            throw e;
	        } catch (Exception e) {
	            throw (INTERNAL)(new INTERNAL("Unable to create new naming context").initCause(e));
	        }
		}

		@Override
		protected org.omg.CORBA.Object resolveObject(NameComponent n, BindingTypeHolder type)
				throws SystemException {
			try {
				org.omg.CORBA.Object o = core.resolveObject(n, type);
				if (o == local) {
					return _this_object();
				} else if (!!!(o instanceof NamingContextImpl)) {
					return o;
				}
				Servant poaNamingContext = 
						((NamingContextImpl)o).getServant(poa, readOnly);
				return NamingContextHelper.narrow(poaNamingContext._this_object());
			} catch (SystemException e) {
				// just propagate system exceptions
				throw e;
			} catch (Exception e) {
				throw (INTERNAL)(new INTERNAL("Unable to create new naming context").initCause(e));
			}
		}

		@Override
		protected void bindObject(NameComponent n, org.omg.CORBA.Object obj,
				BindingTypeHolder type) throws SystemException, CannotProceed {
			if (readOnly) {
				throw new CannotProceed();
			}
			core.bindObject(n, obj, type);
		}

		@Override
		protected org.omg.CORBA.Object unbindObject(NameComponent n) throws SystemException, CannotProceed {
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
			try {
				Servant iterator = 
					((BindingIteratorImpl)bi.value).getServant(poa, readOnly);
	            bi.value = BindingIteratorHelper.narrow(iterator._this_object());
	        } catch (SystemException e) {
	            // just propagate system exceptions
	            throw e;
	        } catch (Exception e) {
	            throw (INTERNAL)(new INTERNAL("Unable to activate BindingIterator").initCause(e));
	        }
		}
	}
	
	/**
	 * Internal class used to store bound objects in the HashMap.
	 */
	public static final class BoundObject {
		// the name this object is bound under.
		public final NameComponent name;
		// the type of binding (either nobject or ncontext).
		public final BindingType type;
		// the actual bound object.
		public final org.omg.CORBA.Object boundObject;

		/**
		 * Create a new object binding for our HashMap.
		 *
		 * @param name   The bound object's name.
		 * @param boundObject
		 *               The bound object (real object or NamingContext).
		 * @param type   The type information associated with this binding.
		 */
		private BoundObject(NameComponent name, org.omg.CORBA.Object boundObject, BindingType type) {
			this.name = name;
			this.boundObject = boundObject;
			this.type = type;
		}
	}
	
	private final Core core;
	
	public NamingContextImpl() throws Exception {
		core = new Core(this);
	}
	
	public NamingContextImpl(org.omg.CORBA.Object rootContext) throws Exception {
		core = new Core(rootContext);
	}

	@Override
	public void bind(NameComponent[] n, org.omg.CORBA.Object obj) throws NotFound,
			CannotProceed, InvalidName, AlreadyBound {
		core.bind(n, obj);
	}

	@Override
	public void bind_context(NameComponent[] n, NamingContext nc)
			throws NotFound, CannotProceed, InvalidName, AlreadyBound {
		core.bind_context(n, nc);
	}

	@Override
	public void rebind(NameComponent[] n, org.omg.CORBA.Object obj) throws NotFound,
			CannotProceed, InvalidName {
		core.rebind(n, obj);
	}

	@Override
	public void rebind_context(NameComponent[] n, NamingContext nc)
			throws NotFound, CannotProceed, InvalidName {
		core.rebind_context(n, nc);
	}

	@Override
	public org.omg.CORBA.Object resolve(NameComponent[] n) throws NotFound, CannotProceed,
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

	@Override
	public Servant getServant(POA poa, boolean readOnly) throws Exception {
		return new POAServant(poa, this, core, readOnly);
	}
}
