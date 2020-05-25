package com.example.tambolaGame.utils

import android.os.Bundle
import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import com.example.tambolaGame.models.Game

class GamesListDiffUtil(
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
        val oldItemWinner = oldList[oldItemPosition].gameWinner
        val newItemWinner = newList[newItemPosition].gameWinner

        val diff = Bundle()
        if (oldItemWinner != newItemWinner)
            if (newItemWinner != null)
                diff.putString("winner", newItemWinner.userName)
            else
                diff.putString("winner", null)

        return diff
        //return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}