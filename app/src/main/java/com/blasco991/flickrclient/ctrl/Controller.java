package com.blasco991.flickrclient.ctrl;

import android.os.AsyncTask;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.blasco991.flickrclient.MVC;
import com.blasco991.flickrclient.model.Entry;
import com.blasco991.flickrclient.model.Model;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
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

        private static final String API_URL = "https://api.flickr.com/services/rest?method=flickr.photos.search&per_page=10";
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
                            String farm_id = node.getAttributes().getNamedItem("farm").getNodeValue();
                            String server_id = node.getAttributes().getNamedItem("server").getNodeValue();
                            String photo_id = node.getAttributes().getNamedItem("id").getNodeValue();
                            String secret = node.getAttributes().getNamedItem("secret").getNodeValue();
                            String urlPhotos = "https://farm" + farm_id + ".staticflickr.com/" + server_id + "/" + photo_id + "_" + secret + "_m.jpg";
                            infos.add(new Entry(node.getAttributes().getNamedItem("title").getNodeValue(), urlPhotos));
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