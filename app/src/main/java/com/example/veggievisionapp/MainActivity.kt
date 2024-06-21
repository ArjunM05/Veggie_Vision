package com.example.veggievisionapp

import android.util.Log
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.veggievisionapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var editTextOption: EditText
    private lateinit var buttonAddOption: Button
    private lateinit var radioGroupOptions: RadioGroup
    private var REQUEST_CAMERA_PERMISSION = 1
    private var REQUEST_IMAGE_CAPTURE = 2
    private var REQUEST_UPLOAD_PERMISSION = 3
    private lateinit var imageView: ImageView
    private lateinit var btnCaptureImage: Button
    private lateinit var btnUploadImage: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize views
        editTextOption = binding.etOption
        buttonAddOption = binding.btnAddOption
        radioGroupOptions = binding.radioGroupOptions
        // Set onClickListener for the button
        buttonAddOption.setOnClickListener { addOption() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        imageView = findViewById(R.id.imageView)

        val captureButton: Button = binding.btnCaptureImage
        captureButton.setOnClickListener { captureImage(it) }

        val uploadButton: Button = binding.btnUploadImage
        uploadButton.setOnClickListener{ uploadImage(it) }
    }

    private fun addOption() {
        val optionText = binding.etOption.text.toString().trim()
        if (optionText.isNotEmpty()) {
            val radioButton = RadioButton(this).apply {
                text = optionText
                id = View.generateViewId()
            }
            binding.radioGroupOptions.addView(radioButton)
            binding.etOption.text.clear()
        }

    }

    private fun captureImage(view: View) {
        // Check for permission at runtime
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            return
        }
        // Open the camera app
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }
    private fun uploadImage(view: View){
        val intent = Intent()
        intent.action=Intent.ACTION_GET_CONTENT
        intent.type="image/*"
        startActivityForResult(intent,REQUEST_UPLOAD_PERMISSION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                captureImage(View(this))  // Pass a dummy view, because the actual view is not needed here
            } else {
                // Permission denied
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val extras = data?.extras
            val imageBitmap = extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)
        }
        if(requestCode==REQUEST_UPLOAD_PERMISSION){
            imageView.setImageURI(data?.data)
        }
    }
}
