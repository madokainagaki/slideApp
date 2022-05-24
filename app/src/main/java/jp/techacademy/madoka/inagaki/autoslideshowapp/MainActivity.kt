package jp.techacademy.madoka.inagaki.autoslideshowapp

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private var cursor : Cursor? = null
    private val PERMISSIONS_REQUEST_CODE = 100
    var mTimer: Timer? = null
    var mTimerSec = 0
    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //パーミッション許可状態確認
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可済み
                start()
            } else {
                // 許可まだ→ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android5以下
        } else {
            start()
        }

        btnNext.setOnClickListener{nextView()}
        btnPre.setOnClickListener{preView()}
        slide.setOnClickListener{timer()}

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("test", "許可された")
                    start()
                }else{
                    Log.d("test", "許可されなかった")
                }
        }
    }

    //最初に表示する
    fun start() {
        cursor1()
        cursor!!.moveToFirst()
        view()
    }

    fun timer(){
        mTimer = Timer()
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                mHandler.post {
                    Log.d("test","testだよ")
                    nextView()
                }
            }
        }, 2000, 2000)
    }

    //カーソルの定義
    fun cursor1(){
        val resolver = contentResolver
        cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )
    }

    //表示する
    fun view(){
        val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
        val id = cursor!!.getLong(fieldIndex)
        val imageUri =
            ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
        imageView.setImageURI(imageUri)
    }

    //次へ
    fun nextView() {
        if (cursor!!.isLast) {
            cursor!!.moveToFirst()
        } else {
            cursor!!.moveToNext()
        }
        view()
    }

    //前へ
    fun preView() {
        if (cursor!!.isFirst) {
            cursor!!.moveToLast()
        } else {
            cursor!!.moveToPrevious()
        }
        view()
    }

}
