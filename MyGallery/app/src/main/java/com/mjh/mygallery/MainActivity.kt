package com.mjh.mygallery

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import kotlin.concurrent.timer


class MainActivity : AppCompatActivity() {

    private val REQUEST_READ_EXTERNAL_STORAGE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 권한이 부여되었는지 확인
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
            != PackageManager.PERMISSION_GRANTED)
        {
            // 권한이 허용되지 않음
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                // 이전에 이미 권한이 거부되었을 때 설명
                alert("사전 정보를 얻으려면 외부 저장소 권한이 필수로 필요합니다.", "권한이 필요한 이유"){
                    yesButton { 
                        // 권한 요청
                        ActivityCompat.requestPermissions(this@MainActivity, 
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 
                        REQUEST_READ_EXTERNAL_STORAGE)
                    }
                    noButton {  }
                }.show()
            }
            else
            {
                // 권한 요청
                ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_READ_EXTERNAL_STORAGE)
            }
        }
        else
            getAllPhotos() // 권한이 이미 허용됨
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode)
        {
            REQUEST_READ_EXTERNAL_STORAGE ->
            {
                if((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED))
                {
                    // 권한 허용됨
                    getAllPhotos()
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
    private fun getAllPhotos()
    {
        // 모든 사진 정보 가져오기
        val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        null,  // 가져올 항목 배열
        null, // 조건
        null, // 조건
        MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC") // 찍은 날짜 내림차순
        val fragments = ArrayList<Fragment>() // Fragment ArrayList 생성
        if(cursor != null)
        {
            while(cursor.moveToNext())
            {
                // 사진 경로 URI 가져오기
                val uri = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                Log.d("MainActivity", uri)
                fragments.add(PhotoFragment.newInstance(uri)) // 프래그먼트 생성한 후 리스트에 추가
            }
            cursor.close() // cursor 객체를 다 사용했으므로 close()로 닫음 (메모리 누수 방지)
        }
        // 어댑터
        // MyPagerAdapter 생성하면서 프래그먼트 매니저를 생성자의 인자로 전달
        // ViewPager는 여러 프래그먼트들을 좌우로 슬라이드 하는 뷰 (어댑터 패턴)
        // 데이터 : 프래그먼트, 어댑터 : 프래그먼트를 어느 화면에 표시할 것인지 관리하는 객체
        // 뷰 : 뷰페이저
        val adapter = MyPagerAdapter(supportFragmentManager)
        adapter.updateFragments(fragments)
        viewPager.adapter = adapter // 어댑터를 viewPager에 설정
        
        // 3초마다 자동 슬라이드
        timer(period = 3000){
            runOnUiThread { 
                if(viewPager.currentItem < adapter.count - 1)
                    viewPager.currentItem = viewPager.currentItem + 1
                else
                    viewPager.currentItem = 0
            }
        }
    }
}
