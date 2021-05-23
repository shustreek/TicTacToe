package com.shustreek.tictactoe.ui.main

import androidx.annotation.DrawableRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shustreek.tictactoe.utils.SingleLiveEvent
import com.shustreek.tictactoe.R
import com.shustreek.tictactoe.views.WinLineState

class MainViewModel : ViewModel() {

    private lateinit var matrix: Array<CellState>
    private lateinit var currentCellState: CellState

    private val mStates = MutableLiveData<Pair<GameStatus, Array<CellState>>>()
    private val mCurrentMove = MutableLiveData<CellState>()
    private val mCellStateByIndex: MutableLiveData<Pair<Int, CellState>> = SingleLiveEvent()
    private val mWinState : MutableLiveData<WinLineState> = MutableLiveData()

    val states: LiveData<Pair<GameStatus, Array<CellState>>> = mStates
    val currentMove: LiveData<CellState> = mCurrentMove
    val cellStateByIndex: LiveData<Pair<Int, CellState>> = mCellStateByIndex
    val winState: LiveData<WinLineState> = mWinState

    init {
        initGame()
    }

    private fun initGame() {
        matrix = Array<CellState>(9) { CellState.None }
        currentCellState = CellState.Cross
        mWinState.value = WinLineState.None
        mStates.value = Pair(GameStatus.Started, matrix)
        mCurrentMove.value = currentCellState
    }

    fun onCellClick(index: Int) {
        val row = index / 3
        val column = index % 3
        matrix[index] = currentCellState
        mCellStateByIndex.value = Pair(index, currentCellState)

        val state = checkWin(row, column)
        if (state != WinLineState.None) {
            mStates.value = Pair(GameStatus.Finished, matrix)
            mWinState.value = state
            return
        }

        currentCellState = if (currentCellState == CellState.Cross) CellState.Circle else CellState.Cross
        mCurrentMove.value = currentCellState
    }

    private fun checkWin(row: Int, column: Int): WinLineState {
        //check row
        if (checkLine { matrix[getIndex(row, it)] == currentCellState }) return WinLineState.Horizontal(row)
        // check column
        if (checkLine { matrix[getIndex(it, column)] == currentCellState }) return WinLineState.Vertical(column)
        if (row == column) {
            // check main diagonal
            if (checkLine { matrix[getIndex(it, it)] == currentCellState }) return WinLineState.MainDiagonal
        }
        if (row + column == 2) {
            // check reverse diagonal
            if (checkLine { matrix[getIndex(it, 2 - it)] == currentCellState }) return WinLineState.ReverseDiagonal
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

enum class CellState(@DrawableRes val icon: Int, val isClickable: Boolean) {
    None(0, true),
    Cross(R.drawable.ic_cross_anim, false),
    Circle(R.drawable.ic_circle_anim, false)
}

enum class GameStatus {
    Started,
    Finished
}
