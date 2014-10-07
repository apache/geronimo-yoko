/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


/**
 * @version $Rev: 497539 $ $Date: 2007-01-18 11:16:12 -0800 (Thu, 18 Jan 2007) $
 */

package org.apache.yoko.orb.CosNaming.separated;

import java.util.HashMap;

import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.BindingTypeHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;

public class CoreNamingContext extends NamingContextBase {
	// the bindings maintained by this context
	protected final HashMap<BindingKey, BoundObject> bindings = new HashMap<BindingKey, BoundObject>();
	// the root context object
	protected final org.omg.CORBA.Object rootContext;

	/**
	 * Construct a TransientNamingContext subcontext.
	 *
	 * @param orb    The orb this context is associated with.
	 * @param poa    The POA the root context is activated under.
	 * @param root   The root context.
	 *
	 * @exception Exception
	 */
	public CoreNamingContext(org.omg.CORBA.Object root) throws Exception {
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
	public NamingContext new_context() throws SystemException {
		try {
			// create a new context.
			LocalNamingContext newContext = new LocalNamingContext(rootContext);

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
	public synchronized void list(int how_many, org.omg.CosNaming.BindingListHolder bl, org.omg.CosNaming.BindingIteratorHolder bi) throws SystemException {
		LocalBindingIterator iterator = new LocalBindingIterator(bindings.values());
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
	 * Retrieve the rootContext for this NamingContext.
	 *
	 * @return The rootContext CORBA object associated with this context.
	 */
	public org.omg.CORBA.Object getRootContext() {
		return rootContext;
	}

	/**
	 * Internal class used for HashMap lookup keys.
	 */
	class BindingKey {
		// the name component this is a HashMap key for.
		public NameComponent name;
		private int hashval = 0;

		/**
		 * Create a new BindingKey for a NameComponent.
		 *
		 * @param n      The lookup name.
		 */
		public BindingKey(NameComponent n) {
			name = n;
			// create a hash value used for lookups
			if (name.id != null) {
				hashval += name.id.hashCode();
			}
			if (name.kind != null) {
				hashval += name.kind.hashCode();
			}
		}

		/**
		 * Return the hashcode associated with this binding key.  The
		 * hashcode is created using the NameComponent id and
		 * kind fields.
		 *
		 * @return The lookup hashvalue associated with this key.
		 */
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
		public boolean equals(Object other) {
			// if not given or the wrong type, this is false.
			if (other == null || !(other instanceof BindingKey)) {
				return false;
			}

			BindingKey otherKey = (BindingKey)other;

			// verify first on the id name.
			if (name.id != null) {
				if (otherKey.name.id == null) {
					return false;
				}
				if (!name.id.equals(otherKey.name.id)) {
					return false;
				}
			}
			else {
				if (otherKey.name.id != null) {
					return false;
				}
			}
			// this is a match so far...now compare the kinds
			if (name.kind != null) {
				if (otherKey.name.kind == null) {
					return false;
				}
				if (!name.kind.equals(otherKey.name.kind)) {
					return false;
				}
			}
			else {
				if (otherKey.name.kind != null) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Internal class used to store bound objects in the HashMap.
	 */
	public class BoundObject {
		// the name this object is bound under.
		public NameComponent name;
		// the type of binding (either nobject or ncontext).
		public BindingType type;
		// the actual bound object.
		public org.omg.CORBA.Object boundObject;

		/**
		 * Create a new object binding for our HashMap.
		 *
		 * @param name   The bound object's name.
		 * @param boundObject
		 *               The bound object (real object or NamingContext).
		 * @param type   The type information associated with this binding.
		 */
		public BoundObject(NameComponent name, org.omg.CORBA.Object boundObject, BindingType type) {
			this.name = name;
			this.boundObject = boundObject;
			this.type = type;
		}
	}
}

