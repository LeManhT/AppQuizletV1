package com.example.quizletappandroidv1.custom

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class EqualSpacingItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        outRect.left = spacing / 2
        outRect.right = spacing / 2

        if (position == 0) {
            outRect.left = spacing // Add padding to the first item
        } else if (position == itemCount - 1) {
            outRect.right = spacing // Add padding to the last item
        }
    }
}