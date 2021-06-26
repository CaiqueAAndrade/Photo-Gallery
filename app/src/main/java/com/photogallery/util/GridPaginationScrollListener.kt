package com.photogallery.util


import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

abstract class GridPaginationScrollListener(private val layoutManager: StaggeredGridLayoutManager) :
    RecyclerView.OnScrollListener() {

    private var pastVisibleItems = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        visibleItemCount = layoutManager.childCount
        totalItemCount = layoutManager.itemCount
        var firstVisibleItems: IntArray? = null
        firstVisibleItems =
            layoutManager.findFirstVisibleItemPositions(firstVisibleItems)
        if (firstVisibleItems != null && firstVisibleItems.isNotEmpty()) {
            pastVisibleItems = firstVisibleItems[0]
        }

        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
            loadMoreItems()
        }
    }


    protected abstract fun loadMoreItems()
}