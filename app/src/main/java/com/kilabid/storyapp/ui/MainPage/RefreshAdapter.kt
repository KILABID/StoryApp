package com.kilabid.storyapp.ui.MainPage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kilabid.storyapp.databinding.RefreshItemBinding

class RefreshAdapter(private val retry: () -> Unit): LoadStateAdapter<RefreshAdapter.LoadViewHolder>() {
    class LoadViewHolder(private val binding: RefreshItemBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.errorMessage.text = loadState.error.localizedMessage
            }
            binding.errorMessage.isVisible = loadState is LoadState.Error
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.retryButton.isVisible = loadState is LoadState.Error
        }
    }

    override fun onBindViewHolder(holder: LoadViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadViewHolder {
        val binding = RefreshItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadViewHolder(binding, retry)
    }
}