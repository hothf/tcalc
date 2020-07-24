package de.ka.jamit.tcalc.utils

import android.graphics.Rect
import android.view.View
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import de.ka.jamit.tcalc.R

/**
 * A item decoration for consensuses.
 *
 * Created by Thomas Hofmann on 12.07.20
 */
class DecorationUtil(
        private val spacingTop: Int,
        private val spacingLeftAndRight: Int = 0,
        private val columnCount: Int = 0
) :
        RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        try {
            val itemPosition = parent.getChildViewHolder(view).adapterPosition
            val child = view.findViewById<RelativeLayout>(R.id.item) ?: return
            val layoutParams = child.layoutParams as? RelativeLayout.LayoutParams

            // no position, leave it alone
            if (itemPosition == RecyclerView.NO_POSITION || layoutParams == null) {
                return
            }

            if (columnCount > 1) {
                when {
                    itemPosition == 0 -> {
                        layoutParams.topMargin = spacingTop
                        layoutParams.leftMargin = spacingLeftAndRight
                        layoutParams.rightMargin = spacingLeftAndRight / 2
                        layoutParams.bottomMargin = spacingTop / 2
                    }
                    itemPosition == 1 -> {
                        layoutParams.topMargin = spacingTop
                        layoutParams.leftMargin = spacingLeftAndRight / 2
                        layoutParams.rightMargin = spacingLeftAndRight
                        layoutParams.bottomMargin = spacingTop / 2
                    }
                    itemPosition % 2 == 0 -> {
                        layoutParams.topMargin = spacingTop / 2
                        layoutParams.leftMargin = spacingLeftAndRight
                        layoutParams.rightMargin = spacingLeftAndRight / 2
                        layoutParams.bottomMargin = spacingTop / 2
                    }
                    else -> {
                        layoutParams.topMargin = spacingTop / 2
                        layoutParams.leftMargin = spacingLeftAndRight / 2
                        layoutParams.rightMargin = spacingLeftAndRight
                        layoutParams.bottomMargin = spacingTop / 2
                    }
                }
            } else {
                if (itemPosition == 0) {
                    layoutParams.topMargin = spacingTop
                    layoutParams.leftMargin = spacingLeftAndRight
                    layoutParams.rightMargin = spacingLeftAndRight
                    layoutParams.bottomMargin = spacingTop / 2
                } else {
                    layoutParams.topMargin = spacingTop / 2
                    layoutParams.leftMargin = spacingLeftAndRight
                    layoutParams.rightMargin = spacingLeftAndRight
                    layoutParams.bottomMargin = spacingTop / 2
                }
            }

            child.layoutParams = layoutParams
        } catch (e: Exception) {
            return
        }
    }
}