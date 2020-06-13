package com.example.tambolaGame.presentation.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.tambolaGame.MainActivity
import com.example.tambolaGame.R
import com.example.tambolaGame.sharedRepository
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*


class MainFragment: Fragment(){

    private lateinit var root: View
    private lateinit var priceTextWatcher: TextWatcher
    private lateinit var membersTextWatcher: TextWatcher
    private lateinit var sizeTextWatcher: TextWatcher
    private lateinit var nameTextWatcher: TextWatcher
    private lateinit var phoneTextWatcher: TextWatcher

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_main, container, false)

        nameTextWatcher = object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(text: Editable?) {
                if (root.rdo_group.checkedRadioButtonId == R.id.rdo_server) {
                    if (text!!.isNotBlank() && root.edit_phone.text.isNotBlank()) {
                        if (root.edit_price_ticket.text.isNotBlank() && root.edit_members.text.isNotBlank() && root.edit_price_ticket.text.toString().toInt() != 0 && root.edit_members.text.toString().toInt() != 0) {
                            button.isEnabled = root.edit_ticket_size.text.toString().toInt() in 1..3 && root.edit_ticket_size.text.isNotBlank()
                            label_amount.text = "Amount to be collected : " + (root.edit_members.text.toString().toInt() * root.edit_price_ticket.text.toString().toInt()).toString()
                        } else {
                            label_amount.text = "Amount to be collected : 0"
                            button.isEnabled = false
                        }

                    } else {
                        button.isEnabled = false
                    }
                } else {
                    button.isEnabled = text!!.isNotBlank() && root.edit_phone.text.isNotBlank()
                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        }

        phoneTextWatcher = object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(text: Editable?) {
                if (root.rdo_group.checkedRadioButtonId == R.id.rdo_server) {
                    if (text!!.isNotBlank() && root.edit_name.text.isNotBlank()) {
                        if (root.edit_price_ticket.text.isNotBlank() && root.edit_members.text.isNotBlank() && root.edit_price_ticket.text.toString().toInt() != 0 && root.edit_members.text.toString().toInt() != 0) {
                            button.isEnabled = root.edit_ticket_size.text.toString().toInt() in 1..3 && root.edit_ticket_size.text.isNotBlank()
                            label_amount.text = "Amount to be collected : " + (root.edit_members.text.toString().toInt() * root.edit_price_ticket.text.toString().toInt()).toString()
                        } else {
                            label_amount.text = "Amount to be collected : 0"
                            button.isEnabled = false
                        }

                    } else {
                        button.isEnabled = false
                    }
                } else {
                    button.isEnabled = text!!.isNotBlank() && root.edit_name.text.isNotBlank()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        }

        priceTextWatcher = object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(text: Editable?) {
                if (root.edit_members.text.isNotBlank() && text!!.isNotBlank() && (text.toString().toInt() != 0) && (root.edit_members.text.toString().toInt() != 0)) {
                    label_amount.text = "Amount to be collected : " + (root.edit_members.text.toString().toInt() * text.toString().toInt()).toString()
                    button.isEnabled = root.edit_ticket_size.text.isNotBlank() && (root.edit_ticket_size.text.toString().toInt() in 1..3) && root.edit_name.text.isNotBlank() && root.edit_phone.text.isNotBlank()
                } else {
                    label_amount.text = "Amount to be collected : 0"
                    button.isEnabled = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        }

        membersTextWatcher = object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(text: Editable?) {
                if (root.edit_price_ticket.text.isNotBlank() && text!!.isNotBlank() && (text.toString().toInt() != 0) && (root.edit_price_ticket.text.toString().toInt() != 0)) {
                    label_amount.text = "Amount to be collected : " + (root.edit_price_ticket.text.toString().toInt() * text.toString().toInt()).toString()
                    button.isEnabled = root.edit_ticket_size.text.isNotBlank() && (root.edit_ticket_size.text.toString().toInt() in 1..3) && root.edit_name.text.isNotBlank() && root.edit_phone.text.isNotBlank()
                } else {
                    label_amount.text = "Amount to be collected : 0"
                    button.isEnabled = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        }

        sizeTextWatcher = object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(text: Editable?) {
                if (text!!.isNotBlank() && text.toString().toInt() in 1..3) {
                    if (root.edit_price_ticket.text.isNotBlank() && root.edit_members.text.isNotBlank() && root.edit_price_ticket.text.toString().toInt() != 0 && root.edit_members.text.toString().toInt() != 0) {
                        label_amount.text = "Amount to be collected : " + (root.edit_members.text.toString().toInt() * root.edit_price_ticket.text.toString().toInt()).toString()
                        button.isEnabled = root.edit_name.text.isNotBlank() && root.edit_phone.text.isNotBlank()
                    } else {
                        label_amount.text = "Amount to be collected : 0"
                        button.isEnabled = false
                    }
                } else {
                    button.isEnabled = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        }

        root.edit_name.setText(sharedRepository.getName())
        root.edit_phone.setText(sharedRepository.getPhoneNumber())

        if (root.rdo_group.checkedRadioButtonId == R.id.rdo_client) {
            if (root.edit_phone.text.trim().isNotBlank() && root.edit_name.text.trim().isNotBlank()) {
                root.button.isEnabled = true
            }
        }

        root.edit_name.addTextChangedListener(nameTextWatcher)
        root.edit_phone.addTextChangedListener(phoneTextWatcher)

        root.rdo_group.setOnCheckedChangeListener {_, radioBtnId ->

            if (radioBtnId == R.id.rdo_server) {
                label_price_ticket.visibility = View.VISIBLE
                edit_price_ticket.visibility = View.VISIBLE
                label_ticket_size.visibility = View.VISIBLE
                edit_ticket_size.visibility = View.VISIBLE
                label_size_range.visibility = View.VISIBLE
                label_members.visibility = View.VISIBLE
                edit_members.visibility = View.VISIBLE
                label_amount.visibility = View.VISIBLE

                edit_price_ticket.text.clear()
                edit_ticket_size.text.clear()
                edit_members.text.clear()

                root.label_amount.text = "Amount to be collected : 0"
                root.edit_price_ticket.addTextChangedListener (priceTextWatcher)
                root.edit_members.addTextChangedListener (membersTextWatcher)
                root.edit_ticket_size.addTextChangedListener (sizeTextWatcher)

                root.button.isEnabled = false
            } else {
                label_price_ticket.visibility = View.GONE
                edit_price_ticket.visibility = View.GONE
                label_ticket_size.visibility = View.GONE
                edit_ticket_size.visibility = View.GONE
                label_size_range.visibility = View.GONE
                label_members.visibility = View.GONE
                edit_members.visibility = View.GONE
                label_amount.visibility = View.GONE

                if (root.edit_phone.text.trim().isNotBlank() && root.edit_name.text.trim().isNotBlank()) {
                    root.button.isEnabled = true
                }

                root.edit_price_ticket.removeTextChangedListener (priceTextWatcher)
                root.edit_members.removeTextChangedListener (membersTextWatcher)
                root.edit_ticket_size.removeTextChangedListener (sizeTextWatcher)
            }
        }

        root.button.setOnClickListener {
            if ((activity as MainActivity).hotSpotDisabled()) {
                if (root.rdo_group.checkedRadioButtonId == R.id.rdo_server) {
                    MainActivity.ticketSize = root.edit_ticket_size.text.toString().trim().toInt()
                    MainActivity.ticketPrice = root.edit_price_ticket.text.toString().trim().toInt()
                    MainActivity.members = root.edit_members.text.toString().trim().toInt()

                    if (root.edit_phone.text.length == 10) {
                        sharedRepository.setName(root.edit_name.text.toString())
                        sharedRepository.setPhoneNumber(root.edit_phone.text.toString())
                        (activity as MainActivity).startWiFi("Server")
                    } else {
                        root.edit_phone.error = "Ph. Number should be of length 10"
                    }
                } else {
                    if (root.edit_phone.text.length == 10) {
                        sharedRepository.setName(root.edit_name.text.toString())
                        sharedRepository.setPhoneNumber(root.edit_phone.text.toString())
                        (activity as MainActivity).startWiFi("Client")
                    } else {
                        root.edit_phone.error = "Ph. Number should be of length 10"
                    }

                }
            } else {
                val builder = AlertDialog.Builder(context!!, R.style.CustomDialogTheme).create()
                val title = "Wifi Permission"
                builder.setTitle(title)
                val msg = "DISABLE Mobile Hotspot and ENABLE WiFi to play."
                builder.setMessage(msg)
                builder.setButton(
                    Dialog.BUTTON_POSITIVE,
                    "OK"
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                builder.setCanceledOnTouchOutside(false)
                builder.setCancelable(false)
                builder.show()
            }
        }

        return root
    }

}