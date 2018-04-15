package com.jguerrerope.tvchallenge.ui.animator

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.view.View

/**
 * Animator utility shortcut to animate background color endlessly of given views
 *
 * @param context application context
 * @param fromColor initial color
 * @param toColor final color
 */
class BackgroundColorAnimator(context: Context, @ColorRes fromColor: Int, @ColorRes toColor: Int) {
    private val views = mutableListOf<View>()

    private val animator = ValueAnimator.ofInt(
            ContextCompat.getColor(context, fromColor),
            ContextCompat.getColor(context, toColor))
            .apply {
                setEvaluator(ArgbEvaluator())
                addUpdateListener {
                    val color = it.animatedValue as Int
                    views.forEach { it.setBackgroundColor(color) }
                }
                duration = 1000L
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
            }

    fun start()  = animator.start()

    fun end()  = animator.end()

    fun addViews(vararg views: View) {
        this.views.addAll(views)
    }

    fun removeViews(vararg views: View) {
        this.views.removeAll(views)
    }
}