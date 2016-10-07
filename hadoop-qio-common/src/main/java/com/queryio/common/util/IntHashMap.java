package com.queryio.common.util;

import java.io.IOException;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 
 * @author Exceed Consultancy Services
 */
public class IntHashMap implements Cloneable, java.io.Serializable
{
	/**
	 * The hash tableIterator data.
	 */
	transient Entry table[];

	/**
	 * The total number of mappings in the hash tableIterator.
	 */
	transient int count;

	/**
	 * The tableIterator is rehashed when its size exceeds this threshold. (The
	 * value of this field is (int)(capacity * loadFactor).)
	 * 
	 * @serial
	 */
	private int threshold;

	/**
	 * The load factor for the hashtable.
	 * 
	 * @serial
	 */
	private final float loadFactor;

	/**
	 * The number of times this IntHashMap has been structurally modified
	 * Structural modifications are those that change the number of mappings in
	 * the IntHashMap or otherwise modify its internal structure (e.g., rehash).
	 * This field is used to make iterators on Collection-views of the
	 * IntHashMap fail-fast. (See ConcurrentModificationException).
	 */
	transient int modCount = 0;

	/**
	 * Constructs a new, empty map with the specified initial capacity and the
	 * specified load factor.
	 * 
	 * @param initialCapacity
	 *            the initial capacity of the IntHashMap.
	 * @param loadFactor
	 *            the load factor of the IntHashMap
	 * @throws IllegalArgumentException
	 *             if the initial capacity is less than zero, or if the load
	 *             factor is nonpositive.
	 */
	public IntHashMap(int initialCapacity, final float load)
	{
		if (initialCapacity < 0)
		{
			throw new IllegalArgumentException("Illegal Initial Capacity: " + initialCapacity); //$NON-NLS-1$
		}

		if ((load <= 0) || Float.isNaN(load))
		{
			throw new IllegalArgumentException("Illegal Load factor: " + load); //$NON-NLS-1$
		}

		if (initialCapacity == 0)
		{
			initialCapacity = 1;
		}

		this.loadFactor = load;
		this.table = new Entry[initialCapacity];
		this.threshold = (int) (initialCapacity * load);
	}

	/**
	 * Constructs a new, empty map with the specified initial capacity and
	 * default load factor, which is <tt>0.75</tt>.
	 * 
	 * @param initialCapacity
	 *            the initial capacity of the IntHashMap.
	 * @throws IllegalArgumentException
	 *             if the initial capacity is less than zero.
	 */
	public IntHashMap(final int initialCapacity)
	{
		this(initialCapacity, 0.75f);
	}

	/**
	 * Constructs a new, empty map with a default capacity and load factor,
	 * which is <tt>0.75</tt>.
	 */
	public IntHashMap()
	{
		this(11, 0.75f);
	}

	/**
	 * Returns the number of key-value mappings in this map.
	 * 
	 * @return the number of key-value mappings in this map.
	 */
	public final int size()
	{
		return this.count;
	}

	/**
	 * Returns <tt>true</tt> if this map contains no key-value mappings.
	 * 
	 * @return <tt>true</tt> if this map contains no key-value mappings.
	 */
	public boolean isEmpty()
	{
		return this.count == 0;
	}

	/**
	 * Returns an int array of all the keys being used for this hashmap
	 * 
	 * @return an int array of all the keys being used for this hashmap
	 */
	public int[] keys()
	{
		final int[] intArray = new int[this.count];

		final Entry tab[] = this.table;

		int ctr = 0;
		for (int i = tab.length; i-- > 0;)
		{
			for (Entry e = tab[i]; e != null; e = e.next)
			{
				intArray[ctr++] = e.key;
			}
		}

		return intArray;
	}

