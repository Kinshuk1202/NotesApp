package com.kinshuk.notestakingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.kinshuk.notestakingapp.database.NoteDatabase
import com.kinshuk.notestakingapp.databinding.ActivityMainBinding
import com.kinshuk.notestakingapp.repositoy.NoteRepository
import com.kinshuk.notestakingapp.viewmodel.NoteViewModel
import com.kinshuk.notestakingapp.viewmodel.NoteViewModelFactory

class MainActivity : AppCompatActivity() {
    lateinit var noteViewModel:NoteViewModel
    lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViewModel()

        supportActionBar?.setBackgroundDrawable(getDrawable(R.color.actionBarColor))
    }

    private fun setUpViewModel() {
        val noteReop = NoteRepository(NoteDatabase(this))
        val viewModelProviderFactory =  NoteViewModelFactory(application,noteReop)
         noteViewModel = ViewModelProvider(this,viewModelProviderFactory).get(NoteViewModel::class.java)
    }
}