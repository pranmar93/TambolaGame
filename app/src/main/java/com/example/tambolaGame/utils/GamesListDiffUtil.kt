package com.example.tambolaGame.utils

import android.os.Bundle
import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import com.example.tambolaGame.models.Game
import com.example.tambolaGame.models.UserDevice
import com.google.gson.Gson


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
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return ((oldList[oldItemPosition].gameId == newList[newItemPosition].gameId) && (oldList[oldItemPosition].gameName == newList[newItemPosition].gameName))
    }

    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        //you can return particular field for changed item.
        val oldItemWinner = oldList[oldItemPosition].gameWinner
        val newItemWinner = newList[newItemPosition].gameWinner

        val diff = Bundle()
        if (!equalLists(oldItemWinner, newItemWinner)) {
            if (newItemWinner != null) {
                val winnerString = Gson().toJson(newItemWinner)
                diff.putString("winners", winnerString)
            } else
                diff.putString("winners", null)
        }

        return diff
    }

    private fun equalLists(
        one: List<UserDevice>?,
        two: List<UserDevice>?
    ): Boolean {

        if (one == null && two == null)
            return true

        if (one == null && two != null || one != null && two == null || one!!.size != two!!.size)
            return false

        return one.containsAll(two) && two.containsAll(one)
    }
}