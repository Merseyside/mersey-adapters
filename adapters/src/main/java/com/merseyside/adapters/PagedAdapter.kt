package com.merseyside.adapters

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.merseyside.adapters.core.base.callback.HasOnItemClickListener
import com.merseyside.adapters.core.base.callback.OnItemClickListener
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.holder.builder.BindingViewHolderBuilder
import com.merseyside.adapters.core.holder.builder.ViewHolderBuilder
import com.merseyside.adapters.core.model.VM
import com.merseyside.merseyLib.kotlin.logger.Logger

abstract class PagedAdapter<Parent : Any, Model : VM<Parent>>(diffUtil: DiffUtil.ItemCallback<Parent>)
    : PagedListAdapter<Parent, ViewHolder<Parent, Model>>(diffUtil),
    HasOnItemClickListener<Parent> {

    override var clickListeners: MutableList<OnItemClickListener<Parent>> = ArrayList()

    private var viewHolderBuilder: ViewHolderBuilder<Parent, Model> = BindingViewHolderBuilder(::getBindingVariable)

    protected fun setViewHolderBuilder(builder: ViewHolderBuilder<Parent, Model>) {
        viewHolderBuilder = builder
    }

    enum class NetworkState { ERROR, NO_CONNECTION, CONNECTED, LOADING }
    private var networkState: INetworkState? = null

    interface INetworkState {

        interface OnRetryListener {
            fun onRetry()
        }

        fun setMessage(msg: String)
        fun onStateChanged(state: NetworkState)
        fun getNetworkState(): NetworkState
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<Parent, Model> {
        return viewHolderBuilder.build(parent, viewType)
    }

    protected abstract fun createItemViewModel(obj: Parent): Model

    protected abstract fun getLayoutIdForPosition(position: Int): Int

    protected abstract fun getBindingVariable(): Int

    protected open fun getNetworkConnectionModel(): INetworkState {
        throw IllegalArgumentException("You have to override this method" +
                "in order to use network states")
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            getNetworkConnectionLayout()
        } else {
            getLayoutIdForPosition(position)
        }
    }

//    override fun onBindViewHolder(holder: ViewHolder<Parent, Model>, position: Int) {
//        if (hasExtraRow() && position == itemCount - 1) {
//            holder.bind(networkState!!)
//        } else {
//            val obj = getItem(position)
//
//            if (obj != null) {
//                holder.bind(getBindingVariable(), createItemViewModel(obj))
//
//                holder.itemView.setOnClickListener {
//                    clickListeners.forEach { listener -> listener.onItemClicked(obj) }
//                }
//            }
//        }
//
//    }

    private fun hasExtraRow() = networkState != null && networkState!!.getNetworkState() != NetworkState.CONNECTED

    @LayoutRes
    open fun getNetworkConnectionLayout(): Int {
        return 0
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    fun setNetworkState(newNetworkState: NetworkState, msg: String? = null) {
        Logger.log(this, "set network state $newNetworkState")
        if (networkState == null) {
            networkState = getNetworkConnectionModel()
        }

        val previousState = this.networkState!!.getNetworkState()
        val hadExtraRow = hasExtraRow()

        setupNetworkItem(newNetworkState, msg)

        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    private fun setupNetworkItem(networkState: NetworkState, msg: String? = null) {
        this.networkState?.apply {
            onStateChanged(networkState)
            if (msg != null) setMessage(msg)
        }
    }
}