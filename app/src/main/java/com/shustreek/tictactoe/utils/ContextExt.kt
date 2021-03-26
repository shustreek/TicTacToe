package com.shustreek.tictactoe.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

inline fun <T> Fragment.observe(data: LiveData<T>, crossinline callback: (T) -> Unit) {
    data.observe(viewLifecycleOwner, Observer { event -> event?.let { callback(it) } })
}
