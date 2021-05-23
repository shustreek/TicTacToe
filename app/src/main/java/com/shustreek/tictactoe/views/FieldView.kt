package com.shustreek.tictactoe.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.doOnNextLayout
import androidx.gridlayout.widget.GridLayout
import com.shustreek.tictactoe.R
import java.io.Serializable

class FieldView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {

    private var mWinLineState: WinLineState = WinLineState.None
    private var widthF: Float = 0f
    private var heightF: Float = 0f
    private var cellWidth: Float = 0f
        set(value) {
            field = value
            halfCellWidth = value / 2
        }

    private var cellHeight: Float = 0f
        set(value) {
            field = value
            halfCellHeight = value / 2
        }
    private var halfCellWidth: Float = 0f

    private var halfCellHeight: Float = 0f
    private val strokeSize = resources.getDimension(R.dimen.stroke_size)

    private var winLineRect: RectF? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ResourcesCompat.getColor(context.resources, R.color.penColor, null)
        strokeWidth = strokeSize
        strokeCap = Paint.Cap.ROUND
    }

    private var currentValue: Float = 0f
    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 500
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener {
            currentValue = it.animatedValue as Float
            invalidate()
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        return bundleOf(
            "base" to super.onSaveInstanceState(),
            "state" to mWinLineState
        )
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val bundle = state as Bundle
        super.onRestoreInstanceState(bundle.getSerializable("base") as Parcelable?)
        mWinLineState = bundle.getSerializable("state") as WinLineState
        doOnNextLayout { drawWinLine(mWinLineState, false) }
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        widthF = MeasureSpec.getSize(widthSpec).toFloat()
        heightF = MeasureSpec.getSize(heightSpec).toFloat()

        cellWidth = widthF / 3f
        cellHeight = heightF / 3f
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        canvas?.run {
            for (i in 1..2) {
                drawLine(cellWidth * i, strokeSize, cellWidth * i, heightF - strokeSize, paint)
                drawLine(strokeSize, cellHeight * i, widthF - strokeSize, cellHeight * i, paint)
            }

            winLineRect?.let {
                val line: RectF = when (mWinLineState) {
                    is WinLineState.Horizontal ->
                        RectF(it.left, it.top, it.right * currentValue, it.bottom)
                    is WinLineState.Vertical ->
                        RectF(it.left, it.top, it.right, it.bottom * currentValue)
                    WinLineState.MainDiagonal ->
                        RectF(it.left, it.top, it.right * currentValue, it.bottom * currentValue)
                    WinLineState.ReverseDiagonal ->
                        RectF(it.left, it.top, it.right + it.left * (1 - currentValue), it.bottom * currentValue)
                    WinLineState.None -> it
                }
                drawLine(line.left, line.top, line.right, line.bottom, paint)
            }

        }
    }

    fun drawWinLine(winLineState: WinLineState, animated: Boolean = true) {
        mWinLineState = winLineState
        winLineRect = when (winLineState) {
            WinLineState.MainDiagonal ->
                RectF(strokeSize, strokeSize, widthF - strokeSize, heightF - strokeSize)
            WinLineState.ReverseDiagonal ->
                RectF(widthF - strokeSize, strokeSize, strokeSize, heightF - strokeSize)
            is WinLineState.Horizontal -> {
                val y = halfCellHeight + cellHeight * winLineState.row
                RectF(strokeSize, y, widthF - strokeSize, y)
            }
            is WinLineState.Vertical -> {
                val x = halfCellWidth + cellWidth * winLineState.column
                RectF(x, strokeSize, x, heightF - strokeSize)
            }
            WinLineState.None -> null
        }
        if (animated) animator.start() else animator.end()
    }

}

sealed class WinLineState : Serializable {
    data class Horizontal(val row: Int) : WinLineState()
    data class Vertical(val column: Int) : WinLineState()
    object MainDiagonal : WinLineState()
    object ReverseDiagonal : WinLineState()
    object None : WinLineState()
}
