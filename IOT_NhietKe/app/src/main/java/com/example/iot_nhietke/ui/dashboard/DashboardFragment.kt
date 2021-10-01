package com.example.iot_nhietke.ui.dashboard

import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.iot_nhietke.R
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

lateinit var bluetoothAdapter: BluetoothAdapter

lateinit var progresDialog: ProgressDialog
var bluetoothSocket: BluetoothSocket? = null
var isConnected: Boolean = false
lateinit var address: String
var NAME = "THUC"
var MY_UUID: UUID = UUID.randomUUID()
var TAG = "BLUETOOTH"

class DashboardFragment : Fragment() {

    lateinit var tvDevice: TextView
    lateinit var listView: ListView
    lateinit var adapter: ArrayAdapter<BluetoothDevice>
    lateinit var list: ArrayList<BluetoothDevice>
    var REQUEST_ENABLE_BT = 1
    var REQUEST_DISCOVERABLE_BT = 2
    lateinit var context2: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        context2 = container?.context!!
        val root: View = inflater.inflate(R.layout.fragment_dashboard, container, false)
        tvDevice = root.findViewById(R.id.blView)
        listView = root.findViewById(R.id.listView)
        list = ArrayList()
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        checkHasBlue()
        checkBlue()
        discoverableTheBlue()
        showListDevice()
        return root
    }

    //Kiểm tra thiết bị xem có hỗ trợ bluetooth không
    fun checkHasBlue() {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(context2, "Thiết bị không hỗ trợ bluetooth", Toast.LENGTH_LONG).show()
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
            var intent: Intent
            intent = Intent(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE))
            startActivityForResult(intent, REQUEST_DISCOVERABLE_BT)
            getListOfPairDevice()
            Log.e("BLUETOOTH: ", "start Discovering")
        } else if (bluetoothAdapter.isDiscovering) {
            Log.e("BLUETOOTH: ", "Discovering")

        }
    }

    //lay ra nhung thiet bi da ket noi
    fun getListOfPairDevice() {
        if (bluetoothAdapter.isEnabled) {
            var device = bluetoothAdapter.bondedDevices
            for (device: BluetoothDevice in device) {
                list.add(device)
                Log.e("BLUETOOTH: ", device.toString())
            }
        }
        if (list != null) {
            tvDevice.text = "Show data"
        }
    }

    fun showListDevice() {
        adapter = ArrayAdapter(context2, android.R.layout.simple_list_item_1, list)
        listView.adapter = adapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, l ->
            address = list[position].address
            ConnectDevice(context2).execute()
            senCommand("a")
        }
    }

    fun senCommand(input: String) {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun disConnect() {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket!!.close()
                bluetoothSocket = null
                isConnected = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_ENABLE_BT ->
                if (resultCode == Activity.RESULT_OK) {
                    Log.e("BLUETOOTH: ", "Bluetooth is on")
                    Toast.makeText(context2, "Bluetooth is on", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("BLUETOOTH: ", "Bluetooth is off")
                    Toast.makeText(context2, "Bluetooth is off", Toast.LENGTH_SHORT).show()
                }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private class ConnectDevice(c: Context) : AsyncTask<Void, Void, String>() {
        var isConnectSuccess: Boolean = true
        lateinit var context: Context

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progresDialog = ProgressDialog.show(context, "Connecting...", "Please wait")

        }

        override fun doInBackground(vararg p0: Void?): String {
            try {
                if (bluetoothSocket != null || !isConnected) {
                    Log.e("Address", address)
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    var device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(address)
                    bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID)
                    bluetoothAdapter.cancelDiscovery()
                    bluetoothSocket!!.connect()
                    if (bluetoothSocket!!.isConnected){
                        Log.e(TAG, "connected")
                    }
                }
            } catch (e: Exception) {
                isConnectSuccess = false
                e.printStackTrace()
            }
            return null.toString()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!isConnectSuccess){
                Log.e("data: ", "Couldn't connect")
                Log.e(TAG, isConnectSuccess.toString())
            }else{
                isConnected = true
                Log.e(TAG, isConnectSuccess.toString())
            }
            progresDialog.dismiss()
        }
    }
}


