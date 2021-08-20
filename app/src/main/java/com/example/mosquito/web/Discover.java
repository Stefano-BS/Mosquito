package com.example.mosquito.web;
import com.example.mosquito.Mosquito;
import com.example.mosquito.NuovaFonteActivity;
import com.example.mosquito.R;
import com.example.mosquito.model.Fonte;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.os.AsyncTask;
import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Discover extends AsyncTask<String, Void, Fonte>  {
    NuovaFonteActivity a;

    public Discover(NuovaFonteActivity a) {this.a = a;}

    @Override
    protected Fonte doInBackground(String... strings) {
        HttpURLConnection connection = null;
        BufferedInputStream input = null;
        String webpage = strings[0];
        if (webpage.length()<7 || (!webpage.substring(0,7).equals("http://") && !webpage.substring(0,8).equals("https://")))
            webpage = "https://" + webpage;
        try {
            URL url = new URL(webpage);
            connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(Mosquito.context.getResources().getInteger(R.integer.timeout_discover));
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            input = new BufferedInputStream(connection.getInputStream());
            Document doc;
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.parse(input);
                doc.getDocumentElement().normalize();
            }
            catch (org.xml.sax.SAXException parsingException) { // Il link fornito non è un feed, proviamo a ricavarlo
                if (connection != null) connection.disconnect();
                if (input != null) try {input.close();} catch (Exception e2) {}
                connection = (HttpURLConnection)url.openConnection();
                connection.setConnectTimeout(500);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                input = new BufferedInputStream(connection.getInputStream());
                Scanner scan = new Scanner(input);
                while (scan.hasNextLine()) {
                    String s = scan.nextLine();
                    if (s.contains("<link") && s.contains("rel=\"alternate\"") && s.contains("type=\"application/rss+xml\"") && s.contains("href=\"")) {
                        s = s.substring(s.indexOf("rel=\"alternate\""));
                        s = s.substring(s.indexOf("href=\"")+6);
                        s = s.substring(0, s.indexOf('"'));
                        if (s.startsWith("/")) {
                            if (webpage.endsWith("/")) webpage = webpage.substring(0, webpage.length());
                            webpage = webpage + s;
                        }
                        else webpage = s;
                        return doInBackground(webpage);
                    }
                }
                return null;
            }

            if (doc.getDocumentElement().getNodeName().equals("rss")) { // Il link fornito è un feed
                NodeList channels = doc.getElementsByTagName("channel");
                Node channel = channels.item(0);
                if (channel.getNodeType() == Node.ELEMENT_NODE) {
                    Element channelElement = (Element) channel;
                    String nome = channelElement.getElementsByTagName("title").item(0).getTextContent();
                    return new Fonte(webpage, nome, false);
                }
            }
        }
        catch (Exception e) {e.printStackTrace();}
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

/*
if (doc.getDocumentElement().getNodeName().toLowerCase().equals("html")) {
    Node head = doc.getElementsByTagName("head").item(0);
    NodeList linkItems = ((Element) head).getElementsByTagName("link");
    for (int i = 0; i < linkItems.getLength(); i++) {
        Node linkNode = linkItems.item(i);
        if (linkNode.getNodeType() == Node.ELEMENT_NODE) {
            Element linkEl = (Element) linkNode;
            System.exit(-2);
            if (linkEl.hasAttribute("rel") && linkEl.getAttribute("rel").equals("alternate")
                    && linkEl.hasAttribute("type") && linkEl.getAttribute("type").equals("application/rss+xml")
                    && linkEl.hasAttribute("href")) {
                String nome = linkEl.getAttribute("href");
                if (nome.substring(0, 1).equals("/")) webpage = webpage + nome;
                else webpage = nome;
                connection.disconnect();
                try {
                    input.close();
                } catch (Exception e) {
                }
                break;
            }
        }
    }
}
 */