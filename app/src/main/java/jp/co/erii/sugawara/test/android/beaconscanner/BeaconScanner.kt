package jp.co.erii.sugawara.test.android.beaconscanner

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.util.Log
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class BeaconScanner {
    private var scanCallback: ScanCallback? = null

    fun startScanning(): Observable<Beacon> {
        return PublishSubject.create<Beacon>()
            .also { subject ->
                val scanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner ?: let {
                    subject.onError(IllegalStateException("bluetoothLeScanner is null"))
                    return@also
                }

                val scanCallback = object : ScanCallback() {
                    override fun onScanFailed(errorCode: Int) {
                        super.onScanFailed(errorCode)

                        Log.e(BeaconScanner::class.java.simpleName, "onScanFailed: errorCode=$errorCode")

                        subject.onError(IllegalStateException("onScanFailed: errorCode=$errorCode"))
                    }

                    override fun onScanResult(callbackType: Int, result: ScanResult?) {
                        super.onScanResult(callbackType, result)

                        val address = result?.device?.address ?: return
                        val data = result.scanRecord?.bytes ?: return
                        val rssi = result.rssi

                        val receivedBeacon = Beacon(address, rssi, data)

                        subject.onNext(receivedBeacon)
                    }
                }.also {
                    this.scanCallback = it
                }

                scanner.startScan(
                    listOf(ScanFilter.Builder().build()),
                    ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(),
                    scanCallback
                )
            }
            .doOnDispose {
                val scanCallback = this.scanCallback ?: return@doOnDispose
                this.scanCallback = null

                val scanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner ?: return@doOnDispose
                scanner.stopScan(scanCallback)
            }
    }
}