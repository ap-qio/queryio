package com.queryio.common.util;

public abstract class MergeSort extends Object {
	protected Object toSort[];
	protected Object swapSpace[];

	public final void sort(final Object array[]) {
		if ((array != null) && (array.length > 1)) {
			int maxLength;

			maxLength = array.length;
			this.swapSpace = new Object[maxLength];
			this.toSort = array;
			this.mergeSort(0, maxLength - 1);
			this.swapSpace = null;
			this.toSort = null;
		}
	}

	public abstract int compareElementsAt(int beginLoc, int endLoc);

	protected final void mergeSort(final int begin, final int end) {
		if (begin != end) {
			int mid;

			mid = (begin + end) / 2;
			this.mergeSort(begin, mid);
			this.mergeSort(mid + 1, end);
			this.merge(begin, mid, end);
		}
	}

	protected final void merge(final int begin, final int middle, final int end) {
		int firstHalf, secondHalf, count;

		firstHalf = begin;
		count = begin;
		secondHalf = middle + 1;
		while ((firstHalf <= middle) && (secondHalf <= end)) {
			if (this.compareElementsAt(secondHalf, firstHalf) < 0) {
				this.swapSpace[count++] = this.toSort[secondHalf++];
			} else {
				this.swapSpace[count++] = this.toSort[firstHalf++];
			}
		}
		if (firstHalf <= middle) {
			while (firstHalf <= middle) {
				this.swapSpace[count++] = this.toSort[firstHalf++];
			}
		} else {
			while (secondHalf <= end) {
				this.swapSpace[count++] = this.toSort[secondHalf++];
			}
		}
		/*
		 * for (count = begin; count <= end; count++) { toSort[count] =
		 * swapSpace[count]; }
		 */
		System.arraycopy(this.swapSpace, begin, this.toSort, begin, end - begin + 1);
	}
}
