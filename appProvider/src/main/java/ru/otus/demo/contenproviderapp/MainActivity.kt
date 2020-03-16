package ru.otus.demo.contenproviderapp

import android.content.ContentValues
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
        fab.setOnClickListener {
            val projection = arrayOf(
                    BookTable.COLUMN_ID,
                    BookTable.COLUMN_NAME,
                    BookTable.COLUMN_ISBN,
                    BookTable.COLUMN_DESCRIPTION)
            val cursor = contentResolver.query(BookContentProvider.CONTENT_URI, projection, null, null,
                    null)
            if (cursor != null) {
                val count = cursor.count
                val values = ContentValues()
                values.put(BookTable.COLUMN_NAME, "COLUMN_NAME" + (count + 1))
                values.put(BookTable.COLUMN_ISBN, "COLUMN_ISBN" + (count + 1))
                values.put(BookTable.COLUMN_DESCRIPTION, "COLUMN_DESCRIPTION" + (count + 1))
                contentResolver.insert(
                        BookContentProvider.CONTENT_URI, values)
                cursor.close()
            }
        }
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