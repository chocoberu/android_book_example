package com.mjh.flashlight

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class TorchAppWidget : AppWidgetProvider() { // 일종의 브로드캐스트 리시버 클래스를 상속받음
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) { // 위젯 업데이트 시 호출됨
        // 위젯이 여러 개 배치되었다면 모든 위젯을 업데이트 한다.
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // 위젯이 처음 생성될 때 호출
    }

    override fun onDisabled(context: Context) {
        // 여러 개일 경우 마지막 위젯이 제거될 때 호출
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) { // 위젯을 업데이트 할 때 수행되는 코드
    val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
    // 위젯은 액티비티에서 레이아웃을 다루는 것과 조금 다름
    // 위젯에 배치하는 뷰는 따로 있음, 그것들은 Remoteviews 객체로 가져올 수 있음
    val views = RemoteViews(context.packageName, R.layout.torch_app_widget)
    views.setTextViewText(R.id.appwidget_text, widgetText) 
    
    
    // 위젯을 클릭했을 때의 처리
    // 실행할 intent 작성
    val intent = Intent(context, TorchService::class.java)
    val pendingIntent = PendingIntent.getService(context, 0, intent, 0)
    // PendingIntent는 실행할 인텐트 정보를 가지고 있다가 수행함
    // getService는 서비스 수행 (컨텍스트, 리퀘스트 코드, 서비스 인텐트, 플래그)
    
    // 위젯을 클릭하면 위에서 정의한 Intent 실행
    views.setOnClickPendingIntent(R.id.appwidget_layout, pendingIntent)
    
    // Instruct the widget manager to update the widget
    // 레이아웃을 모두 수정했다면 AppWidgetManager를 사용해 위젯을 업데이트 한다
    appWidgetManager.updateAppWidget(appWidgetId, views)
}