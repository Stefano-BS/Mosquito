package com.example.mosquito.web;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import com.example.mosquito.Mosquito;
import com.example.mosquito.NotizieFragment;
import com.example.mosquito.R;
import com.example.mosquito.model.Notizia;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;

public class ImgDownloader extends AsyncTask<Void, Void, HashMap<String, Bitmap>> {
    ArrayAdapter adapter;
    private LinkedList<Notizia> destinazioni;

    public ImgDownloader(LinkedList<Notizia> destinazioni, ArrayAdapter adapter) {
        this.destinazioni = destinazioni;
        this.adapter = adapter;
    }


    @Override
    protected HashMap<String, Bitmap> doInBackground(Void... none) {
        HashMap<String, Bitmap> ret = new HashMap<>();
        for (Notizia n : destinazioni) {
            if (n.imgSrc == null || n.imgSrc.length()<5 || NotizieFragment.catalogo.containsKey(n.imgSrc)) continue;
            try {
                URL urlConnection = new URL(n.imgSrc);
                HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
                connection.setDoInput(true);
                connection.setConnectTimeout(Mosquito.context.getResources().getInteger(R.integer.timeout_img));
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                if (bitmap.getWidth() > Mosquito.convertDpToPixel(120)) {
                    ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bytearrayoutputstream);
                    byte[] BYTE = bytearrayoutputstream.toByteArray();
                    bitmap = BitmapFactory.decodeByteArray(BYTE, 0, BYTE.length);
                }
                ret.put(n.imgSrc, bitmap);
            } catch (Exception e) {continue;}
        }
        return ret;
    }

    @Override
    protected void onPostExecute(HashMap<String, Bitmap> result) {
        super.onPostExecute(result);
        NotizieFragment.catalogo.putAll(result);
        NotizieFragment.imdl.remove(this);
        adapter.notifyDataSetChanged();
    }
}
