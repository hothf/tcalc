package de.ka.jamit.tcalc.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import de.ka.jamit.tcalc.R

/**
 * Represents an empty view layout
 *
 * Created by Thomas Hofmann on 07.07.20
 **/
class EmptyViewLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var infoText: TextView
    private var infoImage: ImageView

    init {
        inflate(context, R.layout.layout_emptyview, this)

        infoText = findViewById(R.id.emptyText)
        infoImage = findViewById(R.id.emptyImage)
    }

    /**
     * Sets the info text of this empty view.
     */
    fun setInfoText(text: String) {
        infoText.text = text
    }

    /**
     * Sets the image resource id of the info image.
     */
    fun setInfoImageResourceId(@DrawableRes id: Int) {
        infoImage.setImageResource(id)
    }

    /**
     * Shows or hides this layout.
     */
    fun show(show: Boolean) {
        this.visibility = if (show) View.VISIBLE else View.GONE
    }
}