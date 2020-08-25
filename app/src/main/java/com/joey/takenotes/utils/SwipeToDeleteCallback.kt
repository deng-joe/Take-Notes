package com.joey.takenotes.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.joey.takenotes.R

/**
 * Created by Joe on 8/24/2020.
 */
class SwipeToDeleteCallback(
    context: Context,
    dragDirs: Int,
    swipeDirs: Int,
    private val listener: NoteItemTouchHelperListener
) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    private val icon: Drawable? =
        ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete_24)
    private val background: ColorDrawable = ColorDrawable(Color.GRAY)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) =
        listener.onSwiped(viewHolder, direction, viewHolder.adapterPosition)

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val view = viewHolder.itemView
        val backgroundCornerOffset = 20 // So background is behind the itemView
        val iconMargin = (view.height - icon!!.intrinsicHeight) / 2
        val iconTop = view.top + (view.height - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight

        when {
            dX > 0 -> {   // Swiping to the right
                val iconLeft = view.left + iconMargin
                val iconRight = iconLeft + icon.intrinsicWidth
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                background.setBounds(
                    view.left,
                    view.top,
                    view.left + dX.toInt() + backgroundCornerOffset,
                    view.bottom
                )
            }
            dX < 0 -> {    // Swiping to the left
                val iconLeft = view.right - iconMargin - icon.intrinsicWidth
                val iconRight = view.right - iconMargin
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                background.setBounds(
                    view.right + dX.toInt() - backgroundCornerOffset,
                    view.top,
                    view.right,
                    view.bottom
                )
            }
            else -> {    // View not swiped
                background.setBounds(0, 0, 0, 0)
            }
        }

        background.draw(canvas)
        icon.draw(canvas)
    }

    interface NoteItemTouchHelperListener {
        fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int)
    }
}
