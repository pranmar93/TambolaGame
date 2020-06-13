package com.example.tambolaGame.presentation.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tambolaGame.MainActivity
import com.example.tambolaGame.R
import com.example.tambolaGame.utils.ScreenshotUtils
import kotlinx.android.synthetic.main.fragment_image.view.*
import java.io.File

class ImageFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_image, container, false)

        val fileName = arguments!!.getString("fileName")
        arguments!!.remove("fileName")

        val dir = ScreenshotUtils().getMainDirectoryName(context!!)

        val imageFile = File(dir!!.absolutePath, fileName!!)

        root.image_view.setImageBitmap(BitmapFactory.decodeFile(imageFile.absolutePath))

        (activity as MainActivity).hideProgressBar()

        return root
    }
}