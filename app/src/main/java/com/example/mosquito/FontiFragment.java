package com.example.mosquito;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.mosquito.NuovaFonteActivity;
import com.example.mosquito.R;
import com.example.mosquito.model.Fonte;
import com.example.mosquito.model.Fonti;
import com.google.android.material.snackbar.Snackbar;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.zip.Inflater;

public class FontiFragment extends Fragment {
    View root;
    LayoutInflater i;
    ArrayAdapter<Fonte> adapter;
    LinkedList<Fonte> lista = Fonti.getIstance().getFonti();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_fonti, container, false);
        i = inflater;

        Button bottone = root.findViewById(R.id.aggiungiFonte);
        bottone.setOnClickListener(view -> {
            final Intent intent = new Intent(getActivity(), NuovaFonteActivity.class);
            startActivityForResult(intent, 0);
        });

        generaLista();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        lista = Fonti.getIstance().getFonti();
        adapter.notifyDataSetChanged();
    }

    protected void generaLista() {
        adapter = new ArrayAdapter<Fonte>(root.getContext(), R.layout.fonteinlista) {
            public int getCount() {
                if (lista != null) return lista.size();
                else return 0;
            }
            public Fonte getItem(int position) {
                if (lista != null && lista.size()>position) return lista.get(position);
                else return null;
            }
            public long getItemId(int position) {return position;}

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (convertView == null) {
                    //LayoutInflater i = (LayoutInflater) Mosquito.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = i.inflate(R.layout.fonteinlista, null, false);
                    Fonte f = getItem(position);
                    TextView tvnome =  v.findViewById(R.id.nomefonte);
                    tvnome.setText(f.nome);
                    TextView tvlink = v.findViewById(R.id.linkfonte);
                    tvlink.setText(f.weblink);
                    Button elimina = v.findViewById(R.id.eliminafonte);
                    elimina.setOnClickListener(click -> {
                        Fonti.getIstance().eliminaFonte(f);
                        generaLista();
                    });
                }
                return v;
            }
        };
        ((ListView)root.findViewById(R.id.listafontiinfragment)).setAdapter(adapter);
    }
}
//Log.d("ciao", "dati");
//new AlertDialog.Builder(this).setMessage(""+request).show();
//Snackbar.make(getView().findViewById(R.id.aggiungiFonte), link, Snackbar.LENGTH_LONG).setAction("Action", null).show();
//try {startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));}
//catch (Exception e) {}