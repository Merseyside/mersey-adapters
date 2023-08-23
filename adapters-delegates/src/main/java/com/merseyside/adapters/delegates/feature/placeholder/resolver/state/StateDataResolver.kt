package com.merseyside.adapters.delegates.feature.placeholder.resolver.state

import androidx.lifecycle.LifecycleOwner
import com.merseyside.adapters.core.feature.dataProvider.DataObserver
import com.merseyside.adapters.core.feature.dataProvider.dataProvider
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.delegates.composites.CompositeAdapter
import com.merseyside.adapters.delegates.feature.placeholder.resolver.PlaceholderDataResolver
import kotlinx.coroutines.flow.Flow

abstract class StateDataResolver<StateData, Parent, ParentModel : VM<Parent>>(
    private val viewLifecycleOwner: LifecycleOwner,
    private val flow: Flow<StateData>
) : PlaceholderDataResolver<Parent, ParentModel>(), DataObserver<StateData, Parent> {

    override fun initAdapter(adapter: CompositeAdapter<Parent, ParentModel>) {
        super.initAdapter(adapter)
        adapter.dataProvider(flow).apply {
            observeForever(this@StateDataResolver)
        }
    }

    override suspend fun onModelListChanged(oldSize: Int, newSize: Int, hasChanges: Boolean) {}
}