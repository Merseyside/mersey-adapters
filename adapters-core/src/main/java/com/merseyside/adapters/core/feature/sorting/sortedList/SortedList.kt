package com.merseyside.adapters.core.feature.sorting.sortedList

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.core.feature.sorting.sortedList.SortedList.Callback
import com.merseyside.merseyLib.kotlin.logger.Logger
import com.merseyside.merseyLib.kotlin.logger.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*


/**
 * A Sorted list implementation that can keep items in order and also notify for changes in the
 * list
 * such that it can be bound to a [ RecyclerView.Adapter][RecyclerView.Adapter].
 *
 *
 * It keeps items ordered using the [Callback.compare] method and uses
 * binary search to retrieve items. If the sorting criteria of your items may change, make sure you
 * call appropriate methods while editing them to avoid data inconsistencies.
 *
 *
 * You can control the order of items and change notifications via the [Callback] parameter.
 */
class SortedList<T> @JvmOverloads constructor(
    private val mTClass: Class<T>,
    initialCapacity: Int = MIN_CAPACITY
) {
    private var mData: Array<T>

    /**
     * A reference to the previous set of data that is kept during a mutation operation (addAll or
     * replaceAll).
     */
    private var mOldData: Array<T>? = null

    /**
     * The current index into mOldData that has not yet been processed during a mutation operation
     * (addAll or replaceAll).
     */
    private var mOldDataStart = 0
    private var mOldDataSize = 0

    /**
     * The current index into the new data that has not yet been processed during a mutation
     * operation (addAll or replaceAll).
     */
    private var mNewDataStart = 0

    /**
     * The callback instance that controls the behavior of the SortedList and get notified when
     * changes happen.
     */

    val dummyCallback: Callback<T> = object : Callback<T>() {
        override fun compare(item1: T, item2: T): Int {
            return 0
        }

        override suspend fun onChanged(position: Int, count: Int) {}
        override suspend fun onInserted(position: Int, count: Int) {}
        override suspend fun onRemoved(position: Int, count: Int) {}
        override suspend fun onRemoved(position: Int, count: Int, item: T) {}
        override suspend fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun areItemsTheSame(item1: T, item2: T): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return false
        }

    }

    private var mCallback: Callback<T> = dummyCallback
    private var mBatchedCallback: BatchedCallback<T>? = null
    private var mSize: Int
    /**
     * Creates a new SortedList of type T.
     *
     * @param mTClass           The class of the contents of the SortedList.
     * @param callback        The callback that controls the behavior of SortedList.
     * @param initialCapacity The initial capacity to hold items.
     */
    /**
     * Creates a new SortedList of type T.
     *
     * @param klass    The class of the contents of the SortedList.
     * @param callback The callback that controls the behavior of SortedList.
     */
    init {
        mData = java.lang.reflect.Array.newInstance(mTClass, initialCapacity) as Array<T>
        mSize = 0
    }

    fun removeCallback() {
        mCallback = dummyCallback
    }

    fun setCallback(callback: Callback<T>) {
        mCallback = callback
    }

    fun getAll(): List<T> {
        return mData.take(mSize)
    }

    /**
     * The number of items in the list.
     *
     * @return The number of items in the list.
     */
    fun size(): Int {
        return mSize
    }

    /**
     * Adds the given item to the list. If this is a new item, SortedList calls
     * [Callback.onInserted].
     *
     *
     * If the item already exists in the list and its sorting criteria is not changed, it is
     * replaced with the existing Item. SortedList uses
     * [Callback.areItemsTheSame] to check if two items are the same item
     * and uses [Callback.areContentsTheSame] to decide whether it should
     * call [Callback.onChanged] or not. In both cases, it always removes the
     * reference to the old item and puts the new item into the backing array even if
     * [Callback.areContentsTheSame] returns false.
     *
     *
     * If the sorting criteria of the item is changed, SortedList won't be able to find
     * its duplicate in the list which will result in having a duplicate of the Item in the list.
     * If you need to update sorting criteria of an item that already exists in the list,
     * use [.updateItemAt]. You can find the index of the item using
     * [.indexOf] before you update the object.
     *
     * @param item The item to be added into the list.
     *
     * @return The index of the newly added item.
     * @see Callback.compare
     * @see Callback.areItemsTheSame
     * @see Callback.areContentsTheSame
     */
    suspend fun add(item: T): Int {
        throwIfInMutationOperation()
        return add(item, true)
    }

    /**
     * Adds the given items to the list. Equivalent to calling [SortedList.add] in a loop,
     * except the callback events may be in a different order/granularity since addAll can batch
     * them for better performance.
     *
     *
     * If allowed, will reference the input array during, and possibly after, the operation to avoid
     * extra memory allocation, in which case you should not continue to reference or modify the
     * array yourself.
     *
     *
     * @param items Array of items to be added into the list.
     * @param mayModifyInput If true, SortedList is allowed to modify and permanently reference the
     * input array.
     * @see SortedList.addAll
     */
    suspend fun addAll(items: Array<T>, mayModifyInput: Boolean) {
        throwIfInMutationOperation()
        if (items.isEmpty()) {
            return
        }
        if (mayModifyInput) {
            addAllInternal(items)
        } else {
            addAllInternal(copyArray(items))
        }
    }

    /**
     * Adds the given items to the list. Does not modify or retain the input.
     *
     * @see SortedList.addAll
     * @param items Array of items to be added into the list.
     */
    suspend fun addAll(vararg items: T) {
        addAll(items.toList())
    }

    /**
     * Adds the given items to the list. Does not modify or retain the input.
     *
     * @see SortedList.addAll
     * @param items Collection of items to be added into the list.
     */
    suspend fun addAll(items: Collection<T>) {
        addAll(items.toTypedArray(mTClass), mayModifyInput = true)
    }

    /**
     * Replaces the current items with the new items, dispatching [ListUpdateCallback] events
     * for each change detected as appropriate.
     *
     *
     * If allowed, will reference the input array during, and possibly after, the operation to avoid
     * extra memory allocation, in which case you should not continue to reference or modify the
     * array yourself.
     *
     *
     * Note: this method does not detect moves or dispatch
     * [ListUpdateCallback.onMoved] events. It instead treats moves as a remove
     * followed by an add and therefore dispatches [ListUpdateCallback.onRemoved]
     * and [ListUpdateCallback.onRemoved] events.  See [DiffUtil] if you want
     * your implementation to dispatch move events.
     *
     *
     * @param items Array of items to replace current items.
     * @param mayModifyInput If true, SortedList is allowed to modify and permanently reference the
     * input array.
     * @see .replaceAll
     */
    suspend fun replaceAll(items: Array<T>, mayModifyInput: Boolean) {
        throwIfInMutationOperation()
        if (mayModifyInput) {
            replaceAllInternal(items)
        } else {
            replaceAllInternal(copyArray(items))
        }
    }

    /**
     * Replaces the current items with the new items, dispatching [ListUpdateCallback] events
     * for each change detected as appropriate.  Does not modify or retain the input.
     *
     * @see .replaceAll
     * @param items Array of items to replace current items.
     */
    suspend fun replaceAll(vararg items: T) {
        replaceAll(items.toList().toTypedArray(mTClass), false)
    }

    /**
     * Replaces the current items with the new items, dispatching [ListUpdateCallback] events
     * for each change detected as appropriate. Does not modify or retain the input.
     *
     * @see .replaceAll
     * @param items Array of items to replace current items.
     */
    suspend fun replaceAll(items: Collection<T>) {
        replaceAll(items.toTypedArray(mTClass), mayModifyInput = true)
    }

    private suspend fun addAllInternal(newItems: Array<T>) {
        if (newItems.isEmpty()) {
            return
        }
        val newSize = sortAndDedup(newItems)
        if (mSize == 0) {
            mData = newItems
            mSize = newSize
            mCallback.onInserted(0, newSize)
        } else {
            merge(newItems, newSize)
        }
    }

    private suspend fun replaceAllInternal(newData: Array<T>) {
        val forceBatchedUpdates = mCallback !is BatchedCallback<*>
        if (forceBatchedUpdates) {
            beginBatchedUpdates()
        }
        mOldDataStart = 0
        mOldDataSize = mSize
        mOldData = mData
        mNewDataStart = 0
        val newSize = sortAndDedup(newData)
        mData = java.lang.reflect.Array.newInstance(mTClass, newSize) as Array<T>
        while (mNewDataStart < newSize || mOldDataStart < mOldDataSize) {
            if (mOldDataStart >= mOldDataSize) {
                val insertIndex = mNewDataStart
                val itemCount = newSize - mNewDataStart
                System.arraycopy(newData, insertIndex, mData, insertIndex, itemCount)
                mNewDataStart += itemCount
                mSize += itemCount
                mCallback.onInserted(insertIndex, itemCount)
                break
            }
            if (mNewDataStart >= newSize) {
                val itemCount = mOldDataSize - mOldDataStart
                mSize -= itemCount
                mCallback.onRemoved(mNewDataStart, itemCount)
                break
            }
            val oldItem = mOldData!![mOldDataStart]
            val newItem = newData[mNewDataStart]
            val result = mCallback.compare(oldItem, newItem)
            if (result < 0) {
                replaceAllRemove()
            } else if (result > 0) {
                replaceAllInsert(newItem)
            } else {
                if (!mCallback.areItemsTheSame(oldItem, newItem)) {
                    // The items aren't the same even though they were supposed to occupy the same
                    // place, so both notify to remove and add an item in the current location.
                    replaceAllRemove()
                    replaceAllInsert(newItem)
                } else {
                    mData[mNewDataStart] = newItem
                    mOldDataStart++
                    mNewDataStart++
                    if (!mCallback.areContentsTheSame(oldItem, newItem)) {
                        // The item is the same but the contents have changed, so notify that an
                        // onChanged event has occurred.
                        mCallback.onChanged(
                            mNewDataStart - 1, 1,
                            mCallback.getChangePayload(oldItem, newItem)
                        )
                    }
                }
            }
        }
        mOldData = null
        if (forceBatchedUpdates) {
            endBatchedUpdates()
        }
    }

    private suspend fun replaceAllInsert(newItem: T) {
        mData[mNewDataStart] = newItem
        mNewDataStart++
        mSize++
        mCallback.onInserted(mNewDataStart - 1, 1)
    }

    private suspend fun replaceAllRemove() {
        mSize--
        mOldDataStart++
        mCallback.onRemoved(mNewDataStart, 1)
    }

    /**
     * Sorts and removes duplicate items, leaving only the last item from each group of "same"
     * items. Move the remaining items to the beginning of the array.
     *
     * @return Number of deduplicated items at the beginning of the array.
     */
    private fun sortAndDedup(items: Array<T>): Int {
        if (items.isEmpty()) {
            return 0
        }

        // Arrays.sort is stable.
        Arrays.sort(items, mCallback)

        // Keep track of the range of equal items at the end of the output.
        // Start with the range containing just the first item.
        var rangeStart = 0
        var rangeEnd = 1
        for (i in 1 until items.size) {
            val currentItem = items[i]
            val compare = mCallback.compare(items[rangeStart], currentItem)
            if (compare == 0) {
                // The range of equal items continues, update it.
                val sameItemPos = findSameItem(currentItem, items, rangeStart, rangeEnd)
                if (sameItemPos != INVALID_POSITION) {
                    // Replace the duplicate item.
                    items[sameItemPos] = currentItem
                } else {
                    // Expand the range.
                    if (rangeEnd != i) {  // Avoid redundant copy.
                        items[rangeEnd] = currentItem
                    }
                    rangeEnd++
                }
            } else {
                // The range has ended. Reset it to contain just the current item.
                if (rangeEnd != i) {  // Avoid redundant copy.
                    items[rangeEnd] = currentItem
                }
                rangeStart = rangeEnd++
            }
        }

        return rangeEnd
    }

    private fun findSameItem(item: T, items: Array<T>, from: Int, to: Int): Int {
        for (pos in from until to) {
            if (mCallback.areItemsTheSame(items[pos], item)) {
                return pos
            }
        }
        return INVALID_POSITION
    }

    /**
     * This method assumes that newItems are sorted and deduplicated.
     */
    private suspend fun merge(newData: Array<T>, newDataSize: Int) {
        val forceBatchedUpdates = mCallback !is BatchedCallback<*>
        if (forceBatchedUpdates) {
            beginBatchedUpdates()
        }
        mOldData = mData
        mOldDataStart = 0
        mOldDataSize = mSize
        val mergedCapacity = mSize + newDataSize + CAPACITY_GROWTH
        mData = java.lang.reflect.Array.newInstance(mTClass, mergedCapacity) as Array<T>
        mNewDataStart = 0
        var newDataStart = 0
        while (mOldDataStart < mOldDataSize || newDataStart < newDataSize) {
            if (mOldDataStart == mOldDataSize) {
                // No more old items, copy the remaining new items.
                val itemCount = newDataSize - newDataStart
                System.arraycopy(newData, newDataStart, mData, mNewDataStart, itemCount)
                mNewDataStart += itemCount
                mSize += itemCount
                mCallback.onInserted(mNewDataStart - itemCount, itemCount)
                break
            }
            if (newDataStart == newDataSize) {
                // No more new items, copy the remaining old items.
                val itemCount = mOldDataSize - mOldDataStart
                System.arraycopy(mOldData, mOldDataStart, mData, mNewDataStart, itemCount)
                mNewDataStart += itemCount
                break
            }
            val oldItem = mOldData!![mOldDataStart]
            val newItem = newData[newDataStart]
            val compare = mCallback.compare(oldItem, newItem)
            if (compare > 0) {
                // New item is lower, output it.
                mData[mNewDataStart++] = newItem
                mSize++
                newDataStart++
                mCallback.onInserted(mNewDataStart - 1, 1)
            } else if (compare == 0 && mCallback.areItemsTheSame(oldItem, newItem)) {
                // Items are the same. Output the new item, but consume both.
                mData[mNewDataStart++] = newItem
                newDataStart++
                mOldDataStart++
                if (!mCallback.areContentsTheSame(oldItem, newItem)) {
                    mCallback.onChanged(
                        mNewDataStart - 1, 1,
                        mCallback.getChangePayload(oldItem, newItem)
                    )
                }
            } else {
                // Old item is lower than or equal to (but not the same as the new). Output it.
                // New item with the same sort order will be inserted later.
                mData[mNewDataStart++] = oldItem
                mOldDataStart++
            }
        }
        mOldData = null
        if (forceBatchedUpdates) {
            endBatchedUpdates()
        }
    }

    /**
     * Throws an exception if called while we are in the middle of a mutation operation (addAll or
     * replaceAll).
     */
    private fun throwIfInMutationOperation() {
        if (mOldData != null) {
            throw IllegalStateException(
                "Data cannot be mutated in the middle of a batch "
                        + "update operation such as addAll or replaceAll."
            )
        }
    }

    /**
     * Batches adapter updates that happen after calling this method and before calling
     * [.endBatchedUpdates]. For example, if you add multiple items in a loop
     * and they are placed into consecutive indices, SortedList calls
     * [Callback.onInserted] only once with the proper item count. If an event
     * cannot be merged with the previous event, the previous event is dispatched
     * to the callback instantly.
     *
     *
     * After running your data updates, you **must** call [.endBatchedUpdates]
     * which will dispatch any deferred data change event to the current callback.
     *
     *
     * A sample implementation may look like this:
     * <pre>
     * mSortedList.beginBatchedUpdates();
     * try {
     * mSortedList.add(item1)
     * mSortedList.add(item2)
     * mSortedList.remove(item3)
     * ...
     * } finally {
     * mSortedList.endBatchedUpdates();
     * }
    </pre> *
     *
     *
     * Instead of using this method to batch calls, you can use a Callback that extends
     * [BatchedCallback]. In that case, you must make sure that you are manually calling
     * [BatchedCallback.dispatchLastEvent] right after you complete your data changes.
     * Failing to do so may create data inconsistencies with the Callback.
     *
     *
     * If the current Callback is an instance of [BatchedCallback], calling this method
     * has no effect.
     */
    fun beginBatchedUpdates() {
        throwIfInMutationOperation()
        if (mCallback is BatchedCallback<*>) {
            return
        }
        if (mBatchedCallback == null) {
            mBatchedCallback = BatchedCallback(mCallback).also {
                mCallback = it
            }
        }
    }

    /**
     * Ends the update transaction and dispatches any remaining event to the callback.
     */
    suspend fun endBatchedUpdates() {
        throwIfInMutationOperation()
        if (mCallback is BatchedCallback<*>) {
            (mCallback as BatchedCallback<*>).dispatchLastEvent()
        }
        if (mCallback === mBatchedCallback) {
            mCallback = mBatchedCallback!!.mWrappedCallback
        }
    }

    private suspend fun add(item: T, notify: Boolean): Int {
        var index = findIndexOf(item, mData, 0, mSize, INSERTION)
        if (index == INVALID_POSITION) {
            index = 0
        } else if (index < mSize) {
            val existing = mData[index]
            if (mCallback.areItemsTheSame(existing, item)) {
                if (mCallback.areContentsTheSame(existing, item)) {
                    //no change but still replace the item
                    mData[index] = item
                    return index
                } else {
                    mData[index] = item
                    mCallback.onChanged(index, 1, mCallback.getChangePayload(existing, item))
                    return index
                }
            }
        }
        addToData(index, item)
        if (notify) {
            mCallback.onInserted(index, 1)
        }
        return index
    }

    /**
     * Removes the provided item from the list and calls [Callback.onRemoved].
     *
     * @param item The item to be removed from the list.
     *
     * @return True if item is removed, false if item cannot be found in the list.
     */
    suspend fun remove(item: T): Boolean {
        throwIfInMutationOperation()
        return remove(item, true)
    }

    /**
     * Removes the item at the given index and calls [Callback.onRemoved].
     *
     * @param index The index of the item to be removed.
     *
     * @return The removed item.
     */
    suspend fun removeItemAt(index: Int): T {
        throwIfInMutationOperation()
        val item = get(index)
        removeItemAtIndex(index,true, item)
        return item
    }

    private suspend fun remove(item: T, notify: Boolean): Boolean {
        val index = findIndexOf(item, mData, 0, mSize, DELETION)
        if (index == INVALID_POSITION) {
            throw RuntimeException("OnRemove invalid position. Check your comparator implementation.")
        }
        removeItemAtIndex(index, notify, item)
        return true
    }

    private suspend fun removeItemAtIndex(index: Int, notify: Boolean, item: T) {
        System.arraycopy(mData, index + 1, mData, index, mSize - index - 1)
        mSize--
        //mData[mSize] = null
        if (notify) {
            mCallback.onRemoved(index, 1, item)
        }
    }

    /**
     * Updates the item at the given index and calls [Callback.onChanged] and/or
     * [Callback.onMoved] if necessary.
     *
     *
     * You can use this method if you need to change an existing Item such that its position in the
     * list may change.
     *
     *
     * If the new object is a different object (`get(index) != item`) and
     * [Callback.areContentsTheSame] returns `true`, SortedList
     * avoids calling [Callback.onChanged] otherwise it calls
     * [Callback.onChanged].
     *
     *
     * If the new position of the item is different than the provided `index`,
     * SortedList
     * calls [Callback.onMoved].
     *
     * @param index The index of the item to replace
     * @param item  The item to replace the item at the given Index.
     * @see .add
     */
    suspend fun updateItemAt(index: Int, item: T) {
        throwIfInMutationOperation()
        val existing = get(index)
        // assume changed if the same object is given back
        val contentsChanged = existing === item || !mCallback.areContentsTheSame(existing, item)
        if (existing !== item) {
            // different items, we can use comparison and may avoid lookup
            val cmp = mCallback.compare(existing, item)
            if (cmp == 0) {
                mData[index] = item
                if (contentsChanged) {
                    mCallback.onChanged(index, 1, mCallback.getChangePayload(existing, item))
                }
                return
            }
        }
        if (contentsChanged) {
            mCallback.onChanged(index, 1, mCallback.getChangePayload(existing, item))
        }
        // TODO this done in 1 pass to avoid shifting twice.
        removeItemAtIndex(index, false, item)
        val newIndex = add(item, false)
        if (index != newIndex) {
            mCallback.onMoved(index, newIndex)
        }
    }

    /**
     * This method can be used to recalculate the position of the item at the given index, without
     * triggering an [Callback.onChanged] callback.
     *
     *
     * If you are editing objects in the list such that their position in the list may change but
     * you don't want to trigger an onChange animation, you can use this method to re-position it.
     * If the item changes position, SortedList will call [Callback.onMoved]
     * without
     * calling [Callback.onChanged].
     *
     *
     * A sample usage may look like:
     *
     * <pre>
     * final int position = mSortedList.indexOf(item);
     * item.incrementPriority(); // assume items are sorted by priority
     * mSortedList.recalculatePositionOfItemAt(position);
    </pre> *
     * In the example above, because the sorting criteria of the item has been changed,
     * mSortedList.indexOf(item) will not be able to find the item. This is why the code above
     * first
     * gets the position before editing the item, edits it and informs the SortedList that item
     * should be repositioned.
     *
     * @param index The current index of the Item whose position should be re-calculated.
     * @see .updateItemAt
     * @see .add
     */
    suspend fun recalculatePositionOfItemAt(index: Int): Int {
        throwIfInMutationOperation()
        // TODO can be improved
        val item = get(index)
        removeItemAtIndex(index, false, item)
        val newIndex = add(item, false)
        if (index != newIndex) {
            mCallback.onMoved(index, newIndex)
        }

        return newIndex
    }

    /**
     * Returns the item at the given index.
     *
     * @param index The index of the item to retrieve.
     *
     * @return The item at the given index.
     * @throws java.lang.IndexOutOfBoundsException if provided index is negative or larger than the
     * size of the list.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun get(index: Int): T {
        if (index >= mSize || index < 0) {
            throw IndexOutOfBoundsException(
                ("Asked to get item at " + index + " but size is "
                        + mSize)
            )
        }
        if (mOldData != null) {
            // The call is made from a callback during addAll execution. The data is split
            // between mData and mOldData.
            if (index >= mNewDataStart) {
                return mOldData!![index - mNewDataStart + mOldDataStart]
            }
        }
        return mData[index]
    }

    /**
     * Returns the position of the provided item.
     *
     * @param item The item to query for position.
     *
     * @return The position of the provided item or [.INVALID_POSITION] if item is not in the
     * list.
     */
    fun indexOf(item: T): Int {
        if (mOldData != null) {
            var index = findIndexOf(item, mData, 0, mNewDataStart, LOOKUP)
            if (index != INVALID_POSITION) {
                return index
            }
            index = findIndexOf(item, mOldData!!, mOldDataStart, mOldDataSize, LOOKUP)
            return if (index != INVALID_POSITION) {
                index - mOldDataStart + mNewDataStart
            } else INVALID_POSITION
        }
        return findIndexOf(item, mData, 0, mSize, LOOKUP)
    }

    private fun findIndexOf(item: T, mData: Array<T>, left: Int, right: Int, reason: Int): Int {
        var left = left
        var right = right
        while (left < right) {
            val middle = (left + right) / 2
            val myItem = mData[middle]
            val cmp = mCallback.compare(myItem, item)
            if (cmp < 0) {
                left = middle + 1
            } else if (cmp == 0) {
                return if (mCallback.areItemsTheSame(myItem, item)) {
                    middle
                } else {
                    val exact = linearEqualitySearch(item, middle, left, right)
                    if (reason == INSERTION) {
                        if (exact == INVALID_POSITION) middle else exact
                    } else {
                        exact
                    }
                }
            } else {
                right = middle
            }
        }
        return if (reason == INSERTION) left else INVALID_POSITION
    }

    private fun linearEqualitySearch(item: T, middle: Int, left: Int, right: Int): Int {
        // go left
        for (next in middle - 1 downTo left) {
            val nextItem = mData[next]
            val cmp = mCallback.compare(nextItem, item)
            if (cmp != 0) {
                break
            }
            if (mCallback.areItemsTheSame(nextItem, item)) {
                return next
            }
        }
        for (next in middle + 1 until right) {
            val nextItem = mData[next]
            val cmp = mCallback.compare(nextItem, item)
            if (cmp != 0) {
                break
            }
            if (mCallback.areItemsTheSame(nextItem, item)) {
                return next
            }
        }
        return INVALID_POSITION
    }

    private fun addToData(index: Int, item: T) {
        if (index > mSize) {
            throw IndexOutOfBoundsException(
                "cannot add item to $index because size is $mSize"
            )
        }
        if (mSize == mData.size) {
            // we are at the limit enlarge
            val newData = java.lang.reflect.Array.newInstance(
                mTClass,
                mData.size + CAPACITY_GROWTH
            ) as Array<T>
            System.arraycopy(mData, 0, newData, 0, index)
            newData[index] = item
            System.arraycopy(mData, index, newData, index + 1, mSize - index)
            mData = newData
        } else {
            // just shift, we fit
            System.arraycopy(mData, index, mData, index + 1, mSize - index)
            mData[index] = item
        }
        mSize++
    }

    private fun copyArray(items: Array<T>): Array<T> {
        val copy = java.lang.reflect.Array.newInstance(mTClass, items.size) as Array<T>
        System.arraycopy(items, 0, copy, 0, items.size)
        return copy
    }

    /**
     * Removes all items from the SortedList.
     */
    suspend fun clear() {
        throwIfInMutationOperation()
        if (mSize == 0) {
            return
        }
        val prevSize = mSize
        Arrays.fill(mData, 0, prevSize, null)
        mSize = 0
        mCallback.onRemoved(0, prevSize)
    }

    /**
     * The class that controls the behavior of the [SortedList].
     *
     *
     * It defines how items should be sorted and how duplicates should be handled.
     *
     *
     * SortedList calls the callback methods on this class to notify changes about the underlying
     * data.
     */


    abstract class Callback<T2> : ListUpdateCallback<T2>, Comparator<T2> {

        /**
         * Similar to [java.util.Comparator.compare], should compare two and
         * return how they should be ordered.
         *
         * @param item1 The first object to compare.
         * @param item2 The second object to compare.
         *
         * @return a negative integer, zero, or a positive integer as the
         * first argument is less than, equal to, or greater than the
         * second.
         */

        abstract override fun compare(item1: T2, item2: T2): Int

        override suspend fun onChanged(position: Int, count: Int, payloads: Any?) {
            onChanged(position, count)
        }

        abstract suspend fun onChanged(position: Int, count: Int)

        /**
         * Called by the SortedList when it wants to check whether two items have the same data
         * or not. SortedList uses this information to decide whether it should call
         * [.onChanged] or not.
         *
         *
         * SortedList uses this method to check equality instead of [Object.equals]
         * so
         * that you can change its behavior depending on your UI.
         *
         *
         * For example, if you are using SortedList with a
         * [RecyclerView.Adapter], you should
         * return whether the items' visual representations are the same or not.
         *
         * @param oldItem The previous representation of the object.
         * @param newItem The new object that replaces the previous one.
         *
         * @return True if the contents of the items are the same or false if they are different.
         */
        abstract fun areContentsTheSame(oldItem: T2, newItem: T2): Boolean

        /**
         * Called by the SortedList to decide whether two objects represent the same Item or not.
         *
         *
         * For example, if your items have unique ids, this method should check their equality.
         *
         * @param item1 The first item to check.
         * @param item2 The second item to check.
         *
         * @return True if the two items represent the same object or false if they are different.
         */
        abstract fun areItemsTheSame(item1: T2, item2: T2): Boolean

        /**
         * When [.areItemsTheSame] returns `true` for two items and
         * [.areContentsTheSame] returns false for them, [Callback] calls this
         * method to get a payload about the change.
         *
         *
         * For example, if you are using [Callback] with
         * [RecyclerView], you can return the particular field that
         * changed in the item and your
         * [ItemAnimator][RecyclerView.ItemAnimator] can use that
         * information to run the correct animation.
         *
         *
         * Default implementation returns `null`.
         *
         * @param item1 The first item to check.
         * @param item2 The second item to check.
         * @return A payload object that represents the changes between the two items.
         */
        open fun getChangePayload(item1: T2, item2: T2): Any? {
            return null
        }
    }

    /**
     * A callback implementation that can batch notify events dispatched by the SortedList.
     *
     *
     * This class can be useful if you want to do multiple operations on a SortedList but don't
     * want to dispatch each event one by one, which may result in a performance issue.
     *
     *
     * For example, if you are going to add multiple items to a SortedList, BatchedCallback call
     * convert individual `onInserted(index, 1)` calls into one
     * `onInserted(index, N)` if items are added into consecutive indices. This change
     * can help RecyclerView resolve changes much more easily.
     *
     *
     * If consecutive changes in the SortedList are not suitable for batching, BatchingCallback
     * dispatches them as soon as such case is detected. After your edits on the SortedList is
     * complete, you **must** always call [BatchedCallback.dispatchLastEvent] to flush
     * all changes to the Callback.
     */
    class BatchedCallback<Item>(val mWrappedCallback: Callback<Item>) : Callback<Item>() {
        private val mBatchingListUpdateCallback: BatchingListUpdateCallback<Item> =
            BatchingListUpdateCallback(mWrappedCallback)

        override fun compare(item1: Item, item2: Item): Int {
            return mWrappedCallback.compare(item1, item2)
        }

        override suspend fun onInserted(position: Int, count: Int) {
            mBatchingListUpdateCallback.onInserted(position, count)
        }

        override suspend fun onRemoved(position: Int, count: Int) {
            mBatchingListUpdateCallback.onRemoved(position, count)
        }

        override suspend fun onRemoved(position: Int, count: Int, item: Item) {
            mBatchingListUpdateCallback.onRemoved(position, count, item)
        }

        override suspend fun onMoved(fromPosition: Int, toPosition: Int) {
            mBatchingListUpdateCallback.onMoved(fromPosition, toPosition)
        }

        override suspend fun onChanged(position: Int, count: Int) {
            mBatchingListUpdateCallback.onChanged(position, count, null)
        }

        override suspend fun onChanged(position: Int, count: Int, payload: Any?) {
            mBatchingListUpdateCallback.onChanged(position, count, payload)
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return mWrappedCallback.areContentsTheSame(oldItem, newItem)
        }

        override fun areItemsTheSame(item1: Item, item2: Item): Boolean {
            return mWrappedCallback.areItemsTheSame(item1, item2)
        }

        override fun getChangePayload(item1: Item, item2: Item): Any? {
            return mWrappedCallback.getChangePayload(item1, item2)
        }

        /**
         * This method dispatches any pending event notifications to the wrapped Callback.
         * You **must** always call this method after you are done with editing the SortedList.
         */
        suspend fun dispatchLastEvent() = withContext(Dispatchers.Main) {
            mBatchingListUpdateCallback.dispatchLastEvent()
        }
    }

    companion object {
        /**
         * Used by [.indexOf] when the item cannot be found in the list.
         */
        val INVALID_POSITION = -1
        private val MIN_CAPACITY = 10
        private val CAPACITY_GROWTH = MIN_CAPACITY
        private val INSERTION = 1
        private val DELETION = 1 shl 1
        private val LOOKUP = 1 shl 2
    }
}

