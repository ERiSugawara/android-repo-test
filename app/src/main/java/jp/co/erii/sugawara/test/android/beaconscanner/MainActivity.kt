package jp.co.erii.sugawara.test.android.beaconscanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    companion object {
        const val requestCodeRuntimePermissionAccessFineLocation = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            requestCodeRuntimePermissionAccessFineLocation -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.onStart()
                }
                else {
                    finish()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestCodeRuntimePermissionAccessFineLocation)
        }
        else {
            viewModel.onStart()
        }
    }

    override fun onStop() {
        viewModel.onStop()

        super.onStop()
    }
}
