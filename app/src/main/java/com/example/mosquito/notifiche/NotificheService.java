package com.example.mosquito.notifiche;
import com.example.mosquito.ActivityNotizia;
import com.example.mosquito.Mosquito;
import com.example.mosquito.R;
import com.example.mosquito.web.Unescape;
import com.example.mosquito.model.*;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class NotificheService extends Service {
    private static LinkedList<Fonte> fonti;
    private static boolean isRunning = false;
    Timer t;

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (isRunning) return Service.START_NOT_STICKY;
        Serializable l = intent.getSerializableExtra("fonti");
        if (l instanceof LinkedList) fonti = (LinkedList) l;
        else if (l instanceof ArrayList) {
            ArrayList<Fonte> arr = (ArrayList) l;
            fonti = new LinkedList<>();
            for (Fonte f: arr)
                fonti.add(f);
        }
        else return Service.START_NOT_STICKY;
        isRunning = true;
        t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                //new Parser(NotificheService.this).execute(fonti);
                LinkedList<Notizia> notizie = new LinkedList<Notizia>();
                for (Fonte f : fonti) {
                    HttpURLConnection connection = null;
                    InputStream input = null;
                    try {
                        URL url = new URL(f.weblink);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setConnectTimeout(1000);
                        connection.connect();
                        input = connection.getInputStream();
                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        DocumentBuilder db = dbf.newDocumentBuilder();
                        Document doc = db.parse(input);
                        doc.getDocumentElement().normalize();
                        if (!doc.getDocumentElement().getNodeName().equals("rss")) continue;
                        NodeList items = doc.getElementsByTagName("item");
                        Node it = items.item(0);
                        String link = null, data = null, imgSrc = null;
                        if (it.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement = (Element) it;
                            // PARAMETRI BASE
                            NodeList temp = eElement.getElementsByTagName("link");
                            if (temp.getLength() != 0) link = temp.item(0).getTextContent();
                            else {
                                temp = eElement.getElementsByTagName("guid");
                                if (temp.getLength() != 0) link = temp.item(0).getTextContent();
                                else continue;
                            }
                            //if (DB.getInstance().letta(link))
                            //    continue;
                            temp = eElement.getElementsByTagName("pubDate");
                            if (temp.getLength() != 0) data = temp.item(0).getTextContent();
                            // DESCRIZIONE ARTICOLO
                            String desc1 = "", desc2 = "";
                            temp = eElement.getElementsByTagName("description");
                            if (temp.getLength() != 0) desc1 = temp.item(0).getTextContent();
                            temp = eElement.getElementsByTagName("content:encoded");
                            if (temp.getLength() != 0) desc2 = temp.item(0).getTextContent();
                            String desc = desc1.length() > desc2.length() ? desc1 : desc2;
                            //desc.replace("&lt;", "<");
                            //desc.replace("&gt;", ">");
                            desc.replace("<![CDATA[", "");
                            if (desc.endsWith("]]>"))
                                desc = desc.substring(0, desc.length() - 4);
                            desc = Unescape.unescapeHtml3(desc);

                            Notizia novella = new Notizia(eElement.getElementsByTagName("title").item(0).getTextContent(), link, data, f, desc, imgSrc);
                            novella.letta = DB.getInstance().letta(link);
                            notizie.add(novella);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    } finally {
                        if (connection != null) connection.disconnect();
                        if (input != null) try {input.close();} catch (Exception e) {}
                    }
                }
                callbackParser(notizie);
            }
        }, 10000, 10000);
        return Service.START_STICKY;
    }

    public void onCreate() {
        super.onCreate();
    }

    public void callbackParser(LinkedList<Notizia> notizie) {
        for (Notizia n: notizie)
            notifica(n, n.f);
    }

    private void notifica(Notizia n, Fonte f) {
        NotificationManager nm = getSystemService(NotificationManager.class);
        Intent notificationIntent = new Intent(Mosquito.context, ActivityNotizia.class);
        notificationIntent.putExtra("notizia_obj", n);
        PendingIntent contentIntent = PendingIntent.getActivity(Mosquito.context, f.weblink.hashCode(), notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notifica = new Notification.Builder(this).setStyle(new Notification.BigTextStyle()).setSmallIcon(R.drawable.mosquito)
                .setContentTitle(f.nome).setContentText(n.titolo).setContentIntent(contentIntent)
                .setWhen(System.currentTimeMillis()).setChannelId(f.weblink).setAutoCancel(true).build();
        nm.notify(f.weblink.hashCode(), notifica);
    }

    public void onDestroy(){
        isRunning = false;
        t.cancel();
        super.onDestroy();
    }

    public IBinder onBind(Intent intent) {throw new UnsupportedOperationException("Service non bindable");}
}