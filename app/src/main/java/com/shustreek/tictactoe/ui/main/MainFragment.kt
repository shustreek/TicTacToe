package com.shustreek.tictactoe.ui.main

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.forEachIndexed
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.shustreek.tictactoe.R
import com.shustreek.tictactoe.databinding.MainFragmentBinding

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

        binding.field.forEachIndexed { index, v ->
            v.setOnClickListener { viewModel.onCellClick(index) }
        }

        viewModel.currentMove.observe(viewLifecycleOwner) {
            binding.direction.animate()
                .rotation(if (it == CellState.Cross) 0f else 180f)
//                .scaleY(if (it == Mark.Cross) 1f else -1f)
        }

        viewModel.states.observe(viewLifecycleOwner) {
            val (status, matrix) = it
            matrix.forEachIndexed { index, state ->
                with(binding.field.getChildAt(index) as ImageView) {
                    setImageResource(state.icon)
                    isEnabled = state.isClickable && status == GameStatus.Started
                }
            }
        }

        viewModel.cellStateByIndex.observe(viewLifecycleOwner) {
            val (index, state) = it
            with(binding.field.getChildAt(index) as ImageView) {
                isEnabled = state.isClickable
                ResourcesCompat.getDrawable(resources, state.icon, null)?.let { drw ->
                    setImageDrawable(drw)
                    (drw as? Animatable)?.start()
                }
            }
        }

        viewModel.winState.observe(viewLifecycleOwner) { binding.field.drawWinLine(it) }

    }

}
