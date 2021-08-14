package com.example.mosquito.model;

import android.os.AsyncTask;
import com.example.mosquito.NuovaFonteActivity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Discover extends AsyncTask<String, Void, Fonte>  {
    NuovaFonteActivity a;

    public Discover(NuovaFonteActivity a) {this.a = a;}

    @Override
    protected Fonte doInBackground(String... strings) {
        HttpURLConnection connection = null;
        InputStream input = null;
        try {
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(500);
            connection.connect();
            input = connection.getInputStream();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(input);
            doc.getDocumentElement().normalize();
            if (!doc.getDocumentElement().getNodeName().equals("rss")) {
                String nome = "";
                NodeList items = doc.getElementsByTagName("channel");
                for (int i = 0; i < items.getLength(); i++){
                    Node it = items.item(i);
                    if (it.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) it;
                        nome = eElement.getElementsByTagName("title").item(0).getTextContent();
                        break;
                    }
                }
                return new Fonte(strings[0], nome);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if (connection != null) connection.disconnect();
            if (input != null) try {input.close();} catch (Exception e) {}
        }
        return null;
    }

    @Override
    protected void onPostExecute(Fonte fonte) {
        super.onPostExecute(fonte);
        a.controlla(fonte);
    }
}