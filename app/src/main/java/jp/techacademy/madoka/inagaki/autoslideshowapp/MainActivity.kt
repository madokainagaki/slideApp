package jp.techacademy.madoka.inagaki.autoslideshowapp

import android.content.ContentUris
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var cursor : Cursor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val resolver = contentResolver
        cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )

        //ボタンのリスナー
        btnNext.setOnClickListener {
            nextView()
        }
        btnPre.setOnClickListener {
            preView()
        }

        //起動時にひとまず表示。
        cursor!!.moveToFirst()
        view()
    }

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

