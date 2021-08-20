package com.example.mosquito;
import com.example.mosquito.web.Discover;
import com.example.mosquito.model.Fonte;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NuovaFonteActivity extends androidx.appcompat.app.AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuovafonte);

        Button aggiungi = findViewById(R.id.aggiungiFonte);
        aggiungi.setOnClickListener(click -> {
            String wlink = ((EditText)findViewById(R.id.linkNuovaFonte)).getText().toString();
            new Discover(this).execute(wlink);
        });
    }

    public void controlla(Fonte f) {
        if (f == null) {
            TextView tverrore = findViewById(R.id.errore_discover_fonte);
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(tverrore.getWindowToken(), 0);
            tverrore.setVisibility(View.VISIBLE);
            ValueAnimator anim = new ValueAnimator();
            anim.setIntValues(getResources().getColor(R.color.black), getResources().getColor(R.color.rossoelimina));
            anim.setEvaluator(new ArgbEvaluator());
            anim.addUpdateListener((valueAnimator) -> tverrore.setTextColor((Integer)valueAnimator.getAnimatedValue()));
            anim.setDuration(1000);
            anim.start();
        } else {
            findViewById(R.id.errore_discover_fonte).setVisibility(View.GONE);
            Intent intent = new Intent(NuovaFonteActivity.this, MainActivity.class);
            intent.putExtra("fonte", f);
            setResult(1, intent);
            finish();
        }
    }
}