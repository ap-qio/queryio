/*
 * @(#) IntHashMapExt.java     1.0     02/01/2003 (DD/MM/YYYY)
 *
 * Copyright (C) 2002 Exceed Consultancy Services. All rights reserved.
 *
 * This software is proprietary information of Exceed Consultancy Services and
 * constitutes valuable trade secrets of Exceed Consultancy Services. You shall
 * not disclose this information and shall use it only in accordance with the
 * terms of License.
 *
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL 
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.common.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class IntHashMapExt extends HashMap {
	/**
	 * Determines if a de-serialized file is compatible with this class.
	 * 
	 * Maintainers must change this value if and only if the new version of this
	 * class is not compatible with old versions. See Sun docs for <a
	 * href=http://java.sun.com/products/jdk/1.1/docs/guide
	 * /serialization/spec/version.doc.html> details. </a>
	 * 
	 * Not necessary to include in first version of the class, but included here
	 * as a reminder of its importance.
	 */
	private static final long serialVersionUID = 752647115602766100L;

	/**
	 * @param initialCapacity
	 * @param loadFactor
	 */
	public IntHashMapExt(final int initialCapacity, final float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * @param initialCapacity
	 */
	public IntHashMapExt(final int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * 
	 */
	public IntHashMapExt() {
		super();
	}

	/**
	 * @param m
	 */
	public IntHashMapExt(final Map m) {
		super(m);
	}

	/**
	 * method get
	 * 
	 * @param key
	 * @return
	 */
	public Object get(final int key) {
		return super.get(new Integer(key));
	}

	/**
	 * method put
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Object put(final int key, final Object value) {
		return super.put(new Integer(key), value);
	}

	/**
	 * method containsKey
	 * 
	 * @param key
	 * @return
	 */
	public boolean containsKey(final int key) {
		return super.containsKey(new Integer(key));
	}

	/**
	 * method remove
	 * 
	 * @param key
	 * @return
	 */
	public Object remove(final int key) {
		return super.remove(new Integer(key));
	}

	/**
	 * method keys
	 * 
	 * @return
	 */
	public int[] keys() {
		final Set set = this.keySet();
		final int size = set.size();
		final int[] retVal = new int[size];
		int ctr = 0;
		final Iterator itr = set.iterator();
		while (itr.hasNext()) {
			retVal[ctr++] = ((Integer) itr.next()).intValue();
		}
		return retVal;
	}

}
