package com.example.mosquito;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mosquito.model.Notizia;

public class ActivityNotizia extends AppCompatActivity {
    Notizia n;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notizia);
        getSupportActionBar().hide();

        String nlink = getIntent().getStringExtra("notizia");
        for (Notizia n : NotizieFragment.lista)
            if (n.link.equals(nlink)) {
                this.n = n;
                break;
            }

        ((TextView) findViewById(R.id.titolo_notizia_activity)).setText(n.titolo);
        ((TextView) findViewById(R.id.data_notizia_activity)).setText(n.dataString());
        ((TextView) findViewById(R.id.fonte_notizia_activity)).setText(n.f.nome);
        ((Button) findViewById(R.id.visita_pagina_notizia)).setOnClickListener(click -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(n.link))));
        if (n.image != null) {
            ((ImageView) findViewById(R.id.icona_notizia_activity)).setImageBitmap(n.image);
            ((ImageView) findViewById(R.id.icona_notizia_activity)).setVisibility(View.VISIBLE);
        }
        WebView web = ((WebView) findViewById(R.id.corpo_notizia_activity));
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        String stile = "";
        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) stile = "<style>* {background-color: white; color: black;} h1 {font-size: 4em;} h2 {font-size: 3em;} html {font-size: 2.5em;}</style> ";
        else if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) stile = "<style>* {background-color: black; color: white;}</style> ";
        web.getSettings().setUseWideViewPort(true);
        web.getSettings().setLoadWithOverviewMode(true);
        web.loadData(stile + n.desc, "text/html; charset=utf-8", "UTF-8");
    }
}