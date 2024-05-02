package gyanani.harish.dynamicfeature1

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DynamicActivity1: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic1)
        findViewById<TextView>(R.id.dtxt).setOnClickListener {
            Toast.makeText(this@DynamicActivity1, "Dynamic Module Updated to v7 with module installed check", Toast.LENGTH_LONG).show()
        }
    }
}