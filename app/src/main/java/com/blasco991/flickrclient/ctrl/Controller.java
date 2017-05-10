package com.blasco991.flickrclient.ctrl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.blasco991.flickrclient.MVC;
import com.blasco991.flickrclient.model.Entry;
import com.blasco991.flickrclient.view.View;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by blasco991 on 11/04/17.
 */
public class Controller {

    private MVC mvc;
    private LruCache<String, Bitmap> mMemoryCache;
    private final static String TAG = Controller.class.getName();

    public Controller() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public Bitmap getBitmap(String key, int position) {
        if (mMemoryCache.get(key) == null) {
            new Thread(new ImageDownloadThread(key, position)).start();
            return null;
        } else {
            return mMemoryCache.get(key);
        }
    }

    public void setMVC(MVC mvc) {
        this.mvc = mvc;
    }

    @UiThread
    public void fetchPictureInfos(String searchString) {
        new PicturesInfoFetcher().execute(searchString);
    }

    private class PicturesInfoFetcher extends AsyncTask<String, Void, Iterable<Entry>> {

        @Override
        @WorkerThread
        protected Iterable<Entry> doInBackground(String... args) {
            return fetchPictureInfos(args[0]);
        }

        @Override
        @UiThread
        protected void onPostExecute(Iterable<Entry> pictureInfos) {
            mvc.model.storePictureInfos(pictureInfos);
        }

        private static final String API_URL = "https://api.flickr.com/services/rest?method=flickr.photos.search&extras=tags&per_page=20";
        private final static String API_KEY = "fdce896d8a8474e8a55a3eb6fa92192e";

        @WorkerThread
        private Iterable<Entry> fetchPictureInfos(String searchString) {
            String queryUrl = String.format("%s&api_key=%s&text=%s", API_URL, API_KEY, searchString);
            ArrayList<Entry> infos = null;
            try {
                URL url = new URL(queryUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(100);

                if (urlConnection.getResponseCode() == 200)
                    try {
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        Document doc = builder.parse(urlConnection.getInputStream());
                        NodeList photosXML = doc.getElementsByTagName("photo");
                        infos = new ArrayList<>(photosXML.getLength());
                        for (int i = 0; i < photosXML.getLength(); i++) {
                            Node node = photosXML.item(i);
                            NamedNodeMap attributes = node.getAttributes();

                            String title = attributes.getNamedItem("title").getNodeValue();
                            String tags = attributes.getNamedItem("tags").getNodeValue();
                            String farm_id = node.getAttributes().getNamedItem("farm").getNodeValue();
                            String server_id = node.getAttributes().getNamedItem("server").getNodeValue();
                            String photo_id = node.getAttributes().getNamedItem("id").getNodeValue();
                            String secret = node.getAttributes().getNamedItem("secret").getNodeValue();
                            String urlPhoto = "https://farm" + farm_id + ".staticflickr.com/" + server_id + "/" + photo_id + "_" + secret + "_c.jpg";
                            String urlPhotoPreview = "https://farm" + farm_id + ".staticflickr.com/" + server_id + "/" + photo_id + "_" + secret + "_z.jpg";

                            Entry entry = new Entry(title, i, urlPhoto, urlPhotoPreview, tags);
                            new Thread(new ImageDownloadThread(urlPhotoPreview, i)).start();    //image LOAD
                            infos.add(entry);
                        }

                    } catch (ParserConfigurationException | SAXException e) {
                        e.printStackTrace();
                    }
                else
                    throw new RuntimeException(urlConnection.getResponseMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return infos;
        }
    }

    private class ImageDownloadThread implements Runnable {

        private final String url;
        private final int position;

        ImageDownloadThread(String url, int position) {
            this.url = url;
            this.position = position;
        }

        public void run() {
            URL urlImageConnection;
            try {
                urlImageConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlImageConnection.openConnection();
                connection.setDoInput(true);
                connection.connect();
                try (InputStream input = connection.getInputStream()) {
                    Bitmap bitmap = BitmapFactory.decodeStream(input);
                    addBitmapToMemoryCache(url, bitmap);
                    Log.d("Thread:\t" + Thread.currentThread().getId(), "Bytes loaded:\t" + bitmap.getRowBytes());
                    mvc.forEachView(view -> view.onModelChanged(position));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}