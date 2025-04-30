package com.dangiashish.bluetoothscanner

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dangiashish.bluetoothscanner.databinding.LayoutBluetoothDevicesListBinding
import com.dangiashish.bluetoothscanner.utils.UIUtils
import java.util.Objects
import java.util.concurrent.TimeUnit
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt

class BluetoothScanner : DialogFragment() {
    val GPS_ACTION = "android.location.PROVIDERS_CHANGED"
    private val bind: LayoutBluetoothDevicesListBinding by lazy { LayoutBluetoothDevicesListBinding.inflate(layoutInflater) }
    private val datas = ArrayList<BtAdapter.Device>()
    private var bluetoothAdapter: BluetoothAdapter? = null
    private val launcher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                requestPermission()
            } else {
                Toast.makeText(requireContext(), R.string.request_permission_fail, Toast.LENGTH_SHORT).show()
            }
        }

    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                val action = intent.action
                if (BluetoothDevice.ACTION_FOUND == action) {
                    val btd =
                        checkNotNull(intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE))
                    if (btd.type == 2) return
                    if (btd.bondState != BluetoothDevice.BOND_BONDED && !deviceIsExist(btd.address)) {
                        var name = btd.name
                        if (name == null) name = btd.address
                        val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, 0.toShort()).toInt()
                        val deviceIcon  = UIUtils.deviceIcon(btd)
                        datas.add(BtAdapter.Device(false, name, mac = btd.address, rssi, deviceIcon))
                        Objects.requireNonNull(bind.recyclerView.adapter)
                            .notifyItemChanged(datas.size - 1)
                    }
                } else if (GPS_ACTION == action) {
                    if (isGpsOpen()) {
                        setBluetooth()
                    }
                }
            } catch (e : Exception){
                Log.e("Error", "onReceive: ${e.message.toString()}")
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        if (UIUtils.isDarkMode(requireContext())){
            bind.layoutBG.backgroundTintList = ColorStateList.valueOf("#000000".toColorInt())
        } else {
            bind.layoutBG.backgroundTintList = ColorStateList.valueOf("#ffffff".toColorInt())
        }


        bind.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = BtAdapter(datas)
        adapter.setItemClick(object : BtAdapter.ItemListener {
            override fun onClick(position: Int) {
                listener?.onDeviceSelected(datas[position])
                dismiss()
            }
        })
        bind.recyclerView.adapter = adapter

        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
        intentFilter.addAction(GPS_ACTION)
        requireContext().registerReceiver(mBroadcastReceiver, intentFilter)

        requestPermission()
        initClick()
    }

    private fun initClick() {
        bind.refreshTv.setOnClickListener { requestPermission() }
    }

    private fun requestPermission() {
        val permissionsList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        }
        permissionLauncher.launch(permissionsList)
    }

    private fun handlePermissionsGranted() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || isGpsOpen()) {
            setBluetooth()
        } else {
            openGPS()
        }
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.entries.all { it.value }

            if (allGranted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    handlePermissionsGranted()
                } else {
                    setBluetooth()
                }
            } else {
                Toast.makeText(requireContext(), R.string.request_permission_fail, Toast.LENGTH_SHORT).show()
            }
        }


    private var lastTime = 0L
    private fun setBluetooth() {
        try {
            if (System.currentTimeMillis() - lastTime < 1000) {
                return
            }
            lastTime = System.currentTimeMillis()
            bluetoothAdapter = (requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
            if (!bluetoothAdapter!!.isEnabled) {
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                launcher.launch(intent)
            } else {
                searchDevices()
            }
        } catch (e : Exception){
            Log.e("Error", "setBluetooth: ${e.message.toString()}")
        }

    }

    @SuppressLint("NotifyDataSetChanged", "CheckResult")
    private fun searchDevices() {
        datas.clear()
        val pairedDevices = bluetoothAdapter!!.bondedDevices
        for (it in pairedDevices) {
            var name = it.name
            if (name == null) name = it.address
            val deviceIcon  = UIUtils.deviceIcon(it)
            datas.add(BtAdapter.Device(true, name, mac = it.address, 0, deviceIcon))
        }
        if (bind.recyclerView.adapter != null) {
            bind.recyclerView.adapter!!.notifyDataSetChanged()
        }
        if (bluetoothAdapter!!.isDiscovering) {
            bluetoothAdapter!!.cancelDiscovery()
        }
        Handler(Looper.getMainLooper()).postDelayed({
            bluetoothAdapter?.startDiscovery()
        }, 300)

    }

    private fun deviceIsExist(mac: String): Boolean {
        for (bean in datas) {
            if (bean.mac == mac) {
                return true
            }
        }
        return false
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }


    private fun isGpsOpen(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun openGPS() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (bluetoothAdapter != null && bluetoothAdapter!!.isDiscovering) {
                bluetoothAdapter!!.cancelDiscovery()
            }
            requireContext().unregisterReceiver(mBroadcastReceiver)
        } catch (e : Exception){
            Log.e("Error", "onDestroy: ${e.message.toString()}")
        }
    }

    private var listener: OnDeviceSelectedListener? = null

    fun setListener(listener: OnDeviceSelectedListener) {
        this.listener = listener
    }

    interface OnDeviceSelectedListener {
        fun onDeviceSelected(device : BtAdapter.Device)
    }
}