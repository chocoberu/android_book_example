package com.mjh.flashlight

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val torch = Torch(this)

        flashSwitch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                startService(intentFor<TorchService>().setAction("on"))
            else
                startService(intentFor<TorchService>().setAction("off"))
        }
        //startService(intentFor<TorchService>().setAction("on"))
        /*val intent = Intent(this, TorchService::class.java)
        intent.action = "on"
        startService(intent)*/
    }
}
