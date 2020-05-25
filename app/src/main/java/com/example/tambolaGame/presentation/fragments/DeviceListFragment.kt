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
import com.example.tambolaGame.MainActivity
import com.example.tambolaGame.R
import com.example.tambolaGame.models.UserDevice

class DeviceListFragment : ListFragment() {
    private var listAdapter: DeviceListAdapter? = null

    internal interface DeviceClickListener {
        fun connect2Server(endPoint: UserDevice)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_device_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listAdapter = DeviceListAdapter(
            activity,
            android.R.layout.simple_list_item_2, android.R.id.text1,
            ArrayList()
        )
        setListAdapter(listAdapter)
    }

    @SuppressLint("SetTextI18n")
    override fun onListItemClick(
        l: ListView,
        v: View,
        position: Int,
        id: Long
    ) {
        (activity as MainActivity).connect2Server(l.getItemAtPosition(position) as UserDevice)
        (v.findViewById(android.R.id.text2) as TextView).text = "Connecting"
    }

    inner class DeviceListAdapter(
        context: Context?, resource: Int,
        textViewResourceId: Int, private val items: List<UserDevice>
    ) : ArrayAdapter<UserDevice?>(context!!, resource, textViewResourceId, items) {

        @SuppressLint("SetTextI18n")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var v: View? = convertView
            if (v == null) {
                val vi = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                v = vi.inflate(android.R.layout.simple_list_item_2, null)
            }

            val service = items[position]
            val nameText = v!!.findViewById(android.R.id.text1) as TextView?

            if (nameText != null) {
                nameText.text = service.userName + " - Tambola Game"
            }

            val statusText = v.findViewById(android.R.id.text2) as TextView
            statusText.text = "Available"
            return v
        }
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).stopDiscovery()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).startDiscovery()
    }
}