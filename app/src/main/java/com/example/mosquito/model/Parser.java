package com.example.mosquito.model;

import android.os.AsyncTask;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mosquito.NotizieFragment;
import com.example.mosquito.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.lang.Exception;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class Parser extends AsyncTask<LinkedList<Fonte>, LinkedList<Notizia>, LinkedList<Notizia>> {
    NotizieFragment fr;

    public Parser(NotizieFragment fr) {this.fr = fr;}

    public LinkedList<Notizia> run(Fonte f) {
        LinkedList<Notizia> notizie = new LinkedList<Notizia>();
        HttpURLConnection connection = null;
        InputStream input = null;
        try {
            URL url = new URL(f.weblink);
            connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(500);
            connection.connect();
            input = connection.getInputStream();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(input);
            doc.getDocumentElement().normalize();
            if (!doc.getDocumentElement().getNodeName().equals("rss")) {
                notizie.add(new Notizia("RSSCRASH", "run", new Date().toString(), new Fonte("","Mosquito")));
                return notizie;
            }

            NodeList items = doc.getElementsByTagName("item");
            for (int i = 0; i < items.getLength(); i++){
                Node it = items.item(i);
                String link, data = null;
                if (it.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) it;
                    NodeList temp = eElement.getElementsByTagName("link");
                    if (temp.getLength() != 0) link = temp.item(0).getTextContent();
                    else {
                        temp = eElement.getElementsByTagName("guid");
                        if (temp.getLength() != 0) link = temp.item(0).getTextContent();
                        else continue;
                    }
                    temp = eElement.getElementsByTagName("pubDate");
                    if (temp.getLength() != 0) data = temp.item(0).getTextContent();
                    notizie.add(new Notizia(eElement.getElementsByTagName("title").item(0).getTextContent(), link, data, f));
                }
            }
        }
        catch (Exception e) {e.printStackTrace();notizie.add(new Notizia("CRASH " + f.weblink, "run", new Date().toString(), new Fonte("","Mosquito")));}
        finally {
            if (connection != null) connection.disconnect();
            if (input != null) try{input.close();} catch (Exception e) {}
        }
        return notizie;
    }

    @Override
    protected LinkedList<Notizia> doInBackground(LinkedList<Fonte> ... fonti) {
        Notizia.aggiornaAscDesc();
        LinkedList<Notizia> notizie = new LinkedList<Notizia>();
        for (Fonte f : fonti[0]) {
            notizie.addAll(run(f));
            Collections.sort(notizie);
            publishProgress(notizie);
        }
        return notizie;
    }

    @Override
    protected void onProgressUpdate(LinkedList<Notizia>... temp) {
        super.onProgressUpdate(temp);
        NotizieFragment.lista = temp[0];
        fr.adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPostExecute(LinkedList<Notizia> notizie) {
        super.onPostExecute(notizie);
        NotizieFragment.lista = notizie;
        fr.adapter.notifyDataSetChanged();
        fr.finitoCaricamento = true;
        fr.swipe.setRefreshing(false);
    }
}