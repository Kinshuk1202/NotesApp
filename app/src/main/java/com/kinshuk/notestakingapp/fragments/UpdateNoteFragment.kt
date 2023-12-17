package com.kinshuk.notestakingapp.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.room.Update
import com.kinshuk.notestakingapp.MainActivity
import com.kinshuk.notestakingapp.R
import com.kinshuk.notestakingapp.adapter.NoteAdapter
import com.kinshuk.notestakingapp.databinding.FragmentNewNoteBinding
import com.kinshuk.notestakingapp.databinding.FragmentUpdateNoteBinding
import com.kinshuk.notestakingapp.model.Note
import com.kinshuk.notestakingapp.viewmodel.NoteViewModel

class UpdateNoteFragment : Fragment(R.layout.fragment_update_note) {
    private var _binding : FragmentUpdateNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var notesViewModel: NoteViewModel
    private lateinit var currentNote:Note

    //since note update note fragments contains arguments in nav_graph
    private val args:UpdateNoteFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateNoteBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesViewModel = (activity as MainActivity).noteViewModel
        currentNote = args.note!!

        binding.etTitleUpdate.setText(currentNote.noteTitle)
        binding.etBodyUpdate.setText(currentNote.noteBody)

        //update
        binding.Donebtn.setOnClickListener{
            val title = binding.etTitleUpdate.text.toString().trim()
            val body = binding.etBodyUpdate.text.toString().trim()

            if(title.isNotEmpty())
            {
                val note = Note(currentNote.id,title,body)
                notesViewModel.updateNote(note)

                view.findNavController().navigate(R.id.action_updateNoteFragment_to_homeFragment)
            }
            else
            {
                Toast.makeText(context,"Please Enter Note Title", Toast.LENGTH_LONG).show()
            }
        }

    }
    private fun delNote(){
            AlertDialog.Builder(activity).apply {
                setTitle("Delete Note")
                setMessage("Are you sure you want to delete this note?")
                setPositiveButton("DELETE"){_,_ ->
                    notesViewModel.deleteNote(currentNote)
                    view?.findNavController()?.navigate(R.id.action_updateNoteFragment_to_homeFragment)
                }
                setNegativeButton("CANCEL",null)
            }.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_note_update,menu)
        super.onCreateOptionsMenu(menu, inflater)

        val menuItem1 = menu.findItem(R.id.menu_share_update)
        val menuItem2 = menu.findItem(R.id.menu_del)

        if(Build.VERSION.SDK_INT >= 26) {
            if (menuItem1 != null && menuItem2!=null) {
                menuItem1.iconTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.white)
                menuItem2.iconTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.white)
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_del-> {
                delNote()
            }
            R.id.menu_share_update->{
                shareNote()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun shareNote() {
        val noteTitle = binding.etTitleUpdate.text.toString().trim()
        val noteBody = binding.etBodyUpdate.text.toString().trim()
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