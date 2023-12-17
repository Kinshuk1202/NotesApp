package com.kinshuk.notestakingapp.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.kinshuk.notestakingapp.MainActivity
import com.kinshuk.notestakingapp.R
import com.kinshuk.notestakingapp.adapter.NoteAdapter
import com.kinshuk.notestakingapp.databinding.FragmentNewNoteBinding
import com.kinshuk.notestakingapp.model.Note
import com.kinshuk.notestakingapp.viewmodel.NoteViewModel


class NewNoteFragment : Fragment(R.layout.fragment_new_note) {
    private var _binding : FragmentNewNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var notesViewModel: NoteViewModel
    private lateinit var notesAdapter: NoteAdapter
    private lateinit var mView:View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewNoteBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesViewModel = (activity as MainActivity).noteViewModel
        mView = view


    }

    private fun saveNote(view:View){
        val noteTitle = binding.etTitle.text.toString().trim()
        val noteBody = binding.etBody.text.toString().trim()
        if(noteTitle.isNotEmpty())
        {
            val note = Note(0,noteTitle,noteBody)

            notesViewModel.addNote(note)
            Toast.makeText(context,"Note Saved Sussessfully!!", Toast.LENGTH_LONG).show()

            view.findNavController().navigate(R.id.action_newNoteFragment_to_homeFragment)
        }
        else{
            Toast.makeText(context,"Please Enter Note Title",Toast.LENGTH_LONG).show()
        }
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_new_note,menu)
        super.onCreateOptionsMenu(menu, inflater)
        val menuItem = menu.findItem(R.id.menu_share_new)

        if(Build.VERSION.SDK_INT >= 26) {
            if (menuItem != null) {
                menuItem.iconTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.white)
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_save-> {
                saveNote(mView)
            }
            R.id.menu_share_new ->{
                shareNote()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun shareNote() {
        val noteTitle = binding.etTitle.text.toString().trim()
        val noteBody = binding.etBody.text.toString().trim()
        val content = "$noteTitle\n\n$noteBody"
        if(noteTitle.isNotEmpty() && noteBody.isNotEmpty())
        {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT,content)
            val shareIntent = Intent.createChooser(intent, "Share Note via...")
            startActivity(shareIntent)
        }
        else
        {
            Toast.makeText(context,"Can't send empty title or body!",Toast.LENGTH_LONG).show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}