package com.merseyside.adapters.coroutine

//fun <Parent> BaseAdapter<Parent, *>.setFlow(
//    flow: Flow<Parent>,
//    viewLifecycleOwner: LifecycleOwner,
//    dataBehaviour: DataBehaviour = DataBehaviour.ADD_UPDATE()
//) {
//    setListFlow(flow.map { listOf(it) }, viewLifecycleOwner, dataBehaviour)
//}
//
//fun <Parent> BaseAdapter<Parent, *>.setListFlow(
//    flow: Flow<List<Parent>>,
//    viewLifecycleOwner: LifecycleOwner,
//    dataBehaviour: DataBehaviour = DataBehaviour.ADD_UPDATE()
//) {
//    with(viewLifecycleOwner) {
//        lifecycleScope.launch {
//            flow.flowWithLifecycle(lifecycle).collect { items ->
//                workManager.doAsync {
//                    when (dataBehaviour) {
//                        is DataBehaviour.ADD -> {
//                            add(items)
//                        }
//
//                        else -> {
//                            if (isEmpty()) {
//                                if (dataBehaviour is DataBehaviour.ADD_UPDATE) {
//                                    add(items)
//                                    return@doAsync
//                                }
//                            }
//
//                            update(UpdateRequest.fromBehaviour(items, dataBehaviour))
//                        }
//                    }
//                }
//            }
//        }
//    }
//}