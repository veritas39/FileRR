package com.example.filerr

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File

class DetailNoteActivity : AppCompatActivity() {

    private lateinit var updateButton: Button
    private var filePath: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_note)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val fileName = intent.getStringExtra("FILE_NAME")
        val filePath = intent.getStringExtra("FILE_PATH")

        val txtfileName: EditText = findViewById(R.id.txtFileName)
        val txtfileContent: EditText = findViewById(R.id.txtNotes)
        updateButton = findViewById(R.id.updateBtn)

        txtfileName.setText(fileName)

        val file = filePath?.let { File(it) }
        if (file != null) {
            if (file.exists()) {
                val content = file.readText()
                txtfileContent.setText(content)
            }
        }

        updateButton.setOnClickListener {
            updateFileContent()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateFileContent() {
        val txtfileName: EditText = findViewById(R.id.txtFileName)
        val txtfileContent: EditText = findViewById(R.id.txtNotes)
        val filePath = intent.getStringExtra("FILE_PATH")

        val newFileName = txtfileName.text.toString().trim()
        if (newFileName.isEmpty()) {
            Toast.makeText(this, "File name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val file = File(filePath ?: "")
        if (file.exists()) {
            try {
                val newContent = txtfileContent.text.toString()
                val newFile = File(file.parent, newFileName)
                if (file.renameTo(newFile)) {
                    newFile.writeText(newContent)
                    Toast.makeText(this, "File saved successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to rename file", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "File does not exist" , Toast.LENGTH_SHORT).show()
        }
    }
}