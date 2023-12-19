package com.kinshuk.notestakingapp.fragments

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.view.SupportActionModeWrapper
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kinshuk.notestakingapp.MainActivity
import com.kinshuk.notestakingapp.R
import com.kinshuk.notestakingapp.adapter.NoteAdapter
import com.kinshuk.notestakingapp.databinding.FragmentHomeBinding
import com.kinshuk.notestakingapp.model.Note
import com.kinshuk.notestakingapp.viewmodel.NoteViewModel
import kotlin.math.log


class HomeFragment : Fragment(R.layout.fragment_home)  , SearchView.OnQueryTextListener{

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var notesAdapter: NoteAdapter

    private val CODE_AUTHENTICATION_VERIFICATION = 241
    private lateinit var  sharedPref: SharedPreferences

    private lateinit var menu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesViewModel = (activity as MainActivity).noteViewModel

        sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE) ?: return
        setUpRecyclerVeiw()
        binding.addbtn.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeFragment_to_newNoteFragment)
        }
    }

    private fun setUpRecyclerVeiw() {
        notesAdapter = NoteAdapter()

        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(
                2,StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = notesAdapter
        }
        activity?.let {
            notesViewModel.getAllNotes().observe(
                viewLifecycleOwner, {
                    note->notesAdapter.differ.submitList(note)
                    updateUI(note)
                }
            )
        }
    }

    private fun updateUI(note: List<Note>?) {

        if (note != null) {
            if(note.isNotEmpty()){
                binding.cardView.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
            else
            {
                binding.cardView.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        menu.clear()
        inflater.inflate(R.menu.home_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
        val mMenuSearch = menu.findItem(R.id.menu_search).actionView as SearchView
        mMenuSearch.isSubmitButtonEnabled = false
        mMenuSearch.setOnQueryTextListener(this)
        this.menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val enabled =  sharedPref.getBoolean(getString(R.string.sharedPrefKey),false)
        when(item.itemId){
            R.id.toggle_lock->{
                LockScreen()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onQueryTextSubmit(p0: String?): Boolean {
//        searchNote(p0!!)
        return false
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        if(p0!=null){
            searchNote(p0)
        }
        return true
    }

    private fun searchNote(p0: String) {
            val searchQuery = "%$p0"
            notesViewModel.searchNotes(searchQuery).observe(
                this,
                {
                    list->notesAdapter.differ.submitList(list)
                }
            )
    }

    override fun onResume() {
        super.onResume()

        // Override onBackPressed callback to finish the activity when the back button is pressed
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun LockScreen() {
        val km = requireActivity().getSystemService(AppCompatActivity.KEYGUARD_SERVICE) as KeyguardManager
        if (km.isKeyguardSecure) {
            val enabled = sharedPref.getBoolean(getString(R.string.sharedPrefKey),false)
            var desc:String
            if(enabled)
                desc = "Use your password to disable App Lock"
            else
                desc = "Use your password to enable App Lock"
            val i =
                km.createConfirmDeviceCredentialIntent("Authentication required", desc)
            startActivityForResult(i, CODE_AUTHENTICATION_VERIFICATION)
        }
        else {
            Toast.makeText(
                context,
                "Need to setup Phone Lock Screen Authentication to use App Lock",
                Toast.LENGTH_LONG
            ).show()
            sharedPref.edit().putBoolean(getString(R.string.sharedPrefKey),false).apply()
        }
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val enabled = sharedPref.getBoolean(getString(R.string.sharedPrefKey),false)
        val item = menu.findItem(R.id.toggle_lock)
        item.title = if (enabled) "Disable App Lock" else "Enable App Lock"
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == CODE_AUTHENTICATION_VERIFICATION) {
            val enabled =  sharedPref.getBoolean(getString(R.string.sharedPrefKey),false)
            val editor = sharedPref.edit()
            var item = menu.findItem(R.id.toggle_lock)
            editor.putBoolean(getString(R.string.sharedPrefKey),!enabled).apply()

            if(enabled) {
                item.title = "Disable App Lock"
                Toast.makeText(context, "App Lock Disabled", Toast.LENGTH_LONG).show()
            }
            else {
                item.title = "Enable App Lock"
                Toast.makeText(context, "App Lock Enabled", Toast.LENGTH_LONG).show()
            }
            activity?.invalidateOptionsMenu()
            activity?.invalidateMenu()
        }
        else {
            Toast.makeText(context, "Failure: Unable to verify user's identity", Toast.LENGTH_SHORT)
                .show()
        }
    }

}