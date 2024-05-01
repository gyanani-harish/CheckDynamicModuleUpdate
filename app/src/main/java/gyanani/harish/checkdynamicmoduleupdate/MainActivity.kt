package gyanani.harish.checkdynamicmoduleupdate

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.ktx.BuildConfig
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus


class MainActivity : AppCompatActivity() {
    private lateinit var manager: SplitInstallManager
    private val DYNAMIC_MODULE = "dynamicfeature1"
    val myString = "Hello, SharedPreferences!"
    val key = "myKey"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        manager = SplitInstallManagerFactory.create(this)
        findViewById<TextView>(R.id.txt).setOnClickListener {
            if(getStringFromSharedPreferences(this@MainActivity, key, "") == myString){
                toastAndLog("DO NOT DOWNLOAD AGAIN")
                openActivity()
                return@setOnClickListener
            }
            if (manager.installedModules.contains(DYNAMIC_MODULE)) {
                openActivity()
            } else {
                val request = SplitInstallRequest.newBuilder()
                    .addModule(DYNAMIC_MODULE)
                    .build()
                manager.registerListener(object : SplitInstallStateUpdatedListener {
                    override fun onStateUpdate(state: SplitInstallSessionState) {
                        when(state.status()){
                            SplitInstallSessionStatus.DOWNLOADING ->{
                                toastAndLog("STATUS - Module ${DYNAMIC_MODULE} DOWNLOADING")
                            }
                            SplitInstallSessionStatus.DOWNLOADED ->{
                                toastAndLog("STATUS - Module ${DYNAMIC_MODULE} DOWNLOADED")
                            }
                            SplitInstallSessionStatus.INSTALLED ->{
                                toastAndLog("STATUS - Module ${DYNAMIC_MODULE} installed")
                                saveStringToSharedPreferences(this@MainActivity, key, myString)
                                openActivity()
                            }
                            SplitInstallSessionStatus.FAILED ->{
                                toastAndLog("STATUS - Module ${DYNAMIC_MODULE} FAILED")
                            }
                            else ->{
                                toastAndLog("STATUS - Module ${DYNAMIC_MODULE} ELSE ${state.status()}")
                            }
                        }
                    }

                })
                manager.startInstall(request)
                    .addOnCompleteListener {
                        toastAndLog("Module ${DYNAMIC_MODULE} installed")
                    }
                    .addOnSuccessListener { toastAndLog("Loading ${DYNAMIC_MODULE}") }
                    .addOnFailureListener { toastAndLog("Error Loading ${DYNAMIC_MODULE}") }
            }
        }
    }

    private fun openActivity() {
        val intent = this.intent
        intent.setClassName(
            "gyanani.harish.checkdynamicmoduleupdate",
            "gyanani.harish.dynamicfeature1.DynamicActivity1"
        )
        this.startActivity(intent)
    }

    private fun toastAndLog(s: String) {
        Toast.makeText(this@MainActivity, s, Toast.LENGTH_SHORT).show()
        Log.i("Check", s)
    }

    // Function to save a string value to SharedPreferences
    fun saveStringToSharedPreferences(context: Context, key: String, value: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    // Function to retrieve a string value from SharedPreferences
    fun getStringFromSharedPreferences(context: Context, key: String, defaultValue: String): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }
}