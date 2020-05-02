package com.mjh.flashlight

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager

class Torch(context : Context) {
    private var cameraId : String? = null
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    init {
        cameraId = getCameraId()
    }

    fun flashOn()
    {
        cameraId?.run {
            cameraManager.setTorchMode(cameraId as String, true)
        }

    }
    fun flashOff()
    {
        cameraId?.run {
            cameraManager.setTorchMode(cameraId as String, false)
        }

    }
    private fun getCameraId() : String?
    {
        val cameraIds = cameraManager.cameraIdList
        for(id in cameraIds)
        {
            val info = cameraManager.getCameraCharacteristics(id) // ID별 세부 정보
            val flashAvailable = info.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) // 플래시 사용가능
            val lensFacing = info.get(CameraCharacteristics.LENS_FACING) // 카메라 렌즈 방향

            if(flashAvailable != null
                && flashAvailable
                && lensFacing != null
                && lensFacing == CameraCharacteristics.LENS_FACING_BACK)
            {
                return id
            }
        }
        return null
    }
}