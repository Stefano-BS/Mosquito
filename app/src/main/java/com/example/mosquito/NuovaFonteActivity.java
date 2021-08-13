package com.example.mosquito;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.snackbar.Snackbar;

public class NuovaFonteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nuovafonte_activity);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        Button aggiungi = findViewById(R.id.aggiungiFonte);
        aggiungi.setOnClickListener(click -> {
            Intent intent = new Intent(NuovaFonteActivity.this, MainActivity.class);
            String link = ((EditText)findViewById(R.id.linkNuovaFonte)).getText().toString();
            intent.putExtra("link", link);
            setResult(1, intent);
            finish();
        });
    }
}