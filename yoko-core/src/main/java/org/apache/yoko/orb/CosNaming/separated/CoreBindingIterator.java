package org.apache.yoko.orb.CosNaming.separated;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.yoko.orb.CosNaming.separated.CoreNamingContext.BoundObject;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingHolder;
import org.omg.CosNaming.BindingIteratorPOA;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.NameComponent;

/**
 * Context implementation version of the BindingIterator
 * object used to return list items.
 */
public class CoreBindingIterator extends BindingIteratorPOA {
	// the iterator use to access the bindings
	private final Iterator<BoundObject> iterator;

	private static final NameComponent[] ZERO_NC_ARRAY = new NameComponent[0];
	/**
	 * Create a new BindingIterator to iterate over the given boundObjects.
	 *
	 * @param boundObjects The bound objects over which to iterate.
	 */
	public CoreBindingIterator(Collection<BoundObject> boundObjects) {
		this.iterator = (new ArrayList<BoundObject>(boundObjects)).iterator();
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
		if (iterator.hasNext()) {
			// return this as a Binding value.
			BoundObject obj = iterator.next();
			b.value = new Binding(new NameComponent[] { obj.name }, obj.type);
			return true;
		}
		else {
			// return an empty element
			b.value = new Binding(ZERO_NC_ARRAY, BindingType.nobject);
			return false;
		}
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
		List<Binding> accum = new ArrayList<Binding>();
		BindingHolder holder = new BindingHolder();
		int i = 0;
		// Keep iterating as long as there are entries
		while (i < how_many && next_one(holder)) {
			accum.add(holder.value);
			i++;
		}

		// convert to an array and return whether we found anything.
		bl.value = accum.toArray(new Binding[accum.size()]);
		return accum.isEmpty();
	}

	/**
	 * Destroy this BindingIterator instance
	 */
	public void destroy() {
	}
}