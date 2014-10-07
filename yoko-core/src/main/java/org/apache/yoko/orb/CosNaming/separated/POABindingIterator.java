package org.apache.yoko.orb.CosNaming.separated;

import org.omg.CosNaming.BindingIteratorPOA;
import org.omg.PortableServer.POA;

/**
 * Context implementation version of the BindingIterator
 * object used to return list items.
 */
public class POABindingIterator extends BindingIteratorPOA {
	// the POA used to activate this object (required for destroy();
	private final POA poa;
	private final CoreBindingIterator core;

	/**
	 * Create a new BindingIterator hosted by the given POA and
	 * iterating over the map of items.
	 *
	 * @param poa      The hosting POA.
	 * @param bindings The HashMap of bound objects.
	 */
	public POABindingIterator(POA poa, LocalBindingIterator local) {
		this.poa = poa;
		this.core = local.getCore();
	}

	/**
	 * Return the next object in the iteration sequence.
	 *
	 * @param b      The BindingHolder used to return the next item.  If
	 *               we've reached the end of the sequence, an item
	 *               with an empty name is returned.
	 *
	 * @return true if there is another item, false otherwise.
	 */
	public boolean next_one(org.omg.CosNaming.BindingHolder b) {
		return core.next_one(b);
	}


	/**
	 * Retrieve the next "n" items from the list, returned
	 * as a BindingList.
	 *
	 * @param how_many The count of items to retrieve.
	 * @param bl       A holder for returning an array of Bindings for
	 *                 the returned items.
	 *
	 * @return true if any items were returned, false if there's
	 *         nothing left to return.
	 */
	public boolean next_n(int how_many, org.omg.CosNaming.BindingListHolder bl) {
		return core.next_n(how_many, bl);
	}

	/**
	 * Destroy this BindingIterator instance, which deactivates
	 * it from the hosting POA.
	 */
	public void destroy() {
		try {
			// we need to deactivate this from the POA.
			byte[] objectId = poa.servant_to_id(this);
			if (objectId != null) {
				poa.deactivate_object(objectId);
			}
		} catch (Exception e ) {
		}
	}
}