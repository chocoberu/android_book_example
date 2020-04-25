package com.mjh.mywebbrowser

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.browse
import org.jetbrains.anko.email
import org.jetbrains.anko.sendSMS
import org.jetbrains.anko.share

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 웹뷰 기본 설정
        webView.apply {
            settings.javaScriptEnabled = true // 자바 스크립트 기능 동작하도록 설정
            webViewClient = WebViewClient() // WebViewClient 클래스를 지정
        }
        webView.loadUrl("https://www.google.com")
        
        // editText의 setOnEditorActionListener는 에딧텍스트가 선택되고 글자가 입력될 때 마다 호출
        // 인자로는 반응한 뷰, 액션 ID, 이벤트 3가지 (여기서는 뷰와 이벤트를 사용하지 않기 때문에 _ 로 대치할 수 있음
        urlEditText.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH) // 검색 버튼이 눌렸다면
            {
                webView.loadUrl(urlEditText.text.toString())
                true
            }
            else
            {
                false
            }
        }
        // 컨텍스트 메뉴 등록
        registerForContextMenu(webView)
    }

    override fun onBackPressed() {
        if(webView.canGoBack())
            webView.goBack() // 이전 페이지로 갈 수 있으면 이전 페이지로 이동
        else
            super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { // 옵션 메뉴를 액티비티에 표시
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { // 옵션 메뉴 클릭 이벤트 처리
        when(item?.itemId)
        {
            R.id.action_google, R.id.action_home ->{
                webView.loadUrl("http://www.google.com")
                return true
            }
            R.id.action_naver ->
            {
                webView.loadUrl("http://www.naver.com")
                return true
            }
            R.id.action_daum ->
            {
                webView.loadUrl("http://www.daum.net")
                return true
            }
            R.id.action_call ->
            {
                val intent = Intent(Intent.ACTION_DIAL) // 암시적 인텐트로 전화 앱 전환
                intent.data = Uri.parse("tel:010-8644-2903")
                if(intent.resolveActivity(packageManager) != null){
                    startActivity(intent)
                }
                return true
            }
            R.id.action_send_text ->
            {
                // 문자보내기
                sendSMS("010-8644-2903",webView.url)
                return true
            }
            R.id.action_email ->
            {
                // 이메일 보내기
                email("mjh2903@gmail.com", "테스트 사이트", webView.url)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item?.itemId)
        {
            R.id.action_share ->
            {
                // 페이지 공유
                share(webView.url)
                return true
            }
            R.id.action_browser ->
            {
                // 기본 웹 브라우저에서 열기
                browse(webView.url)
                return true
            }
        }
        return super.onContextItemSelected(item)
    }
}
