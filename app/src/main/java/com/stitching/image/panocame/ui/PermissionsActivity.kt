package com.stitching.image.panocame.ui

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Process
import android.preference.PreferenceManager
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.stitching.image.panocame.R

class PermissionsActivity : AppCompatActivity() {
    val TAG = "PermissionActivity"
    private val ALL_PERMISSIONS = 0
    val AUDIO_PERMISSION = "android.permission.RECORD_AUDIO"
    val CAMERA_PERMISSION = "android.permission.CAMERA"
    val STORAGE_PERMISSIONS = "android.permission.WRITE_EXTERNAL_STORAGE"
    val FC_SHARED_PREFERENCE = "FC_Settings"
    val FC_MEDIA_PREFERENCE = "FC_Media"
    var cameraPermission = false
    var audioPermission = false
    var storagePermission = false
    var showMessage = false
    var showPermission = false
    var exitListener: DialogInterface.OnClickListener? = null
    var alertDialog: AlertDialog.Builder? = null
    private var sharedPreferences: SharedPreferences? = null
    var VERBOSE = false

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == ALL_PERMISSIONS) {
            if (permissions != null && permissions.size > 0) {
                if (VERBOSE) Log.d(TAG, "For camera == " + permissions[0])
                if (permissions[0].equals(CAMERA_PERMISSION, ignoreCase = true) && permissions[1].equals(AUDIO_PERMISSION, ignoreCase = true) &&
                        permissions[2].equals(STORAGE_PERMISSIONS, ignoreCase = true)) {
                    if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                        cameraPermission = true
                        audioPermission = true
                        storagePermission = true
                        openApp()
                    } else {
                        quitApp()
                    }
                }
            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (VERBOSE) Log.d(TAG, "onCreate")
        setContentView(R.layout.activity_permissions)
        if (VERBOSE) Log.d(TAG, "saved instance state == $savedInstanceState")
        if (savedInstanceState != null) {
            if (VERBOSE) Log.d(TAG, "saved instance state restart == " + savedInstanceState.getBoolean("restart"))
            if (VERBOSE) Log.d(TAG, "saved instance state quit == " + savedInstanceState.getBoolean("quit"))
        }

       // supportActionBar!!.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("restart")) {
                showMessage = true
                quitApp()
            }
        }
    }

    fun getSharedPreferences(): SharedPreferences? {
        return sharedPreferences
    }

    override fun onDestroy() {
        if (VERBOSE) Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

    override fun onStart() {
        if (VERBOSE) Log.d(TAG, "onStart")
        super.onStart()
    }

    override fun onStop() {
        if (VERBOSE) Log.d(TAG, "onStop")
        super.onStop()
    }

    override fun onPause() {
        if (VERBOSE) Log.d(TAG, "onPause")
        super.onPause()
    }

    override fun onResume() {
        if (VERBOSE) Log.d(TAG, "onResume")
        super.onResume()
       if (!showMessage) {
            if (VERBOSE) Log.d(TAG, "Check permissions and Start camera = $showPermission")
            val camerapermission = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
            val audiopermission = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.RECORD_AUDIO)
            val storagepermission = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (camerapermission == PackageManager.PERMISSION_GRANTED && audiopermission == PackageManager.PERMISSION_GRANTED && storagepermission == PackageManager.PERMISSION_GRANTED) {
                if (VERBOSE) Log.d(TAG, "ALL permissions obtained.")
                cameraPermission = true
                audioPermission = true
                storagePermission = true
                openApp()
            } else if (!showPermission) {
                if (VERBOSE) Log.d(TAG, "Permissions not obtained. Obtain explicitly")
                //Remove shared preferences. This is necessary, since for some devices it is pre-selected
                //leading to errors.
                  if (VERBOSE) Log.d(TAG, "REMOVED SHAREDPREFS")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        ALL_PERMISSIONS)
                showPermission = true
            }
        }
    }

    fun openApp() {
        if (cameraPermission && audioPermission && storagePermission) {
            //Open VideoFragment under CameraActivity showing camera preview.
            val cameraIntent = Intent(this, StitchingImageActivity::class.java)
            startActivity(cameraIntent)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        if (VERBOSE) Log.d(TAG, "Restore state = $savedInstanceState")
        if (savedInstanceState != null && savedInstanceState.getBoolean("quit")) {
            //The activity was restarted because of possible low memory situation.
            if (VERBOSE) Log.d(TAG, "Quit app")
            finish()
        } else if (savedInstanceState != null && savedInstanceState.getBoolean("showPermission")) {
            showPermission = savedInstanceState.getBoolean("showPermission")
            if (VERBOSE) Log.d(TAG, "show permission = $showPermission")
        }
        super.onRestoreInstanceState(savedInstanceState!!)
        super.onRestoreInstanceState(savedInstanceState)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        if (VERBOSE) Log.d(TAG, "Save before restart")
        if (showMessage) {
            outState.putBoolean("restart", true)
            outState.putBoolean("quit", false)
            if (VERBOSE) Log.d(TAG, "Saved restart")
        } else if (cameraPermission && audioPermission && storagePermission) {
            //The activity could be destroyed because of low memory. Keep a flag to quit the activity when you navigate back here.
            outState.putBoolean("quit", true)
            outState.putBoolean("restart", false)
            if (VERBOSE) Log.d(TAG, "Safe to quit")
        }
        outState.putBoolean("showPermission", showPermission)
        super.onSaveInstanceState(outState)
    }

    fun quitApp() {
        exitListener = DialogInterface.OnClickListener { dialogInterface, which -> Process.killProcess(Process.myPid()) }
        alertDialog = AlertDialog.Builder(this)
        alertDialog!!.setTitle(getString(R.string.title))
        alertDialog!!.setMessage(getString(R.string.message))
        //alertDialog.setNegativeButton(R.string.exit)
        alertDialog!!.setCancelable(false)
        alertDialog!!.show()
        showMessage = true
    }
}
