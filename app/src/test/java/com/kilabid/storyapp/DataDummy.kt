package com.kilabid.storyapp

import com.kilabid.storyapp.data.remote.response.ListStoryItem

object DataDummy {
    fun generateDummyStory(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "create $i",
                "name $i",
                "description $i",
                0.0,
                "id $i",
                0.0
            )
            items.add(story)
        }
        return items
    }
}