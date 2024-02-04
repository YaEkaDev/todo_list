package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.example.todolist.databinding.ActivityCreateNoteBinding;

public class CreateNoteActivity extends AppCompatActivity {

    private ActivityCreateNoteBinding binding;
    private CreateNoteViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(CreateNoteViewModel.class);
        viewModel.getShoudCloseScreen().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean shoudclose) {
                if(shoudclose){
                    finish();
                }
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });

    }

    private void saveNote(){
        String text = "";
        if(binding.edNote.getText().toString().isEmpty()){
            Toast.makeText(CreateNoteActivity.this, R.string.error_empty_note, Toast.LENGTH_SHORT).show();
        } else {
            text = binding.edNote.getText().toString().trim();
            int priority = getPriority();
            Note note = new Note(text, priority);
            viewModel.saveNote(note);

        }
    }

    private int getPriority(){
        int priority;
        if(binding.rbLow.isChecked()){
            priority = 0;
        } else if (binding.rbMedium.isChecked()) {
            priority = 1;
        } else {
            priority = 2;
        }
        return priority;
    }

    public static Intent newIntent(Context context){
        return new Intent(context, CreateNoteActivity.class);

    }

}