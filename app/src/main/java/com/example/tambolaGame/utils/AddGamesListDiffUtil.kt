package com.example.tambolaGame.utils

import android.os.Bundle
import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import com.example.tambolaGame.models.Game

class AddGamesListDiffUtil(
    private var oldList: List<Game>,
    private var newList: List<Game>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItemPosition == newItemPosition
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        //you can return particular field for changed item.
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        val diff = Bundle()
        if (oldItem != newItem) {
            diff.putInt("gameId", newItem.gameId)
            diff.putString("gameName", newItem.gameName)
            diff.putInt("price", newItem.gamePrice)
        }
        return diff
        //return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}