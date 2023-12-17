package com.kinshuk.notestakingapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.view.SupportActionModeWrapper
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kinshuk.notestakingapp.MainActivity
import com.kinshuk.notestakingapp.R
import com.kinshuk.notestakingapp.adapter.NoteAdapter
import com.kinshuk.notestakingapp.databinding.FragmentHomeBinding
import com.kinshuk.notestakingapp.model.Note
import com.kinshuk.notestakingapp.viewmodel.NoteViewModel


class HomeFragment : Fragment(R.layout.fragment_home)  , SearchView.OnQueryTextListener{

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var notesAdapter: NoteAdapter

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

}