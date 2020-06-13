package com.example.tambolaGame.presentation.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.ListFragment
import androidx.navigation.fragment.findNavController
import com.example.tambolaGame.MainActivity
import com.example.tambolaGame.R
import com.example.tambolaGame.utils.ScreenshotUtils


class ImageViewerFragment: ListFragment() {

    private var root: View? = null
    private var imageAdapter: ImageAdapter? = null
    private var fileList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_image_viewer, container, false)
            val dir = ScreenshotUtils().getMainDirectoryName(context!!)

            val files = dir!!.listFiles()

            for (file in files!!) {
                fileList.add(file.name)
            }

            imageAdapter = ImageAdapter(
                activity,
                android.R.layout.simple_list_item_2, android.R.id.text1,
                fileList
            )
            listAdapter = imageAdapter
        }

        return root!!
    }

    @SuppressLint("SetTextI18n")
    override fun onListItemClick(
        l: ListView,
        v: View,
        position: Int,
        id: Long
    ) {
        (activity as MainActivity).showProgressBar("Loading Image", null)
        val bundle = Bundle()
        bundle.putString("fileName", l.getItemAtPosition(position) as String)
        findNavController().navigate(R.id.action_nav_image_viewer_to_nav_image, bundle)
    }

    inner class ImageAdapter(
        context: Context?, resource: Int,
        textViewResourceId: Int, private val items: List<String>
    ) : ArrayAdapter<String?>(context!!, resource, textViewResourceId, items) {

        @SuppressLint("SetTextI18n")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var v: View? = convertView
            if (v == null) {
                val vi = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                v = vi.inflate(android.R.layout.simple_list_item_2, null)
            }

            val fileName = items[position]
            val nameText = v!!.findViewById(android.R.id.text1) as TextView?

            if (nameText != null) {
                nameText.text = fileName
            }

            return v
        }
    }
}