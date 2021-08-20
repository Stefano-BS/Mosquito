package com.example.mosquito;
import com.example.mosquito.model.Fonte;
import com.example.mosquito.model.Fonti;

import android.animation.ObjectAnimator;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.util.LinkedList;

public class FontiFragment extends Fragment {
    View root;
    LayoutInflater i;
    ArrayAdapter<Fonte> adapter;
    LinkedList<Fonte> lista = Fonti.getInstance().getFonti();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_fonti, container, false);
        MainActivity.menuToolbar.getItem(0).setVisible(false);
        MainActivity.menuToolbar.getItem(1).setVisible(false);
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
        lista = Fonti.getInstance().getFonti();
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
                    v = i.inflate(R.layout.fonteinlista, parent, false);
                    Fonte f = getItem(position);
                    if (f == null) return v;
                    TextView tvnome =  v.findViewById(R.id.nomefonte);
                    tvnome.setText(f.nome);
                    tvnome.setZ(1);
                    TextView tvlink = v.findViewById(R.id.linkfonte);
                    tvlink.setText(f.weblink);
                    tvlink.setZ(1);
                    Button elimina = v.findViewById(R.id.eliminafonte);
                    elimina.setOnClickListener(click -> {
                        Fonti.getInstance().eliminaFonte(f);
                        generaLista();
                    });
                    Button notifiche = v.findViewById(R.id.attivanotifiche);
                    if (!f.notifiche) notifiche.setText(getString(R.string.attiva_notifiche));
                    else notifiche.setText(getString(R.string.disattiva_notifiche));
                    notifiche.setOnClickListener(click -> {
                        NotificationManager nm = Mosquito.context.getSystemService(NotificationManager.class);
                        if (f.notifiche) { // Rimozione canale e nel DB
                            nm.deleteNotificationChannel(f.weblink);
                            f.notifiche = false;
                            Fonti.getInstance().notificaFonte(f);
                            notifiche.setText(getString(R.string.attiva_notifiche));
                            Mosquito.managerJobNotifiche();
                        } else { // Creazione canale e informo DB
                            NotificationChannel channel = new NotificationChannel(f.weblink, f.nome, NotificationManager.IMPORTANCE_DEFAULT);
                            channel.setDescription("News for " + f.nome);
                            nm.createNotificationChannel(channel);
                            f.notifiche = true;
                            Fonti.getInstance().notificaFonte(f);
                            Intent notificationIntent = new Intent(Mosquito.context, MainActivity.class);
                            PendingIntent contentIntent = PendingIntent.getActivity(Mosquito.context,0,notificationIntent,0);
                            Notification notifica = new Notification.Builder(getContext()).setTicker(f.nome).setContentText(getString(R.string.aggiunta_fonte) + ' ' + f.nome)
                                    .setSmallIcon(R.drawable.mosquito).setWhen(System.currentTimeMillis()).setContentIntent(contentIntent).setChannelId(f.weblink).build();
                            nm.notify(f.weblink.hashCode(), notifica);
                            notifiche.setText(getString(R.string.disattiva_notifiche));
                            Mosquito.managerJobNotifiche();
                        }
                    });
                    View finalV = v;
                    v.setOnClickListener(click -> apriChiudiPannello(finalV));
                }
                return v;
            }
        };
        ((ListView)root.findViewById(R.id.listafontiinfragment)).setAdapter(adapter);
    }

    void apriChiudiPannello (View fonteinlista) {
        LinearLayout pannello = fonteinlista.findViewById(R.id.layoutBottoniFonte);
        pannello.setZ(0);
        if (pannello.getVisibility() != View.VISIBLE) {
            pannello.setVisibility(View.VISIBLE);
            ObjectAnimator van = ObjectAnimator.ofFloat(pannello, "X", -fonteinlista.getWidth(), Mosquito.convertDpToPixel(10));
            van.setDuration(500);
            van.start();
        }
        else pannello.setVisibility(View.GONE);
    }
}