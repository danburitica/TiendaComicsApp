package com.tiendacomics20.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class TCEditText(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {
    init {
        applyFont()
    }

    private fun applyFont() {
        val typeface: Typeface =
            Typeface.createFromAsset(context.assets, "Dosis-Regular.ttf")
        setTypeface(typeface)
    }
}