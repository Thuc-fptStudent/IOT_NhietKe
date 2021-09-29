package com.example.nhietkeiot

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.Console

class MainActivity : AppCompatActivity() {
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var tvDevice : TextView
    lateinit var listView: ListView
    lateinit var adapter: ArrayAdapter<String>
    lateinit var list : ArrayList<String>
    var REQUEST_ENABLE_BT = 1
    var REQUEST_DISCOVERABLE_BT = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvDevice = findViewById(R.id.blView)
        listView = findViewById(R.id.listView)
        list = ArrayList()
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        checkHasBlue()
        checkBlue()
        showListDevice()

    }

    //Kiểm tra thiết bị xem có hỗ trợ bluetooth không
    fun checkHasBlue() {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(application, "Thiết bị không hỗ trợ bluetooth", Toast.LENGTH_LONG).show()
        }
    }

    // hiển thị dialog yêu cầu bật bluetooth
    private fun checkBlue() {
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    fun discoverableTheBlue() {
        if (!bluetoothAdapter.isDiscovering) {
            Log.e("BLUETOOTH: ", "Discovering")
            Toast.makeText(application, "Discovering", Toast.LENGTH_SHORT).show()
            var intent: Intent
            intent = Intent(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE))
            startActivityForResult(intent, REQUEST_DISCOVERABLE_BT)
            getListOfPairDevice()
        }
    }

    //lay ra nhung thiet bi da ket noi
    fun getListOfPairDevice(){
        if (bluetoothAdapter.isEnabled){
            var device = bluetoothAdapter.bondedDevices
            for (device in device){
                tvDevice.append("\n"+device.name)
                list.add(device.name)
            }

        }
    }

    fun showListDevice(){
        adapter = ArrayAdapter(application, android.R.layout.simple_list_item_1, list)
        listView.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_ENABLE_BT ->
                if (resultCode == Activity.RESULT_OK) {
                    Log.e("BLUETOOTH: ", "Bluetooth is on")
                    Toast.makeText(application, "Bluetooth is on", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("BLUETOOTH: ", "Bluetooth is off")
                    Toast.makeText(application, "Bluetooth is off", Toast.LENGTH_SHORT).show()
                }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun click(view: android.view.View) {
        list = ArrayList()
        discoverableTheBlue()
    }
}