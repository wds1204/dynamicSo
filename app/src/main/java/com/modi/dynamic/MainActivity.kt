package com.modi.dynamic

import android.Manifest.permission
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.modi.dynamic.databinding.ActivityMainBinding
import com.modi.dysolib.DynamicSoHelp
import com.modi.dysolib.utils.AbiUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions(
            arrayOf(
                permission.WRITE_EXTERNAL_STORAGE,
                permission.READ_EXTERNAL_STORAGE
            ), 100
        )
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example of a call to a native method
        binding.button.setOnClickListener {
            DynamicSoHelp.loadSoLibrary("A")
        }

        binding.button1.setOnClickListener {
            binding.sampleText.text = stringAFromJNI()
        }

        binding.button2.setOnClickListener {
            binding.sampleText.text =stringBFromJNI()
        }

        println(AbiUtils.getSupportABIS()?.toList())
    }

    /**
     * A native method that is implemented by the 'dynamic' native library,
     * which is packaged with this application.
     */
    external fun stringAFromJNI(): String

    external fun stringBFromJNI():String


    companion object {
        // Used to load the 'dynamic' library on application startup.
        init {
//            System.loadLibrary("A")
        }
    }
}