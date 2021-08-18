package com.example.mosquito;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mosquito.model.Discover;
import com.example.mosquito.model.Fonte;

public class NuovaFonteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nuovafonte_activity);

        Button aggiungi = findViewById(R.id.aggiungiFonte);
        aggiungi.setOnClickListener(click -> {
            String wlink = ((EditText)findViewById(R.id.linkNuovaFonte)).getText().toString();
            new Discover(this).execute(wlink);
        });
    }

    public void controlla(Fonte f) {
        if (f == null) {
            //new AlertDialog.Builder(this).setMessage("Error").show();
            findViewById(R.id.errore_discover_fonte).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.errore_discover_fonte).setVisibility(View.GONE);
            Intent intent = new Intent(NuovaFonteActivity.this, MainActivity.class);
            intent.putExtra("fonte", f);
            setResult(1, intent);
            finish();
        }
    }
}