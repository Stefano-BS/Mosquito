package com.example.mosquito;
import com.example.mosquito.model.*;
import com.example.mosquito.web.ImgDownloader;
import com.example.mosquito.web.Parser;

import com.google.android.material.snackbar.Snackbar;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

public class NotizieFragment extends Fragment {
    public static LinkedList<Notizia> lista = new LinkedList<>();
    LinkedList<Fonte> fontiAttualmenteSelezionate = Fonti.getInstance().getFonti();
    public ArrayAdapter<Notizia> adapter;
    public static final HashMap<String, Bitmap> catalogo = new HashMap<>();
    public static final LinkedList<ImgDownloader> imdl = new LinkedList<>();
    public boolean visualizzaLette = false;

    Parser p = new Parser(this);
    public boolean finitoCaricamento = false;

    View root;
    LayoutInflater i;
    public SwipeRefreshLayout swipe;
    Spinner spin;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_notizie, container, false);
        if (MainActivity.menuToolbar != null) {
            MainActivity.menuToolbar.getItem(0).setVisible(true);
            MainActivity.menuToolbar.getItem(1).setVisible(true);
        }
        i = inflater;

        adapter = new ArrayAdapter<Notizia>(root.getContext(), stileNotizia()) {
            class Holder {
                TextView tvtitolo, tvfonte, tvdata;
                ImageView icona;
                CardView base;
            }

            public int getCount() {
                if (lista != null) return lista.size();
                else return 0;
            }
            public Notizia getItem(int position) {return lista.get(position);}
            public long getItemId(int position) {return position;}

            @SuppressLint("ClickableViewAccessibility")
            public View getView(int position, View convertView, ViewGroup parent) {
                Holder h;
                View v = convertView;
                Notizia n = getItem(position);
                int stile = stileNotizia();
                if (convertView == null) {
                    v = i.inflate(stile, parent, false);
                    h = new Holder();

                    if (stile == R.layout.notiziainlista_amp) h.icona = v.findViewById(R.id.icona_notizia_amp);
                    h.tvtitolo = v.findViewById(R.id.titolonotizia);
                    h.tvfonte = v.findViewById(R.id.nomefontenotizia);
                    h.tvdata = v.findViewById(R.id.datanotizia);
                    h.base = v.findViewById(R.id.gridlayoutnotiziainlista);
                    v.setTag(h);
                }
                else h = (Holder) v.getTag();
                if (n == null) return v;
                h.tvtitolo.setText(n.titolo);
                h.tvfonte.setText(n.f.nome);
                h.tvdata.setText(n.dataString());

                int coloreTesto = n.letta? getResources().getColor(R.color.testosubt) : getResources().getColor(R.color.testo);
                int coloreTestosub = n.letta? getResources().getColor(R.color.testosubt2) : getResources().getColor(R.color.testosubt);
                h.tvtitolo.setTextColor(coloreTesto);
                h.tvdata.setTextColor(coloreTestosub);
                h.tvfonte.setTextColor(coloreTestosub);

                View finalV = v; // Vars referred in an anonymous class must be final or effecitvely final
                h.base.setOnTouchListener(new OnTouchListener(){
                    final GestureDetector gestureDetector = new GestureDetector(new SimpleOnGestureListener() {
                        private static final int SWIPE_MIN_DISTANCE = 100;
                        private static final int SWIPE_MAX_OFF_PATH = 250;
                        private static final int SWIPE_THRESHOLD_VELOCITY = 80;

                        @Override
                        public boolean onSingleTapConfirmed(MotionEvent e) {
                            Intent intent = new Intent(root.getContext(), ActivityNotizia.class);
                            intent.putExtra("notizia", n.link);
                            startActivity(intent);
                            return super.onSingleTapConfirmed(e);
                        }

                        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                            double dist = Math.sqrt(Math.pow(e1.getX()-e2.getX(), 2) + Math.pow(e1.getY()-e2.getY(), 2));
                            //if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) return false;
                            if (dist > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                if (n.letta) DB.getInstance().marcaNonLetta(n.link);
                                else DB.getInstance().marcaLetta(n.link);
                                n.letta = !n.letta;
                                adapter.notifyDataSetChanged();
                                float finoA = -finalV.getWidth()/3*2;
                                if (e2.getX() > e1.getX()) finoA = -finoA;
                                ObjectAnimator van = ObjectAnimator.ofFloat(finalV, "X", 0, finoA, 0);
                                van.setDuration(500);
                                van.start();
                            }
                            return super.onFling(e1, e2, velocityX, velocityY);
                        }
                    });

                    public boolean onTouch(View v, MotionEvent event) {
                        return !gestureDetector.onTouchEvent(event);
                    }
                });

                if (h.icona != null) {
                    if (catalogo.containsKey(n.imgSrc)) {
                        h.icona.setImageBitmap(catalogo.get(n.imgSrc));
                        h.icona.setVisibility(View.VISIBLE);
                    }
                    else if (n.imgSrc != null) {
                        h.icona.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ptr));
                        h.icona.setVisibility(View.VISIBLE);
                    }
                    else h.icona.setVisibility(View.GONE);
                }
                return v;
            }
        };
        ((ListView)root.findViewById(R.id.listanotizieinfragment)).setAdapter(adapter);

        swipe = root.findViewById(R.id.swiperefresh);
        swipe.setOnRefreshListener(() -> aggiornaContenuti(true, visualizzaLette));
        spin = root.findViewById(R.id.spinner);
        generaSpinner();

        if (Fonti.getInstance().getFonti().size() == 0) {
            root.findViewById(R.id.listanotizieinfragment).setVisibility(View.GONE);
            root.findViewById(R.id.select_source_bar).setVisibility(View.GONE);
            root.findViewById(R.id.msg_notiziefragment).setVisibility(View.VISIBLE);
        }
        else if (savedInstanceState == null || !savedInstanceState.keySet().contains("notizie"))
            aggiornaContenuti(false, visualizzaLette);
        else {
            Serializable bundle = savedInstanceState.getSerializable("notizie");
            if (bundle instanceof LinkedList) {
                lista = (LinkedList) bundle;
                finitoCaricamento = true;
            }
            else { // boh
                aggiornaContenuti(false, visualizzaLette);
            }
        }
        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (finitoCaricamento) outState.putSerializable("notizie", lista);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        if (p != null && p.getStatus() == AsyncTask.Status.RUNNING) p.cancel(true);
        for (ImgDownloader imd : imdl)
            if (imd.getStatus() == AsyncTask.Status.RUNNING) imd.cancel(true);
        super.onDestroy();
    }

    protected void aggiornaContenuti(boolean snackbar, boolean visualizzaLette) {
        this.visualizzaLette = visualizzaLette;
        if (Mosquito.internet()) {
            root.findViewById(R.id.listanotizieinfragment).setVisibility(View.VISIBLE);
            root.findViewById(R.id.select_source_bar).setVisibility(View.VISIBLE);
            ((TextView)root.findViewById(R.id.msg_notiziefragment)).setText(R.string.aggiungi_sorgenti_per_iniziare);
            root.findViewById(R.id.msg_notiziefragment).setVisibility(View.GONE);
            if (p.getStatus() == AsyncTask.Status.RUNNING) {
                //if (imdl != null) imdl.cancel(true);
                p.cancel(true);
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

    private void generaSpinner() {
        LinkedList<Fonte> listaFonti = (LinkedList<Fonte>)Fonti.getInstance().getFonti().clone();
        listaFonti.add(0, new Fonte("#", getString(R.string.all), false));
        ArrayAdapter<Fonte> spinAdapter = new ArrayAdapter<>(root.getContext(), android.R.layout.simple_spinner_item, listaFonti);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(spinAdapter);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Fonte selezionata = (Fonte) parent.getItemAtPosition(position);
                if (selezionata.weblink.equals("#")) {
                    if (fontiAttualmenteSelezionate.size()>1) return;
                    fontiAttualmenteSelezionate = Fonti.getInstance().getFonti();
                    aggiornaContenuti(false, visualizzaLette);
                }
                else {
                    LinkedList<Fonte> listaNuova = new LinkedList<>();
                    listaNuova.add(selezionata);
                    if (fontiAttualmenteSelezionate.size()>1) {
                        fontiAttualmenteSelezionate = listaNuova;
                        aggiornaContenuti(false, visualizzaLette);
                    }
                    else if (fontiAttualmenteSelezionate.get(0).weblink.equals(selezionata.weblink)) return;
                    else {
                        fontiAttualmenteSelezionate = listaNuova;
                        aggiornaContenuti(false, visualizzaLette);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
                fontiAttualmenteSelezionate = Fonti.getInstance().getFonti();
                aggiornaContenuti(false, visualizzaLette);
            }
        });
    }

    private int stileNotizia(){
        String impostato = DB.getInstance().ottieniImpostazione(1);
        if (impostato == null) return R.layout.notiziainlista_mid;
        switch (impostato) {
            case "compatto": return R.layout.notiziainlista_compact;
            case "normale": return R.layout.notiziainlista_mid;
            case "ampio": return R.layout.notiziainlista_amp;
            default: return R.layout.notiziainlista_mid;
        }
    }
}