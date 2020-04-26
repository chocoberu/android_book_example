package com.mjh.mygallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_photo.*

/**
 * A simple [Fragment] subclass.
 */
// 1. 컴파일 시간에 결정되는 상수값
private const val ARG_URI = "uri"

class PhotoFragment : Fragment() {
    
    private var uri : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 3. 프래그먼트가 생성되면 onCreate 메소드가 호출되고 ARG_URI 키에 저장된 uri 값을 얻어서 변수에 저장
        arguments?.let { 
            uri = it.getString(ARG_URI)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 4. onCreateView 메소드에서는 프래그먼트에 표시될 뷰를 생성
        // 액티비티가 아닌 곳에서 레이아웃 리소스를 가지고 오려면 LayoutInflater 객체의 inflate() 메소드를 사용
        // R.layout.fragment_photo 파일을 가지고 와서 반환
        return inflater.inflate(R.layout.fragment_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 5. Glide.with(this)로 사용 준비를 하고 load() 메소드에 uri 값을 인자로 주고 
        // 해당 이미지를 부드럽게 로딩함. 이미지가 로딩되면 into() 메소드로 imageView에 표시
        Glide.with(this).load(uri).into(imageView)
    }
    // 2. newInstance 메소드를 이용해 프래그먼트를 생성할 수 있고 인자로 uri 값을 전달.
    // 이 값은 Bundle 객체에 ARG_URI 키로 저장되고 arguments 프로퍼티에 저장
    companion object{ // static과 비슷 (차이점 : companion object는 실제 객체의 인스턴스 멤버, 인터페이스 구현도 가능)
        @JvmStatic
        fun newInstance(uri : String) =
            PhotoFragment().apply {
                arguments = Bundle().apply { 
                    putString(ARG_URI, uri)
                }
            }
    }

}
