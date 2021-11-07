package com.example.androidstorage11permission

import android.Manifest.permission
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE

import androidx.core.content.ContextCompat

import android.Manifest.permission.READ_EXTERNAL_STORAGE

import android.os.Environment

import android.os.Build
import android.os.Build.VERSION

import android.os.Build.VERSION.SDK_INT
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint

import androidx.core.app.ActivityCompat

import androidx.core.app.ActivityCompat.startActivityForResult

import android.content.Intent
import android.net.Uri

import android.os.Build.VERSION.SDK_INT
import android.provider.Settings
import android.view.MenuItem
import java.lang.Exception
import android.widget.Toast
import androidx.annotation.Nullable


class MainActivity : AppCompatActivity() {
    private val PERMISSION_REQUEST_CODE = 2296
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (checkPermission()) {
            Toast.makeText(
                this,
                "Download File",
                Toast.LENGTH_SHORT
            )
                .show()
        } else {
            requestPermission()
        }

    }

    private fun checkPermission(): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result =
                ContextCompat.checkSelfPermission(this@MainActivity, READ_EXTERNAL_STORAGE)
            val result1 =
                ContextCompat.checkSelfPermission(this@MainActivity, WRITE_EXTERNAL_STORAGE)
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                startActivityForResult(intent, 2296)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, 2296)
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success


                } else {
                    Toast.makeText(this,
                        "Allow permission for storage access!",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when (requestCode) {

            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty()) {
                try {
                    val READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val WRITE_EXTERNAL_STORAGE =
                        grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                        Toast.makeText(this, "Download", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Allow permission for storage access!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}