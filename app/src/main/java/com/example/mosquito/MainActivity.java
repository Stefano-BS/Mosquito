package com.example.mosquito;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.example.mosquito.model.Fonte;
import com.example.mosquito.model.Fonti;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*ExtendedFloatingActionButton fab = findViewById(R.id.aggiungiFonti);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_notizie, R.id.nav_fonti, R.id.nav_impostazioni).setDrawerLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    protected void onActivityResult(int request, int result, Intent dati) {
        super.onActivityResult(request, result, dati);
        if (result == 1) {
            Fonte f = (Fonte) dati.getSerializableExtra("fonte");
            new AlertDialog.Builder(this).setMessage(f.nome + f.weblink).show();
            //String link = dati.getStringExtra("link");
            //if (link != null) Fonti.getIstance().aggiungiFonte(new Fonte(link, "Anonimo"));
        }
    }
}
//int id=0;
//for (Fragment f : getSupportFragmentManager().getFragments())
/*    id = f.getId();*///new AlertDialog.Builder(this).setMessage("tag " + f.getTag() + " id " + f.getId()).setTitle("").show();
//FontiFragment fr = getFragmentManager().findFragmentById(R.id.frammentoNotizie);
//new AlertDialog.Builder(this).setMessage(mAppBarConfiguration.getDrawerLayout().getChildAt(1)).show();
//FontiFragment fr = (FontiFragment) getSupportFragmentManager().findFragmentById(R.id.frammentoNotizie);
//String tag = "android:switcher:" + R.id.nav_view + ":" + 1;
//FontiFragment fr = (FontiFragment) getSupportFragmentManager().getFragments().get(0);
//new AlertDialog.Builder(this).setMessage(getFragmentManager().findFragmentById(R.id.frag_fonti).getClass().toString()).show();