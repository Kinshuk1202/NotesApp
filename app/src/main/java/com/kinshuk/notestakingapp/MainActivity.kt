package com.kinshuk.notestakingapp

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
    private val CODE_AUTHENTICATION_VERIFICATION = 241
    private lateinit var  sharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViewModel()
        supportActionBar?.setBackgroundDrawable(getDrawable(R.color.actionBarColor))

        sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()){
            if(!sharedPref.contains(getString(R.string.sharedPrefKey)))
            {
                putBoolean(getString(R.string.sharedPrefKey),false)
                apply()
            }
        }
        val enabled = sharedPref.getBoolean(getString(R.string.sharedPrefKey),false)
        if(enabled) {
            binding.et.visibility = View.VISIBLE
            LockScreen()
        }
        else
            binding.et.visibility = View.GONE
    }

    private fun LockScreen() {
        val km = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        if (km.isKeyguardSecure) {
            val i =
                km.createConfirmDeviceCredentialIntent("Authentication required", "Use your password")
            startActivityForResult(i, CODE_AUTHENTICATION_VERIFICATION)
        }
        else {
            Toast.makeText(
                this,
                "Need to setup Phone Lock Screen Authentication to use App Lock",
                Toast.LENGTH_LONG
            ).show()
            sharedPref.edit().putBoolean(getString(R.string.sharedPrefKey),false).apply()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CODE_AUTHENTICATION_VERIFICATION) {
            if (resultCode == RESULT_OK) {
                binding.et.visibility = View.GONE
                Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failure: Unable to verify user's identity", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun setUpViewModel() {
        val noteReop = NoteRepository(NoteDatabase(this))
        val viewModelProviderFactory =  NoteViewModelFactory(application,noteReop)
         noteViewModel = ViewModelProvider(this,viewModelProviderFactory).get(NoteViewModel::class.java)
    }
}