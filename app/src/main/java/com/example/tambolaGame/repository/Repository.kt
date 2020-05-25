package com.example.tambolaGame.repository

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask

class Repository {

    fun getName(): String {
        return sharedPref?.getString("UserName", "")!!
    }

    fun setName(name: String) {
        class SetUserName : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg voids: Void): Void? {
                spEditor?.putString("UserName", name)
                spEditor?.apply()
                return null
            }
        }

        val setUserName = SetUserName()
        setUserName.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    fun getPhoneNumber(): String {
        return sharedPref?.getString("PhoneNumber", "")!!
    }

    fun setPhoneNumber(phone: String) {
        class SetPhoneNumber : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg voids: Void): Void? {
                spEditor?.putString("PhoneNumber", phone)
                spEditor?.apply()
                return null
            }
        }

        val setPhoneNumber = SetPhoneNumber()
        setPhoneNumber.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    companion object {

        @Volatile
        private var sharedRepository: Repository? = null
        private var sharedPref: SharedPreferences? = null
        private var spEditor: SharedPreferences.Editor? = null

        fun getSharedRepository(activity: Activity): Repository? {
            if (sharedRepository == null) {
                synchronized(Repository::class.java) {
                    if (sharedRepository == null) {
                        sharedRepository = Repository()
                        sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
                        spEditor = sharedPref!!.edit()
                    }
                }
            }
            return sharedRepository
        }
    }
}