package com.dangiashish.bluetoothscanner.demo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dangiashish.bluetoothscanner.demo.databinding.ActivityMainBinding
import com.dangiashish.bluetoothscanner.BtAdapter
import com.dangiashish.bluetoothscanner.BluetoothScanner

class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    var name = ""
    var mac = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.btnSelectDevice.setOnClickListener {
            /**
             * Call the [BluetoothScanner] dialog with its [BluetoothScanner.OnDeviceSelectedListener] (if required).
             */
            val dialog = BluetoothScanner()
            dialog.setListener(object : BluetoothScanner.OnDeviceSelectedListener {
                override fun onDeviceSelected(device : BtAdapter.Device) {
                    this@MainActivity.mac = device.mac
                    this@MainActivity.name = device.name
                    binding.tvConnectedWith.text = name
                }
            })
            dialog.show(supportFragmentManager, "BluetoothDevices")
        }
    }


}