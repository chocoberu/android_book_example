package com.mjh.gpsmap

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    // 1. 위치 정보를 주기적으로 얻는 데 필요한 객체들을 선언
    // MyLocationCallBack은 MapsActivity의 inner class로 생성
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: MyLocationCallBack
    private val polylineOptions = PolylineOptions().width(5f).color(Color.RED)
    
    private val REQUEST_ACCESS_FINE_LOCATION = 1000

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) // 화면이 꺼지지 않게 하기
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // 세로 모드로 화면 고정
        setContentView(R.layout.activity_maps)
        // supportFragmentManager를 가져와서 지도가 준비되면 알림을 받음
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this) // 지도가 준비되면 알림을 받음

        locationInit() // 2. 1에서 선언한 변수들을 초기화
    }
    // 3. 위치 정보를 얻기 위한 각종 초기화
    private fun locationInit()
    {
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        locationCallback = MyLocationCallBack()
        locationRequest = LocationRequest()

        // GPS 우선
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        // 업데이트 인터벌
        // 위치 정보가 없을 때는 업데이트 안함
        // 상황에 따라 짧아질 수 있음, 정확하지 않음
        // 다른 앱에서 짧은 인터벌로 위치 정보를 요청하면 짧아질 수 있음
        locationRequest.interval = 10000
        // 정확함. 이것보다 짧은 업데이트는 하지 않음
        locationRequest.fastestInterval = 5000
    }
    /**
     * 사용 가능한 맵을 조작합니다.
     * 지도를 사용할 준비가 되면 이 콜백이 호출됩니다.
     * 여기서 마커나 선, 청취자를 추가하거나 카메라를 이동할 수 있습니다.
     * 호주 시드니 근처에 마커를 추가하고 있습니다.
     * Google Play 서비스가 기기에 설치되어 있지 않은 경우 사용자에게
     * supportMapFragment 안에 Google Play 서비스를 설치하라는 메시지가 표시됩니다.
     * 이 메소드는 사용자가 Google Play 서비스를 설치하고 앱으로 돌아온 후에만 호출됩니다.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap // 지도가 준비되면 GoogleMap 객체를 얻습니다

        // 시드니에 마커를 추가하고 카메라를 이동합니다. 
        val sydney = LatLng(37.557398, 127.079597) // 위도 경도로 시드니의 위치를 정하고
        // 구글 지도 객체에 마커를 추가
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Seoul"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney)) // 카메라를 이동
    }

    override fun onResume() {
        super.onResume()

        // 권한 요청
        permissionCheck(cancle = {
            // 위치 정보가 필요한 이유 다이얼로그 표시
            showPermissionInfoDialog()
        }, ok = {
            // 현재 위치를 주기적으로 요청 (권한이 필요한 부분)
            addLocationListener() // 4. 이러한 위치 요청은 액티비티가 활성화되는 onResume 메소드에서 수행
        })
    }

    override fun onPause() {
        super.onPause()
        // 위치 요청을 취소
        removeLocationListener()
    }
    @SuppressLint("MissingPermission")
    private fun addLocationListener()
    {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }
    // 6. requestLocationUpdates 메소드에 전달되는 인자 중 LocationCallBack을 구현한 내부 클래스는
    // LocationResult 객체를 반환하고 lastLocation 프로퍼티로 Location 객체를 얻습니다.
    inner class MyLocationCallBack : LocationCallback()
    {
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)

            val location = p0?.lastLocation
            // 7. 기기의 GPS 설정이 꺼져 있거나 현재 위치 정보를 얻을 수 없는 경우에는 
            // Location 객체가 null일 수 있음, null이 아닐 때 해당 위치로 카메라 이동
            location?.run {
                // 14 level로 확대하고 현재 위치로 카메라 이동
                val latLng = LatLng(latitude, longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))

                // Location 객체의 위도,경도 값을 로그로 출력
                Log.d("MapsActivity", "위도 : $latitude, 경도 : $longitude")

                // PolyLine에 좌표 추가
                polylineOptions.add(latLng)
                // 선 그리기
                mMap.addPolyline(polylineOptions)
            }
        }
    }
    private fun permissionCheck(cancle : () -> Unit, ok : () -> Unit) // 함수 인자 2개를 받음, 두 함수 모두 인자가 없고 리턴값도 없음
    {
        // 위치 권한이 있는지 검사
        if(ContextCompat.checkSelfPermission
                (this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // 권한이 허용되지 않음
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
            {
                // 이전에 권한을 한번 거부한 적이 있는 경우에 실행할 함수
                cancle()
            }
            else
            {
                // 권한 요청
                ActivityCompat.requestPermissions(this, 
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_ACCESS_FINE_LOCATION)
            }
        }
        else
        {
            // 권한을 수락했을 때 실행할 함수
            ok()
        }
    }
    private fun showPermissionInfoDialog()
    {
        alert ("현재 위치 정보를 얻으려면 위치 권한이 필요합니다.", "권한이 필요한 이유"){
            yesButton {
                // 권한 요청
                ActivityCompat.requestPermissions(this@MapsActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_ACCESS_FINE_LOCATION)
            }
            noButton {  }
        }.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode)
        {
            REQUEST_ACCESS_FINE_LOCATION ->
            {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED )
                {
                    // 권한 허용됨
                    addLocationListener()
                }
                else
                {
                    // 권한 거부
                    toast("권한 거부 됨")
                }
                return
            }
        }
    }
    private fun removeLocationListener()
    {
        // 현재 위치 요청을 삭제
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}
