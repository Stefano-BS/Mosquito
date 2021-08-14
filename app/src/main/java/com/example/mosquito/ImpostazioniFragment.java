package com.example.mosquito;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.mosquito.model.DB;

public class ImpostazioniFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_impostazioni, container, false);
        Switch ascdesc = root.findViewById(R.id.ascdesc);
        ascdesc.setChecked(DB.getInstance().ottieniImpostazione(0).equals("true"));
        ascdesc.setOnCheckedChangeListener((view, checked) -> {
            String nuovaImpo = checked? "true" : "false";//((Switch)container.findViewById(R.id.ascdesc)).isChecked() ? "true" : "false";
            DB.getInstance().scriviImpostazione(0, nuovaImpo);
        });
        return root;
    }
}