package com.shustreek.tictactoe.ui.main

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.shustreek.tictactoe.R
import com.shustreek.tictactoe.databinding.MainFragmentBinding
import com.shustreek.tictactoe.utils.observe

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var binding: MainFragmentBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_reload, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_reload) {
            viewModel.onReloadClick()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.field.run {
            for (i in 0 until childCount) {
                val row = i / 3
                val column = i % 3
                with(getChildAt(i)) {
                    setTag(R.id.row_tag, row)
                    setTag(R.id.column_tag, column)
                    setOnClickListener {
                        viewModel.onCellClick(
                            it.getTag(R.id.row_tag) as Int,
                            it.getTag(R.id.column_tag) as Int
                        )
                    }
                }
            }
        }

        observe(viewModel.currentMove) {
            binding.direction.animate()
                .rotation(if (it == Mark.Cross) 0f else 180f)
//                .scaleY(if (it == Mark.Cross) 1f else -1f)
        }

        observe(viewModel.marks) {
            val (status, matrix) = it
            binding.field.clearLine()
            matrix.forEachIndexed { index, mark ->
                with(binding.field.getChildAt(index) as ImageView) {
                    setImageResource(mark.icon)
                    isEnabled = mark.isClickable && status == GameStatus.Started
                }
            }
        }

        observe(viewModel.markByIndex) {
            with(binding.field.getChildAt(it.first) as ImageView) {
                isEnabled = it.second.isClickable
                ResourcesCompat.getDrawable(resources, it.second.icon, null)?.let { drw ->
                    setImageDrawable(drw)
                    (drw as? Animatable)?.start()
                }
            }
        }

        observe(viewModel.winState) { binding.field.drawWinLine(it) }

    }

}
