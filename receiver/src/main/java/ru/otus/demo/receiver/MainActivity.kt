package ru.otus.demo.receiver

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener(View.OnClickListener {
            val AUTHORITY = "ru.otus.demo.contenproviderapp"
            val BASE_PATH = "books"
            val CONTENT_URI = Uri.parse("content://" + AUTHORITY
                    + "/" + BASE_PATH)
            val cursor1 = contentResolver.query(CONTENT_URI, null, null, null,
                    null)
            cursor1?.close()
            val contentProviderClient = contentResolver.acquireContentProviderClient(CONTENT_URI)
                    ?: return@OnClickListener
            var cursor: Cursor? = null
            try {
                cursor = contentProviderClient.query(
                        CONTENT_URI, 
                        null/*arrayOf("_id", "name", "isbn", "description")*/,
                        null,
                        null,
                        null)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            if (cursor != null) {
                val count = cursor.count
                cursor.close()

                //We add an additional book
                val values = ContentValues()
                values.put("name", "COLUMN_NAME" + (count + 1))
                values.put("isbn", "COLUMN_ISBN" + (count + 1))
                values.put("description", "COLUMN_DESCRIPTION" + (count + 1))
                val insertUri = contentResolver.insert(CONTENT_URI, values)
                Log.d("ContentProvider", "insertUri:$insertUri")
            }
            contentProviderClient.release()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }
}