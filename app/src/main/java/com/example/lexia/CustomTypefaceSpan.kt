package com.example.lexia

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan

class CustomTypefaceSpan(private val typeface: Typeface?) : MetricAffectingSpan() {

    override fun updateDrawState(ds: TextPaint) {
        applyTypeface(ds)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyTypeface(paint)
    }

    private fun applyTypeface(paint: TextPaint) {
        paint.typeface = typeface
    }
}