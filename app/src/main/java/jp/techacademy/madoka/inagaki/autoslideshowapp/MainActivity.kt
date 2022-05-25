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
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.graphics.Color;
import android.provider.OpenableColumns
import android.util.Log
import androidx.appcompat.app.AlertDialog


class MainActivity : AppCompatActivity() {
    private var cursor: Cursor? = null
    private val PERMISSIONS_REQUEST_CODE = 100
    var mTimer: Timer? = null
    private var mHandler = Handler()
    var permissionChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSlide.setText("自動再生開始")

        permissionStorage()

        //ボタン押したら
        btnNext.setOnClickListener {
            if (mTimer == null) {
                nextView()
            } else {
            }
        }
        btnPre.setOnClickListener {
            if (mTimer == null) {
                preView()
            } else {
            }
        }
        btnSlide.setOnClickListener {
            timer()
        }
    }


    private fun permissionStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //パーミッション許可状態確認
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可済み
                start()
            } else {
                // 許可まだ→ダイアログを表示する
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
            // Android5以下
        } else {
            start()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //許可された
                    start()
                } else {
                    //許可されなかった
                    if (!shouldShowRequestPermissionRationale(permissions[0])) {
                        // 「二度と表示しない」がチェックされた
                        permissionChecked = true
                        showAlertDialog()
                    }else{
                        showAlertDialog()
                    }
                }
        }
    }

    //許可されなかった時のダイアログ

    private fun showAlertDialog() {
        if (permissionChecked == false) {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("このアプリの使用にはストレージへのアクセス許可が必要です")
            alertDialogBuilder.setMessage("次の画面で許可を選択してください")


            alertDialogBuilder.setPositiveButton("次へ") { dialog, which ->
                permissionStorage()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

        }else {

            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("ストレージへのアクセス許可が必要です。")
            alertDialogBuilder.setMessage("一度アプリを閉じ、設定から権限を変更してください")
                .setCancelable(false)

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }

    //最初に表示する
    fun start() {
        cursor1()
        cursor!!.moveToFirst()
        view()
    }

    fun timer() {
        if(mTimer == null) {

            //ボタン無効を視覚化
            btnPre.setColorFilter(Color.argb(230, 255 , 255, 255));
            btnNext.setColorFilter(Color.argb(230, 255 , 255, 255));

            //タイマースタート※タイマーがnullだから新規
            btnSlide.setText("停止")
            mTimer = Timer()
            mTimer!!.schedule(object : TimerTask() {
                override fun run() {
                    mHandler.post {
                        nextView()
                    }
                }
            }, 2000, 2000)
        }else{
            //停止する処理※タイマーが始まっている状態でボタンが押されてた
            mTimer!!.cancel()
            mTimer = null
            btnSlide.setText("自動再生開始")

            btnPre.setColorFilter(Color.argb(0, 255 , 255, 255));
            btnNext.setColorFilter(Color.argb(0, 255 , 255, 255));

        }
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
        val nameIndex = cursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        textTitle.text = ("画像名: " + cursor!!.getString(nameIndex))
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

    override fun onDestroy() {
        super.onDestroy()
        cursor!!.close()
    }
}