package com.mdr.appselecttestassignment.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import com.mdr.appselecttestassignment.domain.NetworkStatus

class NetworkStatusImpl(private val context: Context): NetworkStatus {
    private val cm =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun isOnline(): Boolean {
        val network: Network = cm.activeNetwork ?: return false
        val networkCapabilities: NetworkCapabilities =
            cm.getNetworkCapabilities(network) ?: return false

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val isInternetSuspended =
                !networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_SUSPENDED)

            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                    && !isInternetSuspended
        } else {
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }
    }
}