package jp.co.erii.sugawara.test.android.beaconscanner

import android.util.Log
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import java.util.concurrent.TimeUnit

class MainViewModel : ViewModel() {
    private val beaconScanner = BeaconScanner()

    private val receivedList = mutableListOf<Beacon>()

    private val disposables = CompositeDisposable()

    fun onStart() {
        beaconScanner.startScanning()
            .buffer(3, TimeUnit.SECONDS)
            .subscribe { allReceived ->
                val addressList = allReceived.map { it.bdAddress }.distinct()

                val averagedBeaconList = addressList.map { targetAddress ->
                    allReceived.filter { it.bdAddress == targetAddress }
                }.map {
                    val averagedRssi = it.map { it.rssi }.reduce { acc, i -> acc + i } / it.size

                    val first = it.first()

                    Beacon(first.bdAddress, averagedRssi, first.data)
                }.sortedByDescending { it.rssi }

                receivedList.clear()

                receivedList.addAll(averagedBeaconList)

                Log.d(MainViewModel::class.java.simpleName, "onNext: receivedList.size=${receivedList.size}")
                receivedList.take(10).forEachIndexed { index, beacon ->
                    Log.d(MainViewModel::class.java.simpleName, "Top10 Ranking: [$index] beacon=${beacon.toString()}")
                }
            }
            .addTo(disposables)
    }

    fun onStop() {
        disposables.clear()
    }
}