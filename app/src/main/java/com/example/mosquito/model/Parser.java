package com.example.mosquito.model;

import android.os.AsyncTask;
import com.example.mosquito.*;
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
            connection.setConnectTimeout(Mosquito.context.getResources().getInteger(R.integer.timeout_parse));
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
                    if (!fr.visualizzaLette && DB.getInstance().letta(link)) continue;
                    temp = eElement.getElementsByTagName("pubDate");
                    if (temp.getLength() != 0) data = temp.item(0).getTextContent();
                    // IMMAGINE ARTICOLO
                    temp = eElement.getElementsByTagName("description");
                    if (temp.getLength() != 0) {
                        imgSrc = temp.item(0).getTextContent();
                        imgSrc.replace("&lt;", "<");
                        imgSrc.replace("&gt;", ">");
                        if (imgSrc.contains("<img ")) {
                            imgSrc = imgSrc.substring(imgSrc.indexOf("<img "));
                            imgSrc = imgSrc.substring(0, imgSrc.indexOf(">"));
                            imgSrc = imgSrc.substring(imgSrc.indexOf("src=") + 5);
                            imgSrc = imgSrc.substring(0, imgSrc.indexOf('"'));
                        } else {
                            temp = eElement.getElementsByTagName("content:encoded");
                            if (temp.getLength() != 0) {
                                imgSrc = temp.item(0).getTextContent();
                                if (imgSrc.contains("<img ")) {
                                    imgSrc = imgSrc.substring(imgSrc.indexOf("<img "));
                                    imgSrc = imgSrc.substring(0, imgSrc.indexOf(">"));
                                    imgSrc = imgSrc.substring(imgSrc.indexOf("src=") + 5);
                                    imgSrc = imgSrc.substring(0, imgSrc.indexOf('"'));
                                } else imgSrc = null;
                            } else imgSrc = null;
                        }
                    }
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
                    if (desc.endsWith("]]>")) desc = desc.substring(0, desc.length()-4);
                    desc = Unescape.unescapeHtml3(desc);

                    Notizia novella = new Notizia(eElement.getElementsByTagName("title").item(0).getTextContent(), link, data, f, desc, imgSrc);
                    novella.letta = DB.getInstance().letta(link);
                    notizie.add(novella);
                }
            }
        }
        catch (Exception e) {e.printStackTrace();notizie.add(new Notizia("CRASH " + f.weblink, "run", new Date().toString(), new Fonte("","Mosquito"), null, null));}
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
        if (DB.getInstance().ottieniImpostazione(1).equals("ampio")) {
            ImgDownloader imdl = new ImgDownloader(temp[0], fr.adapter);
            fr.imdl.add(imdl);
            imdl.execute();
        }
        fr.adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPostExecute(LinkedList<Notizia> notizie) {
        super.onPostExecute(notizie);
        NotizieFragment.lista = notizie;
        if (DB.getInstance().ottieniImpostazione(1).equals("ampio")) {
            /*if (fr.imdl != null && fr.imdl.getStatus() == AsyncTask.Status.RUNNING)
                fr.imdl.cancel(true);*/
            ImgDownloader imdl = new ImgDownloader(notizie, fr.adapter);
            fr.imdl.add(imdl);
            imdl.execute();
        }
        //else fr.adapter.notifyDataSetChanged();
        fr.finitoCaricamento = true;
        fr.swipe.setRefreshing(false);
    }
}