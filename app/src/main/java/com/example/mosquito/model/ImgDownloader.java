package com.example.mosquito.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import com.example.mosquito.Mosquito;
import com.example.mosquito.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

public class ImgDownloader extends AsyncTask<Void, Void, LinkedList<Bitmap>> {
    ArrayAdapter adapter;
    private LinkedList<Notizia> destinazioni;

    public ImgDownloader(LinkedList<Notizia> destinazioni, ArrayAdapter adapter) {
        this.destinazioni = destinazioni;
        this.adapter = adapter;
    }


    @Override
    protected LinkedList<Bitmap> doInBackground(Void... none) {
        LinkedList<Bitmap> ret = new LinkedList<Bitmap>();
        for (Notizia n : destinazioni) {
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
                ret.add(bitmap);
            } catch (Exception e) {ret.add(null);}
        }
        return ret;
    }

    @Override
    protected void onPostExecute(LinkedList<Bitmap> result) {
        super.onPostExecute(result);
        for (int i=0; i<destinazioni.size(); i++)
            destinazioni.get(i).image = result.get(i);
        adapter.notifyDataSetChanged();
    }
}
