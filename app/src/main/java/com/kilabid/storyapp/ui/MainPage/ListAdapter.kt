package com.kilabid.storyapp.ui.MainPage

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kilabid.storyapp.R
import com.kilabid.storyapp.data.remote.response.ListStoryItem
import com.kilabid.storyapp.databinding.ItemLayoutBinding
import com.kilabid.storyapp.ui.DetailPage.DetailActivity

class ListAdapter(private var listStory: List<ListStoryItem>) :
    RecyclerView.Adapter<ListAdapter.ListViewHolder>() {
    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemLayoutBinding.bind(itemView)

        fun bind(story: ListStoryItem) {
            binding.tvItemName.text = story.name
            binding.tvItemDate.text = story.createdAt
            Glide.with(itemView)
                .load(story.photoUrl)
                .into(binding.ivItemPhoto)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra("EXTRA_ID", story.id)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.tvItemName, "name"),
                        Pair(binding.tvItemDate, "time"),
                        Pair(binding.ivItemPhoto, "image")
                    )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter.ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListAdapter.ListViewHolder, position: Int) {
        val story = listStory[position]
        holder.bind(story)
    }

    override fun getItemCount(): Int {
        return listStory.size
    }

    fun submitList(newList: List<ListStoryItem>) {
        val diffCallback = StoryDiffCallback(listStory, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listStory = newList
        diffResult.dispatchUpdatesTo(this)
    }
}
