package com.shustreek.tictactoe.ui.main

import androidx.annotation.DrawableRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shustreek.otustodo.utils.SingleLiveEvent
import com.shustreek.tictactoe.R
import com.shustreek.tictactoe.views.WinLineState

class MainViewModel : ViewModel() {

    private lateinit var matrix: Array<Mark>
    private lateinit var currentMark: Mark
    private lateinit var mGameStatus: GameStatus

    private val mMarks = MutableLiveData<Pair<GameStatus, Array<Mark>>>()
    private val mCurrentMove = MutableLiveData<Mark>()
    private val mMarkByIndex: MutableLiveData<Pair<Int, Mark>> = SingleLiveEvent()
    private val mWinState = MutableLiveData<WinLineState>()

    val marks: LiveData<Pair<GameStatus, Array<Mark>>> = mMarks
    val currentMove: LiveData<Mark> = mCurrentMove
    val markByIndex: LiveData<Pair<Int, Mark>> = mMarkByIndex
    val winState: LiveData<WinLineState> = mWinState

    init {
        initGame()
    }

    private fun initGame() {
        matrix = Array<Mark>(9) { Mark.None }
        currentMark = Mark.Cross
        mGameStatus = GameStatus.Started
        mWinState.value = WinLineState.None
        mMarks.value = Pair(mGameStatus, matrix)
        mCurrentMove.value = currentMark
    }

    fun onCellClick(row: Int, column: Int) {
        val index = getIndex(row, column)
        matrix[index] = currentMark
        mMarkByIndex.value = Pair(index, currentMark)

        val state = checkWin(row, column)
        if (state != WinLineState.None) {
            mGameStatus = GameStatus.Finished
            mMarks.value = Pair(mGameStatus, matrix)
            mWinState.value = state
            return
        }

        currentMark = if (currentMark == Mark.Cross) Mark.Circle else Mark.Cross
        mCurrentMove.value = currentMark
    }

    private fun checkWin(row: Int, column: Int): WinLineState {
        //check row
        if (checkLine { matrix[getIndex(row, it)] == currentMark }) return WinLineState.Horizontal(row)
        // check column
        if (checkLine { matrix[getIndex(it, column)] == currentMark }) return WinLineState.Vertical(column)
        if (row == column) {
            // check main diagonal
            if (checkLine { matrix[getIndex(it, it)] == currentMark }) return WinLineState.MainDiagonal
        }
        if (row + column == 2) {
            // check reverse diagonal
            if (checkLine { matrix[getIndex(it, 2 - it)] == currentMark }) return WinLineState.ReverseDiagonal
        }
        return WinLineState.None
    }

    private fun checkLine(function: (Int) -> Boolean): Boolean {
        for (i in 0..2) {
            if (!function(i)) return false
        }
        return true
    }

    fun onReloadClick() {
        initGame()
    }

    private fun getIndex(row: Int, column: Int) = row * 3 + column
}

enum class Mark(@DrawableRes val icon: Int, val isClickable: Boolean) {
    None(0, true),
    Cross(R.drawable.ic_cross_anim, false),
    Circle(R.drawable.ic_circle_anim, false)
}

enum class GameStatus {
    Started,
    Finished
}
