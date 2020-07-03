package com.cotemustis.safetykitdemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.huawei.hms.api.ConnectionResult
import com.huawei.hms.api.HuaweiApiAvailability
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.api.safetydetect.SafetyDetect
import com.huawei.hms.support.api.safetydetect.SafetyDetectStatusCodes
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "WIFI-DETECT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (HuaweiApiAvailability.getInstance()
                .isHuaweiMobileServicesAvailable(this) == ConnectionResult.SUCCESS
        ) {
            //Load safety
            setupWifiDetect()
        } else {
            //Load HMS in a prompt
        }
    }

    private fun setupWifiDetect() {
        val wifiDetectClient = SafetyDetect.getClient(this@MainActivity)
        val task = wifiDetectClient.wifiDetectStatus
        task.addOnSuccessListener { wifiDetectResponse ->
            val wifiDetectStatus = wifiDetectResponse.wifiDetectStatus

            /**
             *-1: Failed to obtain the Wi-Fi status.
             * 0: No Wi-Fi is connected.
             * 1: The connected Wi-Fi is secure.
             * 2: The connected Wi-Fi is insecure.
             */

            Log.e(TAG, "Wifi status $wifiDetectStatus")

            wifiDetectMessage.text = "Wifi status $wifiDetectStatus"

        }.addOnFailureListener {
            if (it is ApiException) {
                val apiException = it
                Log.e(
                    TAG, "Error " + apiException.statusCode + ":"
                            + SafetyDetectStatusCodes.getStatusCodeString(apiException.statusCode)
                            + ": " + apiException.statusMessage
                )

                wifiDetectMessage.text =
                    "Error " + apiException.statusCode + ":" + SafetyDetectStatusCodes.getStatusCodeString(
                        apiException.statusCode
                    ) + ": " + apiException.statusMessage

            } else {
                Log.e(TAG, "OTHER ERROR: " + it.message)
                wifiDetectMessage.text = "OTHER ERROR: " + it.message
            }
        }
    }
}