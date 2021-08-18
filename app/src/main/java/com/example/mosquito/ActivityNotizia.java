package com.example.mosquito;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.mosquito.model.DB;
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
                DB.getInstance().marcaLetta(n.link);
                n.letta = true;
                break;
            }

        ((TextView) findViewById(R.id.titolo_notizia_activity)).setText(n.titolo);
        ((TextView) findViewById(R.id.data_notizia_activity)).setText(n.dataString());
        ((TextView) findViewById(R.id.fonte_notizia_activity)).setText(n.f.nome);
        /*if (NotizieFragment.catalogo.containsKey(n.imgSrc)) {
            ((ImageView) findViewById(R.id.icona_notizia_activity)).setImageBitmap(NotizieFragment.catalogo.get(n.imgSrc));
            findViewById(R.id.icona_notizia_activity).setVisibility(View.VISIBLE);
        }*/
        Intent intentBrowser;
        if (DB.getInstance().ottieniImpostazione(2).equals("inapp")) {
            intentBrowser = new Intent(ActivityNotizia.this, InAppBrowser.class);
            intentBrowser.putExtra("link", n.link);
        }
        else intentBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(n.link));
        findViewById(R.id.visita_pagina_notizia).setOnClickListener(click -> startActivity(intentBrowser));

        findViewById(R.id.condividi_notizia).setOnClickListener(click -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, n.link);
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, n.titolo);
            startActivity(Intent.createChooser(intent, "Share"));
        });

        WebView web = findViewById(R.id.corpo_notizia_activity);
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        String stile = "";
        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
            stile = "<style>* {background-color: white; color: black;} h1 {font-size: 3em;} h2 {font-size: 2.8em;} html {font-size: 2.5em;}</style> ";
            findViewById(R.id.scrollview_notizia_activity).setBackgroundColor(getColor(R.color.white));
        }
        else if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            stile = "<style>* {background-color: black; color: white;} h1 {font-size: 3em;} h2 {font-size: 2.8em;} html {font-size: 2.5em;}</style> ";
            findViewById(R.id.scrollview_notizia_activity).setBackgroundColor(getColor(R.color.black));
        }
        web.getSettings().setUseWideViewPort(true);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setAllowContentAccess(false);
        web.setOnTouchListener((v, event) -> true);
        web.loadData(stile + n.desc, "text/html; charset=utf-8", "UTF-8");
    }
}