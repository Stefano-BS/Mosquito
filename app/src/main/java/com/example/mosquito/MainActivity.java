package com.example.mosquito;
import com.example.mosquito.model.Fonte;
import com.example.mosquito.model.Fonti;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import com.google.android.material.navigation.NavigationView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    public static Menu menuToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_notizie, R.id.nav_fonti, R.id.nav_impostazioni).setDrawerLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // CODE BASED ON SERVICES, NOT WORKING IN BACKGROUND SINCE ANDROID 8 LIMITATIONS (https://developer.android.com/about/versions/oreo/background#services)
        /*ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {  // Notifiche Service alredy running
            new AlertDialog.Builder(this).setMessage(service.getClass().getName()).show();
            if (NotificheService.class.getName().equals(service.service.getClassName())) return;
        }
        stopService(new Intent(this, NotificheService.class));
        Intent intent =  new Intent(this, NotificheService.class);
        LinkedList<Fonte> listaNotificabili = new LinkedList<>();
        for (Fonte f : Fonti.getInstance().getFonti())
            if (f.notifiche) listaNotificabili.add(f);
        intent.putExtra("fonti", listaNotificabili);
        startService(intent);*/

        // CODE BASED ON JOBSERVICES
        Mosquito.managerJobNotifiche();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuToolbar = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        menu.getItem(0).setOnMenuItemClickListener(click -> visualizzaNonLette(true));
        menu.getItem(1).setOnMenuItemClickListener(click -> visualizzaNonLette(false));
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
            Fonti.getInstance().aggiungiFonte(f);
        }
    }

    public boolean visualizzaNonLette(boolean visualizza) {
        if (visualizza) menuToolbar.getItem(0).setChecked(true);
        else menuToolbar.getItem(0).setChecked(false);
        if (visualizza) menuToolbar.getItem(1).setChecked(false);
        else menuToolbar.getItem(1).setChecked(true);
        NotizieFragment frag = (NotizieFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment).getChildFragmentManager().getFragments().get(0);
        if (frag.visualizzaLette != visualizza) frag.aggiornaContenuti(true, visualizza);
        return true;
    }
}