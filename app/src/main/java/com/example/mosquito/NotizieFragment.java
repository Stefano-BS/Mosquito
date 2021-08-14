package com.example.mosquito;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.example.mosquito.model.Fonte;
import com.example.mosquito.model.Fonti;
import com.example.mosquito.model.Notizia;
import com.example.mosquito.model.Parser;
import java.util.LinkedList;

public class NotizieFragment extends Fragment {
    View root;
    LayoutInflater i;
    public ArrayAdapter<Notizia> adapter;
    public static LinkedList<Notizia> lista = new LinkedList<>();
    public boolean finitoCaricamento = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_notizie, container, false);
        i = inflater;
        generaLista();
        if (savedInstanceState == null || !savedInstanceState.keySet().contains("notizie")) {
            LinkedList<Fonte> fonti = Fonti.getInstance().getFonti();
            new Parser(this).execute(fonti);
        } else {
            lista = (LinkedList<Notizia>) savedInstanceState.get("notizie");
            adapter.notifyDataSetChanged();
            finitoCaricamento = true;
        }
        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (finitoCaricamento) outState.putSerializable("notizie", lista);
        super.onSaveInstanceState(outState);
    }

    public void generaLista() {
        adapter = new ArrayAdapter<Notizia>(root.getContext(), R.layout.notiziainlista) {
            class Holder {
                TextView tvtitolo, tvfonte, tvdata;
                CardView base;
            }

            public int getCount() {
                if (lista != null) return lista.size();
                else return 0;
            }
            public Notizia getItem(int position) {return lista.get(position);}
            public long getItemId(int position) {return position;}

            public View getView(int position, View convertView, ViewGroup parent) {
                Holder h = null;
                View v = convertView;
                if (convertView == null) {
                    v = i.inflate(R.layout.notiziainlista, null, false);
                    h = new Holder();
                    h. tvtitolo =  v.findViewById(R.id.titolonotizia);
                    h.tvfonte = v.findViewById(R.id.nomefontenotizia);
                    h.tvdata = v.findViewById(R.id.datanotizia);
                    h.base = v.findViewById(R.id.gridlayoutnotiziainlista);
                    v.setTag(h);
                }
                else h = (Holder) v.getTag();
                Notizia n = getItem(position);
                if (n == null) return v;
                h.tvtitolo.setText(n.titolo);
                h.tvfonte.setText(n.f.nome);
                h.tvdata.setText(n.dataString());
                h.base.setOnClickListener(click -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(n.link))));
                return v;
            }
        };
        ((ListView)root.findViewById(R.id.listanotizieinfragment)).setAdapter(adapter);
    }
}