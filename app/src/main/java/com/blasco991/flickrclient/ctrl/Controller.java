package com.blasco991.flickrclient.ctrl;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
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
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by blasco991 on 11/04/17.
 */
public class Controller {
    private final static String TAG = Controller.class.getName();
    private MVC mvc;

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

        private static final String API_URL = "https://api.flickr.com/services/rest?method=flickr.photos.search&extras=url_z,url_c,url_s,tags";
        private final static String API_KEY = "fdce896d8a8474e8a55a3eb6fa92192e";

        @WorkerThread
        private Iterable<Entry> fetchPictureInfos(String searchString) {
            String queryUrl = String.format("%s&api_key=%s&text=%s", API_URL, API_KEY, searchString);
            List<Entry> infos = new LinkedList<>();

            try {
                URL url = new URL(queryUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                if (urlConnection.getResponseCode() == 200)
                    try {
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        Document doc = builder.parse(urlConnection.getInputStream());
                        NodeList photosXML = doc.getElementsByTagName("photo");
                        for (int i = 0; i < photosXML.getLength(); i++) {
                            Node node = photosXML.item(i);
                            NamedNodeMap attributes = node.getAttributes();

                            String title = attributes.getNamedItem("title").getNodeValue();
                            String tags = attributes.getNamedItem("tags").getNodeValue();
                            String urlPhotoPreview;
                            if (attributes.getNamedItem("url_z") != null)
                                urlPhotoPreview = attributes.getNamedItem("url_z").getNodeValue();
                            else
                                urlPhotoPreview = attributes.getNamedItem("url_s").getNodeValue();
                            String urlPhoto;
                            if (attributes.getNamedItem("url_c") != null)
                                urlPhoto = attributes.getNamedItem("url_c").getNodeValue();
                            else
                                urlPhoto = attributes.getNamedItem("url_s").getNodeValue();
                            Entry entry = new Entry(title, urlPhoto, i, tags);

                            //image LOAD
                            int j = i;
                            new Thread(() -> {
                                URL urlImageConnection;
                                try {
                                    urlImageConnection = new URL(urlPhotoPreview);
                                    HttpURLConnection connection = (HttpURLConnection) urlImageConnection.openConnection();
                                    connection.setDoInput(true);
                                    connection.connect();
                                    try (InputStream input = connection.getInputStream()) {
                                        entry.setPreview(BitmapFactory.decodeStream(input));
                                        Log.d("Thread:\t" + Thread.currentThread().getId(), "Bytes loaded:\t" + entry.getPreview().getRowBytes());
                                        mvc.forEachView(new MVC.ViewTask() {
                                            @Override
                                            public void process(View view) {
                                                view.onModelChanged(j);
                                            }
                                        });
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }).start();
                            infos.add(entry);
                        }

                    } catch (ParserConfigurationException | SAXException e) {
                        e.printStackTrace();
                    }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return infos;
        }
    }


}