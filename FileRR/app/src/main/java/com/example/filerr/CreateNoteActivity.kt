package com.example.filerr

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CreateNoteActivity : AppCompatActivity() {

    private lateinit var fileNameEditText: EditText
    private lateinit var notesEditText: EditText
    private lateinit var saveButton: Button

    companion object {
        private const val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_note)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        fileNameEditText = findViewById(R.id.txtFileName)
        notesEditText = findViewById(R.id.txtNotes)
        saveButton = findViewById(R.id.saveBtn)

        saveButton.setOnClickListener {
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
            if (saveFile()) {
                Toast.makeText(this, "File has been Created", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "External storage is not writable", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkPermissionAndSaveFile() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE_WRITE_EXTERNAL_STORAGE
            )
        } else {
            saveFile()
        }
    }

    private fun saveFile(): Boolean {
        val fileName = fileNameEditText.text.toString()
        val notes = notesEditText.text.toString()

        if (fileName.isBlank() || notes.isBlank()) {
            Toast.makeText(this, "File name or notes cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }

        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "$fileName.txt")

        return try {
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(notes.toByteArray())
            fileOutputStream.close()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_WRITE_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    saveFile()
                } else {
                    Toast.makeText(this, "Permission denied to write external storage", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
}