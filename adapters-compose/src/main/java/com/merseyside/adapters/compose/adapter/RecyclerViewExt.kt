package com.merseyside.adapters.compose.adapter

import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.setAdapterComposer(composer: AdapterComposer) {
    adapter = composer.provideAdapter()
}