package com.aplikasi.mystory.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aplikasi.mystory.R
import com.aplikasi.mystory.data.remote.response.ListStoryItem
import com.aplikasi.mystory.databinding.StoryItemBinding
import com.aplikasi.mystory.util.withDateFormat
import com.bumptech.glide.Glide

class StoryAdapter : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    private val stories = ArrayList<ListStoryItem>()
    private var onItemClickCallback: ((ListStoryItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = stories[position]
        holder.storyData(story)
    }

    override fun getItemCount(): Int = stories.size

    inner class ViewHolder(private val binding: StoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun storyData(item: ListStoryItem) {
            binding.apply {
                root.setOnClickListener {
                    onItemClickCallback?.invoke(item)
                }
                Glide.with(itemView.context)
                    .load(item.photoUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(imageStory)
                userName.text = item.name
                descStory.text = item.description
                dateStory.text = item.createdAt?.withDateFormat()
            }
        }
    }

    fun setStories(newStories: List<ListStoryItem>) {
        val diffCallback = StoryDiffCallback(stories, newStories)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        stories.clear()
        stories.addAll(newStories)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setOnItemCallback(callback: (ListStoryItem) -> Unit) {
        this.onItemClickCallback = callback
    }

    companion object {
        class StoryDiffCallback(
            private val oldList: List<ListStoryItem>,
            private val newList: List<ListStoryItem>
        ) : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldList.size
            override fun getNewListSize(): Int = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition].id == newList[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }
        }
    }
}
