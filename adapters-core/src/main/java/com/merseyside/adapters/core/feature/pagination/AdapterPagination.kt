package com.merseyside.adapters.core.feature.pagination

import androidx.lifecycle.LifecycleOwner
import com.merseyside.adapters.core.async.addAsync
import com.merseyside.adapters.core.async.clearAsync
import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.feature.dataProvider.dataProvider
import kotlinx.coroutines.flow.Flow

class AdapterPagination<Parent>(
    private val adapter: IBaseAdapter<Parent, *>,
    viewLifecycleOwner: LifecycleOwner,
    onNextPage: Flow<List<Parent>>,
    onPrevPage: Flow<List<Parent>>?
) {

    init {
        adapter.dataProvider(viewLifecycleOwner, onNextPage) { data ->
            addAsync(data)
        }

        if (onPrevPage != null) {
            adapter.dataProvider(viewLifecycleOwner, onPrevPage) { data ->
                addAsync(position = 0, data)
            }
        }
    }

    fun resetPaging() {
        adapter.clearAsync()
    }
}