interface ListUpdateCallback<T> {
    /**
     * Called when `count` number of items are inserted at the given position.
     *
     * @param position The position of the new item.
     * @param count    The number of items that have been added.
     */
    suspend fun onInserted(position: Int, count: Int)

    /**
     * Called when `count` number of items are removed from the given position.
     *
     * @param position The position of the item which has been removed.
     * @param count    The number of items which have been removed.
     */
    suspend fun onRemoved(position: Int, count: Int) {}

    suspend fun onRemoved(position: Int, count: Int, item: T) {}

    /**
     * Called when an item changes its position in the list.
     *
     * @param fromPosition The previous position of the item before the move.
     * @param toPosition   The new position of the item.
     */
    suspend fun onMoved(fromPosition: Int, toPosition: Int)


    /**
     * Called by the SortedList when the item at the given position is updated.
     *
     * @param position The position of the item which has been updated.
     * @param count    The number of items which has changed.
     */
    suspend fun onChanged(position: Int, count: Int, payloads: Any?)

}

class BatchingListUpdateCallback<T>(val mWrapped: ListUpdateCallback<T>) :
    ListUpdateCallback<T> {
    var mLastEventType: Int = TYPE_NONE
    var mLastEventPosition = -1
    var mLastEventCount = -1
    var mLastEventPayload: Any? = null

    /**
     * BatchingListUpdateCallback holds onto the last event to see if it can be merged with the
     * next one. When stream of events finish, you should call this method to dispatch the last
     * event.
     */
    suspend fun dispatchLastEvent() {
        if (mLastEventType == TYPE_NONE) {
            return
        }
        when (mLastEventType) {
            TYPE_ADD -> mWrapped.onInserted(
                mLastEventPosition,
                mLastEventCount
            )

            TYPE_REMOVE -> mWrapped.onRemoved(
                mLastEventPosition,
                mLastEventCount
            )

            TYPE_CHANGE -> mWrapped.onChanged(
                mLastEventPosition,
                mLastEventCount,
                mLastEventPayload
            )
        }
        mLastEventPayload = null
        mLastEventType = TYPE_NONE
    }

    override suspend fun onInserted(position: Int, count: Int) {
        if (mLastEventType == TYPE_ADD && position >= mLastEventPosition && position <= mLastEventPosition + mLastEventCount) {
            mLastEventCount += count
            mLastEventPosition = Math.min(position, mLastEventPosition)
            return
        }
        dispatchLastEvent()
        mLastEventPosition = position
        mLastEventCount = count
        mLastEventType = TYPE_ADD
    }

    override suspend fun onRemoved(position: Int, count: Int) {
        if (mLastEventType == TYPE_REMOVE && mLastEventPosition >= position && mLastEventPosition <= position + count) {
            mLastEventCount += count
            mLastEventPosition = position
            return
        }
        dispatchLastEvent()
        mLastEventPosition = position
        mLastEventCount = count
        mLastEventType = TYPE_REMOVE
    }

    override suspend fun onMoved(fromPosition: Int, toPosition: Int) {
        dispatchLastEvent() // moves are not merged
        mWrapped.onMoved(fromPosition, toPosition)
    }

    @SuppressLint("UnknownNullness") // b/240775049: Cannot annotate properly
    override suspend fun onChanged(position: Int, count: Int, payload: Any?) {
        if (mLastEventType == TYPE_CHANGE &&
            !(position > mLastEventPosition + mLastEventCount || position + count < mLastEventPosition || mLastEventPayload !== payload)
        ) {
            // take potential overlap into account
            val previousEnd = mLastEventPosition + mLastEventCount
            mLastEventPosition = Math.min(position, mLastEventPosition)
            mLastEventCount = Math.max(previousEnd, position + count) - mLastEventPosition
            return
        }
        dispatchLastEvent()
        mLastEventPosition = position
        mLastEventCount = count
        mLastEventPayload = payload
        mLastEventType = TYPE_CHANGE
    }

    companion object {
        private const val TYPE_NONE = 0
        private const val TYPE_ADD = 1
        private const val TYPE_REMOVE = 2
        private const val TYPE_CHANGE = 3
    }
}

fun <T> Collection<T>.toTypedArray(clazz: Class<T>): Array<T> {
    val array = java.lang.reflect.Array.newInstance(clazz, size) as Array<T>
    val thisCollection = this as java.util.Collection<T>
    return thisCollection.toArray(array) as Array<T>
}