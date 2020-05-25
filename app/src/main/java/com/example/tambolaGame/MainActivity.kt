package com.example.tambolaGame

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.forEach
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tambolaGame.models.Game
import com.example.tambolaGame.models.UserDevice
import com.example.tambolaGame.presentation.fragments.DeviceListFragment
import com.example.tambolaGame.presentation.fragments.GameFragment
import com.example.tambolaGame.presentation.fragments.ParticipantsWalletFragment
import com.example.tambolaGame.repository.Repository
import com.example.tambolaGame.utils.CountDrawable
import com.example.tambolaGame.utils.RoleEnums
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

var gameRole = RoleEnums.NONE
var isConnected = MutableLiveData<Boolean>().apply{setValue(false)}
lateinit var sharedRepository: Repository

class MainActivity : AppCompatActivity(), DeviceListFragment.DeviceClickListener {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var doubleBackToExitPressedOnce = false
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private lateinit var connectionsClient: ConnectionsClient
    private lateinit var wifiManager: WifiManager
    private lateinit var destiny: String

    private val permissions = arrayOf(
        Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedRepository = Repository.getSharedRepository(this)!!

        initView()
        connectionsClient = Nearby.getConnectionsClient(this)
    }

    private fun initView() {
        /*currentUser = sharedRepository.getUserDetails()
        navHeaderImage = nav_view.getHeaderView(0)
        navHeaderName = navHeaderImage.findViewById(R.id.userName)
        navHeaderEmail = navHeaderImage.findViewById(R.id.userMailId)
        if ((currentUser.fullName != "") && (currentUser.email != "")) {
            navHeaderName.text = "Welcome ${currentUser.fullName}"
            navHeaderEmail.text = currentUser.email
        } else {
            navHeaderName.text = "Welcome Guest"
            navHeaderEmail.visibility = View.GONE
        }*/

        //nav_view.inflateMenu(R.menu.kronecrane_settings_menu)
        navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_main
            ), drawer_layout
        )
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
        nav_view.setupWithNavController(navController)

        setDrawerEnabled(false)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            invalidateOptionsMenu()
            if (destination.id == R.id.nav_main) {
                onGameEnd()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)

        if (isConnected.value!!) {
            if (navController.currentDestination!!.id != R.id.nav_participants_wallet)
                supportActionBar?.title = navController.currentDestination!!.label.toString() + " - " + myDevice.userId.toString()
            supportActionBar?.subtitle = "Connected"
        } else {
            supportActionBar?.title = navController.currentDestination!!.label.toString()
            supportActionBar?.subtitle = "Disconnected"
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (navController.currentDestination!!.id == R.id.nav_main || navController.currentDestination!!.id == R.id.nav_add_games || navController.currentDestination!!.id == R.id.nav_device_list) {
            menu!!.forEach { item -> item.isVisible = false }
        } else {
            if (gameRole == RoleEnums.SERVER) {
                setMemberCount(this, menu!!, tambolaMembers.size.toString())
                setWalletMoney(this, menu, myDevice.walletMoney.toString())
            } else if (gameRole == RoleEnums.CLIENT) {
                menu!!.findItem(R.id.joined_members).isVisible = false
                menu.findItem(R.id.winner).isVisible = false
                setWalletMoney(this, menu, myDevice.walletMoney.toString())
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.joined_members -> {
            when (navController.currentDestination!!.id) {
                R.id.nav_games -> {
                    val bundle = Bundle()
                    bundle.putString("TYPE", "Participants")
                    navController.navigate(R.id.action_nav_games_to_nav_participants_wallet, bundle)
                }
                R.id.nav_participants_wallet -> {
                    val fragment =
                        supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.first() as ParticipantsWalletFragment

                    if (fragment.getType() == "Wallet")
                        fragment.refreshView("Participants")
                }
                R.id.nav_number_board -> {
                    val bundle = Bundle()
                    bundle.putString("TYPE", "Participants")
                    navController.navigate(R.id.action_nav_number_board_to_nav_participants_wallet, bundle)
                }
                R.id.nav_winner -> {
                    val bundle = Bundle()
                    bundle.putString("TYPE", "Participants")
                    navController.navigate(R.id.action_nav_winner_to_nav_participants_wallet, bundle)
                }
            }
            true
        }
        R.id.wallet -> {
            if (gameRole == RoleEnums.SERVER) {
                when (navController.currentDestination!!.id) {
                    R.id.nav_games -> {
                        val bundle = Bundle()
                        bundle.putString("TYPE", "Wallet")
                        navController.navigate(R.id.action_nav_games_to_nav_participants_wallet, bundle)
                    }
                    R.id.nav_participants_wallet -> {
                        val fragment =
                            supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.first() as ParticipantsWalletFragment

                        if (fragment.getType() == "Participants")
                            fragment.refreshView("Wallet")
                    }
                    R.id.nav_number_board -> {
                        val bundle = Bundle()
                        bundle.putString("TYPE", "Wallet")
                        navController.navigate(R.id.action_nav_number_board_to_nav_participants_wallet, bundle)
                    }
                    R.id.nav_winner -> {
                        val bundle = Bundle()
                        bundle.putString("TYPE", "Wallet")
                        navController.navigate(R.id.action_nav_winner_to_nav_participants_wallet, bundle)
                    }
                }
            }
            true
        }
        R.id.winner -> {
            when (navController.currentDestination!!.id) {
                R.id.nav_games -> {
                    navController.navigate(R.id.action_nav_games_to_nav_winner)
                }
                R.id.nav_participants_wallet -> {
                    navController.navigate(R.id.action_nav_participants_wallet_to_nav_winner)
                }
                R.id.nav_number_board -> {
                    navController.navigate(R.id.action_nav_number_board_to_nav_winner)
                }
            }
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun setWalletMoney(context: Context, menu: Menu, money: String) {
        val menuItem = menu.findItem(R.id.wallet)
        val icon = menuItem!!.icon as LayerDrawable

        val badge: CountDrawable
        val reuse = icon.findDrawableByLayerId(R.id.ic_wallet_count)

        badge = if (reuse != null && reuse is CountDrawable) {
            reuse
        } else {
            CountDrawable(context)
        }

        badge.setCount(money)
        icon.mutate()
        icon.setDrawableByLayerId(R.id.ic_wallet_count, badge)
    }

    private fun setMemberCount(context: Context, menu: Menu, count: String) {
        val menuItem = menu.findItem(R.id.joined_members)
        val icon = menuItem!!.icon as LayerDrawable

        val badge: CountDrawable
        val reuse = icon.findDrawableByLayerId(R.id.ic_group_count)

        badge = if (reuse != null && reuse is CountDrawable) {
            reuse
        } else {
            CountDrawable(context)
        }

        badge.setCount(count)
        icon.mutate()
        icon.setDrawableByLayerId(R.id.ic_group_count, badge)
    }

    override fun onSupportNavigateUp(): Boolean {
        val inputMethodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager!!.hideSoftInputFromWindow(this.window.decorView.windowToken, 0)
        onBackPressed()
        return true
    }

    //SERVER
    fun startAdvertising() {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(STRATEGY).build()

        isConnected.value = true
        gameRole = RoleEnums.SERVER
        myDevice = UserDevice(0, "${sharedRepository.getName()} (0)", null, sharedRepository.getPhoneNumber(), gamesTotalPrice)
        tambolaMembers.add(myDevice)
        invalidateOptionsMenu()

        connectionsClient.startAdvertising(sharedRepository.getName(), SERVICE_ID, object: ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                if (!gameStart) {
                    connectionsClient.acceptConnection(endpointId, payloadCallback)
                } else {
                    connectionsClient.rejectConnection(endpointId)
                }
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        val device = UserDevice(tambolaMembers[tambolaMembers.size - 1].userId + 1, null, endpointId, null, 0)
                        tambolaMembers.add(device)
                        sendInitialData(endpointId, device.userId)
                    }
                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                        Toast.makeText(applicationContext, "Connection rejected", Toast.LENGTH_SHORT).show()
                    }
                    ConnectionsStatusCodes.STATUS_ERROR -> {
                        Toast.makeText(applicationContext, "Connection error", Toast.LENGTH_SHORT).show()
                    }
                    else -> {

                    }
                }
            }

            override fun onDisconnected(endpointId: String) {
                if (tambolaMembers.find { it.endpointID == endpointId } != null) {
                    tambolaMembers.remove(tambolaMembers.first { p -> p.endpointID == endpointId })
                    invalidateOptionsMenu()
                }
                Toast.makeText(applicationContext, "Disconnected", Toast.LENGTH_SHORT).show()
            }

        }, advertisingOptions)
    }

    fun startDiscovery() {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(STRATEGY).build()

        myDevice = UserDevice(0 ,sharedRepository.getName(), null, sharedRepository.getPhoneNumber(), 0)

        connectionsClient.startDiscovery(SERVICE_ID, object: EndpointDiscoveryCallback() {

            override fun onEndpointFound(endPointId: String, connectionInfo: DiscoveredEndpointInfo) {

                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                val fragment = navHostFragment.childFragmentManager.fragments[0] as DeviceListFragment?

                if (fragment != null) {
                    val adapter = fragment.listAdapter as DeviceListFragment.DeviceListAdapter
                    val service = UserDevice(0, connectionInfo.endpointName, endPointId, null, 0)
                    adapter.add(service)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onEndpointLost(endPointId: String) {
                Toast.makeText(applicationContext, "Connection Lost", Toast.LENGTH_SHORT).show()
            }

        }, discoveryOptions)
    }

    override fun connect2Server(endPoint: UserDevice) {
        showProgressBar("Connecting to Game Server", "")
        connectionsClient.requestConnection(endPoint.userName!!, endPoint.endpointID!!, object: ConnectionLifecycleCallback() {

            override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                connectionsClient.acceptConnection(endpointId, payloadCallback)
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        tambolaMembers.add(endPoint)
                        gameRole = RoleEnums.CLIENT
                        isConnected.value = true
                        invalidateOptionsMenu()
                        sendData(endpointId, USER_INFO, "${myDevice.userName}`${myDevice.phone}")

                        Toast.makeText(applicationContext, "Device is connected", Toast.LENGTH_SHORT).show()
                    }
                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                        Toast.makeText(applicationContext, "Connection rejected", Toast.LENGTH_SHORT).show()
                    }
                    ConnectionsStatusCodes.STATUS_ERROR -> {
                        Toast.makeText(applicationContext, "Connection error", Toast.LENGTH_SHORT).show()
                    }
                    else -> {

                    }
                }
            }

            override fun onDisconnected(endPointId: String) {
                when (navController.currentDestination!!.id) {
                    R.id.nav_number_board -> {
                        navController.navigate(R.id.action_nav_number_board_to_nav_main)
                    }
                    R.id.nav_games -> {
                        navController.navigate(R.id.action_nav_games_to_nav_main)
                    }
                }
                Toast.makeText(applicationContext, "Disconnected", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun sendInitialData(endpointId: String, userId: Int) {
        class SendInitData: AsyncTask<Void?, Void?, Void>() {

            override fun doInBackground(vararg p0: Void?): Void? {
                sendData(endpointId, ALLOT_SEQ, userId.toString())
                sendData(endpointId, SET_GAME_SETTINGS, "$ticketPrice`$ticketSize`$members")
                for (i in tambolaGameList.indices) {
                    val game = tambolaGameList[i]
                    val gameId = game.gameId
                    val gameName = game.gameName
                    val gamePrice = game.gamePrice

                    if (i == tambolaGameList.size - 1)
                        sendData(endpointId, ADD_GAMES, "Last`$gameId`$gameName`$gamePrice")
                    else
                        sendData(endpointId, ADD_GAMES, "Ongoing`$gameId`$gameName`$gamePrice")
                }
                return null
            }
        }

        val sendInitData = SendInitData()
        sendInitData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    fun stopDiscovery() {
        connectionsClient.stopDiscovery()
    }

    fun stopAdvertising() {
        connectionsClient.stopAdvertising()
    }

    fun sendData(endpointId: String, operationCode: String, value: String) {
        val msg = Payload.fromBytes(("$operationCode~$value").toByteArray(Charsets.UTF_8))
        connectionsClient.sendPayload(endpointId, msg)
    }

    fun sendAllData(operationCode: String, value: String) {
        val msg = Payload.fromBytes(("$operationCode~$value").toByteArray(Charsets.UTF_8))
        connectionsClient.sendPayload(tambolaMembers.map { it.endpointID }, msg)
    }

    private val payloadCallback = object: PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            var messageComplete = String(payload.asBytes()!!, Charsets.UTF_8)
            val operation = messageComplete.split('~')

            if (operation[0] == USER_INFO) {
                var msg = operation[1]
                msg = "$endpointId`$msg"
                messageComplete = "${operation[0]}~$msg"
            }

            receivedMessage(messageComplete)
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {}
    }

    private fun receivedMessage(readMessage: String) {
        val splitString = readMessage.split('~')

        val currFragment = navController.currentDestination!!.id

        when (splitString[0]) {
            ALLOT_SEQ -> {
                val userId = splitString[1].toInt()

                myDevice.userId = userId
                myDevice.userName = "${myDevice.userName} ($userId)"
                invalidateOptionsMenu()
            }
            SET_GAME_SETTINGS -> {
                val values = splitString[1].split('`')

                ticketPrice = values[0].toInt()
                ticketSize = values[1].toInt()
                members = values[2].toInt()

                changeProgressBar("Getting Game List", null)
            }
            USER_INFO -> {
                val values = splitString[1].split('`')

                val endpointId = values[0]
                val name = values[1]
                val phone = values[2]

                val member = tambolaMembers.first {it.endpointID == endpointId}
                member.userName = "$name (${member.userId})"
                member.phone = phone
                invalidateOptionsMenu()
            }
            ADD_GAMES -> {
                val values = splitString[1].split('`')

                val tag = values[0]
                val gameId = values[1].toInt()
                val gameName = values[2]
                val gamePrice = values[3].toInt()

                val newGame = Game(gameId, gameName, gamePrice, null)

                tambolaGameList.add(newGame)

                if (tag == "Last") {
                    hideProgressBar()
                    navController.navigate(R.id.action_nav_device_list_to_nav_games)
                }
            }
            ADD_WINNER -> {
                val values = splitString[1].split('`')

                val position = values[0].toInt()
                val gameId = values[1].toInt()
                val gameName = values[2]
                val gamePrice = values[3].toInt()
                val winner = values[4]
                val winnerId = values[5].toInt()

                val oldList = tambolaGameList.map { it.copy() }

                if (myDevice.userId == winnerId) {
                    myDevice.walletMoney += gamePrice
                }
                invalidateOptionsMenu()

                val win = UserDevice(winnerId, winner, null, null, 0)
                if (tambolaGameList[position].gameId == gameId) {
                    tambolaGameList[position].gameWinner = win
                } else {
                    tambolaGameList.first {it.gameName == gameName}.gameWinner = win
                }

                if (currFragment == R.id.nav_games) {
                    val fragment =
                        supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.first()
                    (fragment as GameFragment).updateGamesAdapter(oldList)
                }
            }
            GEN_NEW_NUMBER -> {
                val newNumber = splitString[1]

                prevNumber = currNumber
                currNumber = newNumber
                if (newNumber != "Empty")
                    GameFragment.numberGenerator.removeNumber(currNumber.toInt())

                if (currFragment == R.id.nav_games) {
                    val fragment =
                        supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.first()
                    (fragment as GameFragment).updateNewNumber()
                }
            }
            CHANGE_WINNER -> {
                val values = splitString[1].split('`')

                val position = values[0].toInt()
                val gameId = values[1].toInt()
                val gameName = values[2]
                val gamePrice = values[3].toInt()
                val winner = values[4]
                val winnerId = values[5].toInt()
                val prevWinnerId = values[6].toInt()

                val oldList = tambolaGameList.map { it.copy() }

                if (myDevice.userId == winnerId) {
                    myDevice.walletMoney += gamePrice
                } else if (myDevice.userId == prevWinnerId) {
                    myDevice.walletMoney -= gamePrice
                }
                invalidateOptionsMenu()

                val win = UserDevice(winnerId, winner, null, null, 0)
                if (tambolaGameList[position].gameId == gameId) {
                    tambolaGameList[position].gameWinner = win
                } else {
                    tambolaGameList.first {it.gameName == gameName}.gameWinner = win
                }

                if (currFragment == R.id.nav_games) {
                    val fragment =
                        supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.first()
                    (fragment as GameFragment).updateGamesAdapter(oldList)
                }
            }
            REMOVE_WINNER -> {
                val values = splitString[1].split('`')

                val position = values[0].toInt()
                val gameId = values[1].toInt()
                val gameName = values[2]
                val gamePrice = values[3].toInt()
                val prevWinnerId = values[4].toInt()

                val oldList = tambolaGameList.map { it.copy() }

                if (myDevice.userId == prevWinnerId) {
                    myDevice.walletMoney -= gamePrice
                }
                invalidateOptionsMenu()

                if (tambolaGameList[position].gameId == gameId) {
                    tambolaGameList[position].gameWinner = null
                } else {
                    tambolaGameList.first {it.gameName == gameName}.gameWinner = null
                }

                if (currFragment == R.id.nav_games) {
                    val fragment =
                        supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.first()
                    (fragment as GameFragment).updateGamesAdapter(oldList)
                }
            }
        }
    }

    private fun openAppSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        startActivity(intent)
        finishAffinity()
    }

    private fun setDrawerEnabled(enabled: Boolean) {
        val lockMode: Int = if (enabled)
            DrawerLayout.LOCK_MODE_UNLOCKED
        else
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED

        drawer_layout.setDrawerLockMode(lockMode)
    }

    override fun onBackPressed() {
        if (!disableBack) {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer((GravityCompat.START))
            } else {
                when (navController.currentDestination!!.id) {
                    R.id.nav_games -> {
                        val alertDialog = AlertDialog.Builder(this, R.style.CustomDialogTheme).create()
                        alertDialog.setTitle(resources.getString(R.string.game_exit))
                        alertDialog.setCancelable(true)
                        alertDialog.setCanceledOnTouchOutside(false)

                        alertDialog.setButton(
                            Dialog.BUTTON_POSITIVE,
                            resources.getString(R.string.yes)
                        ) { dialog, _ ->
                            dialog.dismiss()
                            super.onBackPressed()
                        }

                        alertDialog.setButton(
                            Dialog.BUTTON_NEGATIVE,
                            resources.getString(R.string.no)
                        ) { dialog, _ ->
                            dialog.dismiss()
                        }

                        alertDialog.show()
                    }
                    R.id.nav_add_games -> {
                        val alertDialog = AlertDialog.Builder(this, R.style.CustomDialogTheme).create()
                        alertDialog.setTitle("All the changes will be lost if you leave this page.")
                        alertDialog.setCancelable(true)
                        alertDialog.setCanceledOnTouchOutside(false)

                        alertDialog.setButton(
                            Dialog.BUTTON_POSITIVE,
                            "OK"
                        ) { dialog, _ ->
                            dialog.dismiss()
                            super.onBackPressed()
                        }

                        alertDialog.setButton(
                            Dialog.BUTTON_NEGATIVE,
                            "CANCEL"
                        ) { dialog, _ ->
                            dialog.dismiss()
                        }

                        alertDialog.show()
                    }
                    R.id.nav_main -> {
                        if (doubleBackToExitPressedOnce) {
                            super.onBackPressed()
                        }

                        doubleBackToExitPressedOnce = true
                        Toast.makeText(this, resources.getString(R.string.action_exit), Toast.LENGTH_SHORT).show()
                        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
                    }
                    else -> {
                        super.onBackPressed()
                    }
                }
            }
        } else {
            return
        }
    }

    private fun showProgressBar(text: String, message: String?) {
        val loadingProgress = findViewById<ConstraintLayout>(R.id.progress_homepage)
        val loadingText = loadingProgress.findViewById<TextView>(R.id.loadingText)
        val loadingMessage = loadingProgress.findViewById<TextView>(R.id.loading_message)

        loadingText.text = text
        if (message != null) {
            loadingMessage.text = message
            loadingMessage.visibility = View.VISIBLE
        } else {
            loadingMessage.visibility = View.GONE
        }

        loadingProgress.visibility = View.VISIBLE

        disableBack = true
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun changeProgressBar(text: String, message: String?) {
        val loadingProgress = findViewById<ConstraintLayout>(R.id.progress_homepage)
        val loadingText = loadingProgress.findViewById<TextView>(R.id.loadingText)
        val loadingMessage = loadingProgress.findViewById<TextView>(R.id.loading_message)

        loadingText.text = text
        if (message != null) {
            loadingMessage.text = message
            loadingMessage.visibility = View.VISIBLE
        } else {
            loadingMessage.visibility = View.GONE
        }
    }

    private fun hideProgressBar() {
        val loadingProgress = findViewById<ConstraintLayout>(R.id.progress_homepage)

        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        disableBack = false

        loadingProgress.visibility = View.GONE
    }

    fun showActionBar() {
        container.setPadding(0, toolbar.layoutParams.height , 0, 0)
        supportActionBar!!.show()
    }

    fun hideActionBar() {
        container.setPadding(0, 0, 0, 0)
        supportActionBar!!.hide()
    }

    private fun startBluetooth() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show()
        } else {
            if (!bluetoothAdapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
        }
    }

    fun hotSpotDisabled(): Boolean {
        wifiManager = (applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager)
        val method = wifiManager.javaClass.getDeclaredMethod("getWifiApState")
        method.isAccessible = true
        val actualState = method.invoke(wifiManager)!!.toString().toInt()
        if (actualState == 11) {
            return true
        }
        return false
    }

    fun startWiFi(destiny: String) {
        this.destiny = destiny
        if (wifiManager.wifiState == WifiManager.WIFI_STATE_ENABLED || wifiManager.wifiState == WifiManager.WIFI_STATE_ENABLING) {
            if (destiny == "Server")
                navController.navigate(R.id.action_nav_main_to_nav_add_games)
            else
                navController.navigate(R.id.action_nav_main_to_nav_device_list)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
                startActivityForResult(panelIntent, REQUEST_WIFI_CODE)
            } else {
                wifiManager.isWifiEnabled = true
                if (destiny == "Server")
                    navController.navigate(R.id.action_nav_main_to_nav_add_games)
                else
                    navController.navigate(R.id.action_nav_main_to_nav_device_list)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!hasPermissions(this, permissions)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, REQUEST_CODE_REQUIRED_PERMISSIONS)
            }
        } else {
            startBluetooth()
        }
    }

    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_REQUIRED_PERMISSIONS -> {
                for (index in grantResults.indices) {
                    if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            val showRationale =
                                shouldShowRequestPermissionRationale(permissions[index])
                            if (showRationale) {
                                val builder =
                                    AlertDialog.Builder(this, R.style.CustomDialogTheme).create()
                                val title = "Location Access"
                                builder.setTitle(title)
                                val msg =
                                    "Please grant this app location permission to function properly."
                                builder.setMessage(msg)
                                builder.setButton(
                                    Dialog.BUTTON_POSITIVE,
                                    "OK"
                                ) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                builder.setOnDismissListener {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(
                                            arrayOf(permissions[index]),
                                            REQUEST_CODE_REQUIRED_PERMISSIONS
                                        )
                                    }
                                }
                                builder.setCanceledOnTouchOutside(false)
                                builder.setCancelable(false)
                                builder.show()
                            } else {
                                val builder =
                                    AlertDialog.Builder(this, R.style.CustomDialogTheme).create()
                                val title = "Necessary Permission Required"
                                builder.setTitle(title)
                                val msg = "Redirecting to Settings to grant location permission."
                                builder.setMessage(msg)
                                builder.setButton(
                                    Dialog.BUTTON_POSITIVE,
                                    "OK"
                                ) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                builder.setOnDismissListener { openAppSettings() }
                                builder.setCanceledOnTouchOutside(false)
                                builder.setCancelable(false)
                                builder.show()
                            }
                        }
                        break
                    } else {
                        if (permissions[index] == Manifest.permission.ACCESS_FINE_LOCATION) {
                            startBluetooth()
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_WIFI_CODE -> {
                if (wifiManager.wifiState == WifiManager.WIFI_STATE_DISABLED) {
                    val builder = AlertDialog.Builder(this, R.style.CustomDialogTheme).create()
                    val title = "Wifi Permission"
                    builder.setTitle(title)
                    val msg = "Wifi permission is necessary for this app to work."
                    builder.setMessage(msg)
                    builder.setButton(
                        Dialog.BUTTON_POSITIVE,
                        "OK"
                    ) { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.setOnDismissListener { finishAffinity() }
                    builder.setCanceledOnTouchOutside(false)
                    builder.setCancelable(false)
                    builder.show()
                } else if (wifiManager.wifiState == WifiManager.WIFI_STATE_ENABLED || wifiManager.wifiState == WifiManager.WIFI_STATE_ENABLING){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        requestedOrientation =
                            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    }
                    if (destiny == "Server")
                        navController.navigate(R.id.action_nav_main_to_nav_add_games)
                    else
                        navController.navigate(R.id.action_nav_main_to_nav_device_list)
                }
            }
            REQUEST_ENABLE_BT -> {
                if (resultCode != Activity.RESULT_OK) {
                    val builder = AlertDialog.Builder(this, R.style.CustomDialogTheme).create()
                    val title = "Bluetooth Permission"
                    builder.setTitle(title)
                    val msg = "Bluetooth permission is necessary for this app to work."
                    builder.setMessage(msg)
                    builder.setButton(
                        Dialog.BUTTON_POSITIVE,
                        "OK"
                    ) { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.setOnDismissListener { finishAffinity() }
                    builder.setCanceledOnTouchOutside(false)
                    builder.setCancelable(false)
                    builder.show()
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun onGameEnd() {
        if (gameRole == RoleEnums.SERVER) {
            connectionsClient.stopAllEndpoints()
            isConnected.value = false
            gameRole = RoleEnums.NONE
            invalidateOptionsMenu()
        } else if (gameRole == RoleEnums.CLIENT){
            connectionsClient.disconnectFromEndpoint(tambolaMembers[0].endpointID!!)
            isConnected.value = false
            gameRole = RoleEnums.NONE
            invalidateOptionsMenu()
        }
        tambolaGameList.clear()
        tambolaMembers.clear()
        currNumber = ""
        prevNumber = ""
        gameStart = false
        gamesTotalPrice = 0
        selectedGame = null
    }

    companion object {
        var disableBack = false
        var members = 0
        var ticketSize = 0
        var ticketPrice = 0
        var tambolaGameList = ArrayList<Game>()
        var tambolaMembers = ArrayList<UserDevice>()
        var currNumber = ""
        var prevNumber = ""
        var gameStart = false
        var gamesTotalPrice = 0
        var selectedGame: Game? = null
        var myDevice = UserDevice(-1, null, null, null, 0)

        val STRATEGY = Strategy.P2P_STAR!!
        const val SERVICE_ID = "com.example.tambolaGame.praneet"
        const val REQUEST_CODE_REQUIRED_PERMISSIONS = 1
        const val REQUEST_ENABLE_BT = 2
        const val REQUEST_WIFI_CODE = 0

        //Game Constants
        const val SET_GAME_SETTINGS = "0000"
        const val ADD_GAMES = "0001"
        const val ADD_WINNER = "0010"
        const val REMOVE_WINNER = "0011"
        const val CHANGE_WINNER = "0100"
        const val GEN_NEW_NUMBER = "0101"
        const val USER_INFO = "0110"
        const val ALLOT_SEQ = "0111"
    }
}
