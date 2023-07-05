package com.merseyside.adapters.compose.view.card

import android.content.Context
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.compose.BR
import com.merseyside.adapters.compose.R
import com.merseyside.adapters.compose.adapter.ViewCompositeAdapter
import com.merseyside.adapters.compose.manager.ViewDelegatesManager
import com.merseyside.adapters.compose.model.ViewAdapterViewModel
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.compose.view.viewGroup.ComposingViewGroupDelegate
import com.merseyside.adapters.core.feature.selecting.Selecting
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.merseyLib.kotlin.utils.safeLet
import com.merseyside.utils.ext.getDimension
import com.merseyside.utils.ext.getDimensionPixelSize

class ComposingCardDelegate : ComposingViewGroupDelegate<ComposingCard, ComposingCardStyle,
        ComposingCardViewModel<ComposingCard>,
        SCV, ViewAdapterViewModel, ViewCompositeAdapter<SCV, ViewAdapterViewModel>>() {

    override fun getLayoutIdForItem() = R.layout.view_composing_card
    override fun createItemViewModel(item: ComposingCard) = ComposingCardViewModel(item)
    override fun getBindingVariable() = BR.model

    override fun isResponsibleForItemClass(clazz: Class<out SCV>): Boolean {
        return clazz == ComposingCard::class.java
    }

    override fun applyStyle(
        context: Context,
        holder: ViewHolder<SCV, ComposingCardViewModel<ComposingCard>>,
        style: ComposingCardStyle
    ) {
        super.applyStyle(context, holder, style)
        val card = holder.root as CardView
        safeLet(style.cardCornerRadius) { radius ->
            card.radius = context.getDimension(radius)
        }
        safeLet(style.cardElevation) { elevation ->
            card.cardElevation = context.getDimension(elevation)
        }
        safeLet(style.backgroundCardColor) { color ->
            card.setCardBackgroundColor(ContextCompat.getColor(context, color))
        }
        safeLet(style.contentPaddings) { paddings ->
            with(paddings) {
                card.setContentPadding(
                    context.getDimensionPixelSize(start),
                    context.getDimensionPixelSize(top),
                    context.getDimensionPixelSize(end),
                    context.getDimensionPixelSize(bottom)
                )
            }
        }
    }

    override fun createCompositeAdapter(
        model: ComposingCardViewModel<ComposingCard>,
        delegateManager: ViewDelegatesManager<SCV, ViewAdapterViewModel>
    ): ViewCompositeAdapter<SCV, ViewAdapterViewModel> {
        return ViewCompositeAdapter(delegateManager) {
            apply(model.item.listConfig.adapterConfig)

            Selecting {
                with(model.item) {
                    selectableMode = listConfig.selectableMode
                    isSelectEnabled = listConfig.isSelectEnabled
                    isAllowToCancelSelection = listConfig.isAllowToCancelSelection

                    onSelect = { item, isSelected, isSelectedByUser ->
                        model.item.listConfig.notifyOnSelected(item, isSelected, isSelectedByUser)
                    }
                }
            }
        }
    }

    override fun getNestedRecyclerView(
        holder: ViewHolder<SCV, ComposingCardViewModel<ComposingCard>>,
        model: ComposingCardViewModel<ComposingCard>
    ): RecyclerView? {
        return (holder.root as CardView).findViewById<RecyclerView?>(R.id.list)
            .also { recyclerView ->
                with(model.item.listConfig) {
                    recyclerView.layoutManager = layoutManager(holder.context)
                    safeLet(decorator) { recyclerView.addItemDecoration(it) }
                }
            }
    }
}