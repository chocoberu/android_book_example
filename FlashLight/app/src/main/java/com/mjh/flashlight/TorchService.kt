package com.mjh.flashlight

import android.app.Service
import android.content.Intent
import android.os.IBinder

class TorchService : Service() {

    // lazy : torch 객체를 처음 사용할 때 초기화
    private val torch : Torch by lazy {
        Torch(this)
    }

    private var isRunning = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action)
        {
            "on" -> {
                torch.flashOn()
                isRunning = true
            }
            "off" ->{
                torch.flashOff()
                isRunning = false
            }
            else ->{
                isRunning = !isRunning
                if(isRunning)
                    torch.flashOn()
                else
                    torch.flashOff()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}