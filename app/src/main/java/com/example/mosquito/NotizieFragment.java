package com.example.mosquito;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.mosquito.model.Fonte;
import com.example.mosquito.model.Fonti;
import com.example.mosquito.model.Notizia;
import com.example.mosquito.model.Parser;
import com.google.android.material.snackbar.Snackbar;
import java.util.LinkedList;

public class NotizieFragment extends Fragment {
    View root;
    LayoutInflater i;
    public ArrayAdapter<Notizia> adapter;
    public static LinkedList<Notizia> lista = new LinkedList<>();
    public boolean finitoCaricamento = false;
    LinkedList<Fonte> fontiAttualmenteSelezionate = Fonti.getInstance().getFonti();
    Parser p = new Parser(this);
    public SwipeRefreshLayout swipe;
    Spinner spin;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_notizie, container, false);
        i = inflater;
        generaLista();
        swipe = root.findViewById(R.id.swiperefresh);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {aggiornaContenuti(true);}
        });
        spin = root.findViewById(R.id.spinner);
        generaSpinner();

        if (Fonti.getInstance().getFonti().size() == 0) {
            root.findViewById(R.id.listanotizieinfragment).setVisibility(View.GONE);
            root.findViewById(R.id.select_source_bar).setVisibility(View.GONE);
            root.findViewById(R.id.msg_notiziefragment).setVisibility(View.VISIBLE);
        }
        else if (savedInstanceState == null || !savedInstanceState.keySet().contains("notizie"))
            aggiornaContenuti(false);
        else {
            lista = (LinkedList<Notizia>) savedInstanceState.getSerializable("notizie");
            finitoCaricamento = true;
        }
        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (finitoCaricamento) outState.putSerializable("notizie", lista);
        super.onSaveInstanceState(outState);
    }

    protected void aggiornaContenuti(boolean snackbar) {
        if (Mosquito.internet()) {
            root.findViewById(R.id.listanotizieinfragment).setVisibility(View.VISIBLE);
            root.findViewById(R.id.select_source_bar).setVisibility(View.VISIBLE);
            ((TextView)root.findViewById(R.id.msg_notiziefragment)).setText(R.string.aggiungi_sorgenti_per_iniziare);
            root.findViewById(R.id.msg_notiziefragment).setVisibility(View.GONE);
            if (p.getStatus() == AsyncTask.Status.RUNNING) {
                swipe.setRefreshing(false);
                return;
            }
            p = new Parser(this);
            p.execute(fontiAttualmenteSelezionate);
        }
        else if (snackbar) {
            Snackbar.make(root, getString(R.string.nointernet), Snackbar.LENGTH_LONG).show();
            swipe.setRefreshing(false);
        }
        else {
            root.findViewById(R.id.listanotizieinfragment).setVisibility(View.GONE);
            root.findViewById(R.id.select_source_bar).setVisibility(View.GONE);
            ((TextView)root.findViewById(R.id.msg_notiziefragment)).setText(R.string.nointernet);
            root.findViewById(R.id.msg_notiziefragment).setVisibility(View.VISIBLE);
            swipe.setRefreshing(false);
        }
    }

    private void generaLista() {
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
                    v = i.inflate(R.layout.notiziainlista, parent, false);
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

    private void generaSpinner() {
        LinkedList<Fonte> listaFonti = (LinkedList<Fonte>)Fonti.getInstance().getFonti().clone();
        for (Fonte f : listaFonti)
            f.nome = f.nome.substring(0, Math.min(18, f.nome.length()));
        listaFonti.add(0, new Fonte("#", getString(R.string.all)));
        ArrayAdapter<Fonte> spinAdapter = new ArrayAdapter<Fonte>(root.getContext(), android.R.layout.simple_spinner_item, listaFonti);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(spinAdapter);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Fonte selezionata = (Fonte) parent.getItemAtPosition(position);
                if (selezionata.weblink.equals("#")) {
                    if (fontiAttualmenteSelezionate.size()>1) return;
                    fontiAttualmenteSelezionate = Fonti.getInstance().getFonti();
                    aggiornaContenuti(false);
                }
                else {
                    LinkedList<Fonte> listaNuova = new LinkedList<>();
                    listaNuova.add(selezionata);
                    if (fontiAttualmenteSelezionate.size()>1) {
                        fontiAttualmenteSelezionate = listaNuova;
                        aggiornaContenuti(false);
                    }
                    else if (fontiAttualmenteSelezionate.get(0).weblink.equals(selezionata.weblink)) return;
                    else {
                        fontiAttualmenteSelezionate = listaNuova;
                        aggiornaContenuti(false);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
                fontiAttualmenteSelezionate = Fonti.getInstance().getFonti();
                aggiornaContenuti(false);
            }
        });
    }
}