	/**
	 * Returns <tt>true</tt> if this map maps one or more keys to the
	 * specified value.
	 * 
	 * @param value
	 *            value whose presence in this map is to be tested.
	 * @return <tt>true</tt> if this map maps one or more keys to the
	 *         specified value.
	 */
	public boolean containsValue(final Object value)
	{
		final Entry tab[] = this.table;

		if (value == null)
		{
			for (int i = tab.length; i-- > 0;)
			{
				for (Entry e = tab[i]; e != null; e = e.next)
				{
					if (e.value == null)
					{
						return true;
					}
				}
			}
		}
		else
		{
			for (int i = tab.length; i-- > 0;)
			{
				for (Entry e = tab[i]; e != null; e = e.next)
				{
					if (value.equals(e.value))
					{
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Returns <tt>true</tt> if this map contains a mapping for the specified
	 * key.
	 * 
	 * @return <tt>true</tt> if this map contains a mapping for the specified
	 *         key.
	 * @param key
	 *            key whose presence in this Map is to be tested.
	 */
	public boolean containsKey(final int key)
	{
		final Entry tab[] = this.table;

		final int hash = key;
		final int index = (hash & 0x7FFFFFFF) % tab.length;

		for (Entry e = tab[index]; e != null; e = e.next)
		{
			if ((e.hash == hash) && (key == e.key))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the value to which this map maps the specified key. Returns
	 * <tt>null</tt> if the map contains no mapping for this key. A return
	 * value of <tt>null</tt> does not <i>necessarily</i> indicate that the
	 * map contains no mapping for the key; it's also possible that the map
	 * explicitly maps the key to <tt>null</tt>. The <tt>containsKey</tt>
	 * operation may be used to distinguish these two cases.
	 * 
	 * @return the value to which this map maps the specified key.
	 * @param key
	 *            key whose associated value is to be returned.
	 */
	public Object get(final int key)
	{
		final Entry tab[] = this.table;

		final int hash = key;
		final int index = (hash & 0x7FFFFFFF) % tab.length;
		for (Entry e = tab[index]; e != null; e = e.next)
		{
			if ((e.hash == hash) && (key == e.key))
			{
				return e.value;
			}
		}
		return null;
	}

	/**
	 * Rehashes the contents of this map into a new <tt>IntHashMap</tt>
	 * instance with a larger capacity. This method is called automatically when
	 * the number of keys in this map exceeds its capacity and load factor.
	 */
	private void rehash()
	{
		final int oldCapacity = this.table.length;
		final Entry oldMap[] = this.table;

		final int newCapacity = oldCapacity * 2 + 1;
		final Entry newMap[] = new Entry[newCapacity];

		this.modCount++;
		this.threshold = (int) (newCapacity * this.loadFactor);
		this.table = newMap;

		for (int i = oldCapacity; i-- > 0;)
		{
			for (Entry old = oldMap[i]; old != null;)
			{
				final Entry e = old;
				old = old.next;

				final int index = (e.hash & 0x7FFFFFFF) % newCapacity;
				e.next = newMap[index];
				newMap[index] = e;
			}
		}
	}

	/**
	 * Associates the specified value with the specified key in this map. If the
	 * map previously contained a mapping for this key, the old value is
	 * replaced.
	 * 
	 * @param key
	 *            key with which the specified value is to be associated.
	 * @param value
	 *            value to be associated with the specified key.
	 * @return previous value associated with specified key, or <tt>null</tt>
	 *         if there was no mapping for key. A <tt>null</tt> return can
	 *         also indicate that the IntHashMap previously associated
	 *         <tt>null</tt> with the specified key.
	 */
	public Object put(final int key, final Object value)
	{
		// Makes sure the key is not already in the IntHashMap.
		Entry tab[] = this.table;
		int hash = 0;
		int index = 0;

		hash = key;
		index = (hash & 0x7FFFFFFF) % tab.length;
		for (Entry e = tab[index]; e != null; e = e.next)
		{
			if ((e.hash == hash) && (key == e.key))
			{
				final Object old = e.value;
				e.value = value;
				return old;
			}
		}

		this.modCount++;
		if (this.count >= this.threshold)
		{
			// Rehash the tableIterator if the threshold is exceeded
			this.rehash();

			tab = this.table;
			index = (hash & 0x7FFFFFFF) % tab.length;
		}

		// Creates the new entry.
		final Entry e = new Entry(hash, key, value, tab[index]);
		tab[index] = e;
		this.count++;

		return null;
	}

	/**
	 * Removes the mapping for this key from this map if present.
	 * 
	 * @param key
	 *            key whose mapping is to be removed from the map.
	 * @return previous value associated with specified key, or <tt>null</tt>
	 *         if there was no mapping for key. A <tt>null</tt> return can
	 *         also indicate that the map previously associated <tt>null</tt>
	 *         with the specified key.
	 */
	public Object remove(final int key)
	{
		final Entry tab[] = this.table;

		final int hash = key;
		final int index = (hash & 0x7FFFFFFF) % tab.length;

		for (Entry e = tab[index], prev = null; e != null; prev = e, e = e.next)
		{
			if ((e.hash == hash) && (key == e.key))
			{
				this.modCount++;
				if (prev != null)
				{
					prev.next = e.next;
				}
				else
				{
					tab[index] = e.next;
				}

				this.count--;
				final Object oldValue = e.value;
				e.value = null;
				return oldValue;
			}
		}
		return null;
	}

	/**
	 * Removes all mappings from this map.
	 */
	public void clear()
	{
		final Entry tab[] = this.table;
		this.modCount++;
		for (int index = tab.length; --index >= 0;)
		{
			tab[index] = null;
		}
		this.count = 0;
	}

	/**
	 * Returns a shallow copy of this <tt>IntHashMap</tt> instance: the keys
	 * and values themselves are not cloned.
	 * 
	 * @return a shallow copy of this map.
	 */
	public Object clone()
	{
		try
		{
			final IntHashMap t = (IntHashMap) super.clone();
			t.table = new Entry[this.table.length];
			for (int i = this.table.length; i-- > 0;)
			{
				t.table[i] = (this.table[i] != null) ? (Entry) this.table[i].clone() : null;
			}
			/*
			 * t.keySet = null; t.entrySet = null;
			 */
			t.values = null;
			t.modCount = 0;
			return t;
		}
		catch (final CloneNotSupportedException e)
		{
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	// Views

	/*
	 * private transient Set keySet = null; private transient Set entrySet =
	 * null;
	 */
	private transient Collection values = null;

	/**
	 * Returns a set view of the keys contained in this map. The set is backed
	 * by the map, so changes to the map are reflected in the set, and
	 * vice-versa. The set supports element removal, which removes the
	 * corresponding mapping from this map, via the <tt>Iterator.remove</tt>,
	 * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt>, and
	 * <tt>clear</tt> operations. It does not support the <tt>add</tt> or
	 * <tt>addAll</tt> operations.
	 * 
	 * @return a set view of the keys contained in this map.
	 */
	/*
	 * public Set keySet() { if (keySet == null) { keySet = new AbstractSet() {
	 * public Iterator iterator() { return getHashIterator(KEYS); } public int
	 * size() { return count; } public boolean contains(Object o) { return
	 * containsKey(o); } public boolean remove(Object o) { int oldSize = count;
	 * IntHashMap.this.remove(o); return count != oldSize; } public void clear() {
	 * IntHashMap.this.clear(); } }; } return keySet; }
	 */

	/**
	 * Returns a collection view of the values contained in this map. The
	 * collection is backed by the map, so changes to the map are reflected in
	 * the collection, and vice-versa. The collection supports element removal,
	 * which removes the corresponding mapping from this map, via the
	 * <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
	 * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
	 * operations. It does not support the <tt>add</tt> or <tt>addAll</tt>
	 * operations.
	 * 
	 * @return a collection view of the values contained in this map.
	 */
	public Collection values()
	{
		if (this.values == null)
		{
			this.values = new AbstractCollection()
			{
				/**
				 * Method iterator
				 * 
				 * @return Iterator
				 */
				public Iterator iterator()
				{
					return IntHashMap.this.getHashIterator(VALUES);
				}

				/**
				 * Method size
				 * 
				 * @return int
				 */
				public final int size()
				{
					return IntHashMap.this.count;
				}

				/**
				 * Method contains
				 * 
				 * @param o
				 *            Object
				 * @return boolean
				 */
				public boolean contains(final Object o)
				{
					return IntHashMap.this.containsValue(o);
				}

				/**
				 * Method clear
				 */
				public void clear()
				{
					IntHashMap.this.clear();
				}
			};
		}
		return this.values;
	}

	/**
	 * Returns a collection view of the mappings contained in this map. Each
	 * element in the returned collection is a <tt>Map.Entry</tt>. The
	 * collection is backed by the map, so changes to the map are reflected in
	 * the collection, and vice-versa. The collection supports element removal,
	 * which removes the corresponding mapping from the map, via the
	 * <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
	 * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
	 * operations. It does not support the <tt>add</tt> or <tt>addAll</tt>
	 * operations.
	 * 
	 * @return a collection view of the mappings contained in this map. /*public
	 *         Set entrySet() { if (entrySet==null) { entrySet = new
	 *         AbstractSet() { public Iterator iterator() { return
	 *         getHashIterator(ENTRIES); }
	 * 
	 * public boolean contains(Object o) { if (!(o instanceof Map.Entry)) {
	 * return false; } Map.Entry entry = (Map.Entry)o; Object key =
	 * entry.getKey(); Entry tab[] = tableIterator; int hash = (key==null ? 0 :
	 * key.hashCode()); int index = (hash & 0x7FFFFFFF) % tab.length;
	 * 
	 * for (Entry e = tab[index]; e != null; e = e.next) { if (e.hash==hash &&
	 * e.equals(entry)) { return true; } } return false; }
	 * 
	 * public boolean remove(Object o) { if (!(o instanceof Map.Entry)) { return
	 * false; } Map.Entry entry = (Map.Entry)o; Object key = entry.getKey();
	 * Entry tab[] = tableIterator; int hash = (key==null ? 0 : key.hashCode());
	 * int index = (hash & 0x7FFFFFFF) % tab.length;
	 * 
	 * for (Entry e = tab[index], prev = null; e != null; prev = e, e = e.next) {
	 * if (e.hash==hash && e.equals(entry)) { modCount++; if (prev != null) {
	 * prev.next = e.next; } else { tab[index] = e.next; }
	 * 
	 * count--; e.value = null; return true; } } return false; }
	 * 
	 * public int size() { return count; }
	 * 
	 * public void clear() { IntHashMap.this.clear(); } }; }
	 * 
	 * return entrySet; }
	 */

	Iterator getHashIterator(final int type)
	{
		if (this.count == 0)
		{
			return emptyHashIterator;
		}
		return new HashIterator(type);
	}

	/**
	 * IntHashMap collision list entry.
	 * 
	 * @author Exceed Consultancy Services
	 */
	private static class Entry /* implements Map.Entry */
	{
		int hash;
		int key;
		Object value;
		Entry next;

		/**
		 * Method Entry.
		 * 
		 * @param hash
		 * @param key
		 * @param value
		 * @param next
		 */
		Entry(final int h, final int k, final Object v, final Entry n)
		{
			this.hash = h;
			this.key = k;
			this.value = v;
			this.next = n;
		}

		/**
		 * @see java.lang.Object#clone()
		 */
		protected Object clone()
		{
			return new Entry(this.hash, this.key, this.value, (this.next == null ? null : (Entry) this.next.clone()));
		}

		// Map.Entry Ops
		/**
		 * Method getKey.
		 * 
		 * @return int
		 */
		public final int getKey()
		{
			return this.key;
		}

		/**
		 * Method getValue.
		 * 
		 * @return Object
		 */
		public final Object getValue()
		{
			return this.value;
		}

		/**
		 * Method setValue.
		 * 
		 * @param value
		 * @return Object
		 */
//		public Object setValue(final Object val)
//		{
//			final Object oldValue = this.value;
//			this.value = val;
//			return oldValue;
//		}

		/**
		 * @see java.lang.Object#equals(Object)
		 */
		public boolean equals(final Object o)
		{
			if (!(o instanceof Entry))
			{
				return false;
			}
			final Entry e = (Entry) o;

			return ((this.key == e.getKey()) && (this.value == null ? e.getValue() == null : this.value.equals(e
					.getValue())));
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode()
		{
			return this.hash ^ (this.value == null ? 0 : this.value.hashCode());
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{
			return this.key + "=" + this.value; //$NON-NLS-1$
		}
	}

	// Types of Iterators
	private static final int KEYS = 0;
	private static final int VALUES = 1;
	// private static final int ENTRIES = 2;

	private static EmptyHashIterator emptyHashIterator = new EmptyHashIterator();

	/**
	 * 
	 * @author Exceed Consultancy Services
	 */
	private static class EmptyHashIterator implements Iterator
	{
		/**
		 * @see java.lang.Object#Object()
		 */
		EmptyHashIterator()
		{
			// default constructor
		}

		/**
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			return false;
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		public Object next()
		{
			throw new NoSuchElementException();
		}

		/**
		 * @see java.util.Iterator#remove()
		 */
		public void remove()
		{
			throw new IllegalStateException();
		}
	}

	/**
	 * 
	 * @author Exceed Consultancy Services
	 */
	private class HashIterator implements Iterator
	{
		private final Entry[] tableIterator = IntHashMap.this.table;
		private int index = this.tableIterator.length;
		private Entry entry = null;
		private Entry lastReturned = null;
		private final int type;

		/**
		 * The modCount value that the iterator believes that the backing List
		 * should have. If this expectation is violated, the iterator has
		 * detected concurrent modification.
		 */
		private int expectedModCount = IntHashMap.this.modCount;

		/**
		 * Method HashIterator.
		 * 
		 * @param type
		 */
		HashIterator(final int typ)
		{
			this.type = typ;
		}

		/**
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			Entry e = this.entry;
			int i = this.index;
			final Entry t[] = this.tableIterator;
			/* Use locals for faster loop iteration */
			while ((e == null) && (i > 0))
			{
				e = t[--i];
			}
			this.entry = e;
			this.index = i;
			return e != null;
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		public Object next()
		{
			if (IntHashMap.this.modCount != this.expectedModCount)
			{
				throw new ConcurrentModificationException();
			}

			Entry et = this.entry;
			int i = this.index;
			final Entry t[] = this.tableIterator;

			/* Use locals for faster loop iteration */
			while ((et == null) && (i > 0))
			{
				et = t[--i];
			}

			this.entry = et;
			this.index = i;
			if (et != null)
			{
				final Entry e = this.lastReturned = this.entry;
				this.entry = e.next;
				return this.type == KEYS ? /* e.key */
				null : (this.type == VALUES ? e.value : e);
			}
			throw new NoSuchElementException();
		}

		/**
		 * @see java.util.Iterator#remove()
		 */
		public void remove()
		{
			if (this.lastReturned == null)
			{
				throw new IllegalStateException();
			}
			if (IntHashMap.this.modCount != this.expectedModCount)
			{
				throw new ConcurrentModificationException();
			}

			final Entry[] tab = IntHashMap.this.table;
			final int ind = (this.lastReturned.hash & 0x7FFFFFFF) % tab.length;

			for (Entry e = tab[ind], prev = null; e != null; prev = e, e = e.next)
			{
				if (e == this.lastReturned)
				{
					IntHashMap.this.modCount++;
					this.expectedModCount++;
					if (prev == null)
					{
						tab[ind] = e.next;
					}
					else
					{
						prev.next = e.next;
					}
					IntHashMap.this.count--;
					this.lastReturned = null;
					return;
				}
			}
			throw new ConcurrentModificationException();
		}
	}

	/**
	 * Save the state of the <tt>IntHashMap</tt> instance to a stream (i.e.,
	 * serialize it).
	 * 
	 * @serialData The <i>capacity</i> of the IntHashMap (the length of the
	 *             bucket array) is emitted (int), followed by the <i>size</i>
	 *             of the IntHashMap (the number of key-value mappings),
	 *             followed by the key (Object) and value (Object) for each
	 *             key-value mapping represented by the IntHashMap The key-value
	 *             mappings are emitted in no particular order.
	 */
	private void writeObject(final java.io.ObjectOutputStream s) throws IOException
	{
		// Write out the threshold, loadfactor, and any hidden stuff
		s.defaultWriteObject();

		// Write out number of buckets
		s.writeInt(this.table.length);

		// Write out size (number of Mappings)
		s.writeInt(this.count);

		// Write out keys and values (alternating)
		for (int index = this.table.length - 1; index >= 0; index--)
		{
			Entry entry = this.table[index];

			while (entry != null)
			{
				s.writeInt(entry.key);
				s.writeObject(entry.value);
				entry = entry.next;
			}
		}
	}

	private static final long serialVersionUID = 362498820763181265L;

	/**
	 * Reconstitute the <tt>IntHashMap</tt> instance from a stream (i.e.,
	 * deserialize it).
	 */
	private void readObject(final java.io.ObjectInputStream s) throws IOException, ClassNotFoundException
	{
		// Read in the threshold, loadfactor, and any hidden stuff
		s.defaultReadObject();

		// Read in number of buckets and allocate the bucket array;
		final int numBuckets = s.readInt();
		this.table = new Entry[numBuckets];

		// Read in size (number of Mappings)
		final int size = s.readInt();

		// Read the keys and values, and put the mappings in the IntHashMap
		for (int i = 0; i < size; i++)
		{
			final int key = s.readInt();
			final Object value = s.readObject();
			this.put(key, value);
		}
	}

	/**
	 * Method capacity.
	 * 
	 * @return int
	 */
	final int capacity()
	{
		return this.table.length;
	}

	/**
	 * Method loadFactor.
	 * 
	 * @return float
	 */
	final float loadFactor()
	{
		return this.loadFactor;
	}
}
