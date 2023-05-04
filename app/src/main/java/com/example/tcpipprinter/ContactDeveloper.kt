package com.example.tcpipprinter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tcpipprinter.databinding.ActivityContactDeveloperBinding

class ContactDeveloper : AppCompatActivity() {
    private lateinit var binding:ActivityContactDeveloperBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityContactDeveloperBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}