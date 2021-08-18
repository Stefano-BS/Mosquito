package com.example.mosquito;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.mosquito.model.DB;

public class ImpostazioniFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_impostazioni, container, false);
        MainActivity.menuToolbar.getItem(0).setVisible(false);
        MainActivity.menuToolbar.getItem(1).setVisible(false);

        Switch ascdesc = root.findViewById(R.id.ascdesc);
        ascdesc.setChecked(DB.getInstance().ottieniImpostazione(0).equals("true"));
        ascdesc.setOnCheckedChangeListener((view, checked) -> {
            String nuovaImpo = checked? "true" : "false";//((Switch)container.findViewById(R.id.ascdesc)).isChecked() ? "true" : "false";
            DB.getInstance().scriviImpostazione(0, nuovaImpo);
        });

        RadioButton radioCompatto = root.findViewById(R.id.radioCompatto),
                radioNormale = root.findViewById(R.id.radioNormale),
                radioAmpio = root.findViewById(R.id.radioAmpio);

        String impostato = DB.getInstance().ottieniImpostazione(1);
        if (impostato.equals("compatto")) radioCompatto.setChecked(true);
        else if (impostato.equals("normale")) radioNormale.setChecked(true);
        else if (impostato.equals("ampio")) radioAmpio.setChecked(true);

        radioCompatto.setOnCheckedChangeListener((buttonView, isChecked) -> cambiaStileNotizie(buttonView, isChecked));
        radioNormale.setOnCheckedChangeListener((buttonView, isChecked) -> cambiaStileNotizie(buttonView, isChecked));
        radioAmpio.setOnCheckedChangeListener((buttonView, isChecked) -> cambiaStileNotizie(buttonView, isChecked));
        return root;
    }

    private void cambiaStileNotizie(View buttonView, boolean isChecked) {
        if (isChecked) {
            if (buttonView.getId() == R.id.radioCompatto) DB.getInstance().scriviImpostazione(1, "compatto");
            else if (buttonView.getId() == R.id.radioNormale) DB.getInstance().scriviImpostazione(1, "normale");
            else if (buttonView.getId() == R.id.radioAmpio) DB.getInstance().scriviImpostazione(1, "ampio");
        }
    }
}