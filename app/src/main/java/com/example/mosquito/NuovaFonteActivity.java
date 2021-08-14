package com.example.mosquito;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mosquito.model.Discover;
import com.example.mosquito.model.Fonte;

public class NuovaFonteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nuovafonte_activity);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(false);

        Button aggiungi = findViewById(R.id.aggiungiFonte);
        aggiungi.setOnClickListener(click -> {
            String wlink = ((EditText)findViewById(R.id.linkNuovaFonte)).getText().toString();
            new Discover(this).execute(wlink);
        });
    }

    public void controlla(Fonte f) {
        if (f == null) {
            new AlertDialog.Builder(this).setMessage("Error").show();
        } else {
            Intent intent = new Intent(NuovaFonteActivity.this, MainActivity.class);
            intent.putExtra("fonte", f);
            //String link = ((EditText)findViewById(R.id.linkNuovaFonte)).getText().toString();
            //intent.putExtra("link", link);
            setResult(1, intent);
            finish();
        }
    }
}