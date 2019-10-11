package com.example.ryanmak.makx1280_a5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.R.id.message;
import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainFragment extends Fragment {

    private final String URL = "https://www.cbc.ca/cmlink/rss-topstories";

    private final int READ_TIMEOUT = 10_000; // milliseconds

    private final int CONNECT_TIMEOUT = 15_000; // milliseconds

    private List<NewsItem> contents = new ArrayList<>();

    private RecyclerView mRecyclerView;

    private NewsAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Load the recyclerview fragment layout and create the recyclerview object
        View view = inflater.inflate(R.layout.fragment_recycler_view, parent, false);
        mRecyclerView = view.findViewById(R.id.list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (isNetworkAvailable()) {
            // Download XML file from url
            new DownloadWebpageTask().execute();
        }

        return view;
    }

    @Override
    public void onResume() {
        // onResume can be called when the new slide activity exits.
        // therefore we want to update the ui with new data in the list
        updateUI();
        super.onResume();
    }

    private void updateUI() {
        // PowerPoint powerPoint = PowerPoint.get(getActivity());
        // slides = powerPoint.getSlides();

        mAdapter = new NewsAdapter(contents);
        mRecyclerView.setAdapter(mAdapter);
    }

    // Check to see if user is connected to the internet
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        return networkInfo != null && networkInfo.isConnected();
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        private String xmlData = "";

        @Override
        protected String doInBackground(String... params) {
            try {
                xmlData = downloadUrl();
                return xmlData;
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            // Save XML to local storage
            writeToFile(xmlData,getContext());
            updateUI();
        }
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private String downloadUrl() throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // Starts the query
            conn.connect();
            is = conn.getInputStream();

            return convertStreamToString(is);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // parse the incoming xml data into a string
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    // write the xml data onto local stroage
    private void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("rssfeed.xml", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();

            Log.d("Write Success", "Yes");
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        readFromFile(context);
    }

    // read the xml file stored in internal storage
    private void readFromFile(Context context) {
        try {
            InputStream is = context.openFileInput("rssfeed.xml");

            // Initialize xml parser to read xml tags
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList mainNodeList = doc.getElementsByTagName("item");

            // for every tag named 'item', grab the title, link, and description
            // in the description, grab the image link and the synopsis paragraph
            for (int i=0; i < mainNodeList.getLength(); i++) {
                Node node = mainNodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) node;

                    // Get title
                    NodeList nodeList = e.getElementsByTagName("title").item(0).getChildNodes();
                    Node childNode = nodeList.item(0);
                    String title = childNode.getNodeValue();

                    // Get link
                    nodeList = e.getElementsByTagName("link").item(0).getChildNodes();
                    childNode = nodeList.item(0);
                    String link = childNode.getNodeValue();

                    // Get description (used to get image and intro paragraph)
                    nodeList = e.getElementsByTagName("description").item(0).getChildNodes();
                    childNode = nodeList.item(1);
                    String description = childNode.getNodeValue();

                    // get image link
                    int x = description.indexOf("src=") + 5;
                    int y = description.indexOf("alt=") - 2;
                    String image = description.substring(x,y);

                    // Get intro paragraph
                    x = description.indexOf("<p>") + 3;
                    y = description.indexOf("</p>");
                    String intro = description.substring(x,y);

                    // Add NewsItem object to list
                    contents.add(new NewsItem(title,image,intro,link));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ViewHolder Class ---------------------------------------------------------------------------
    // the view holder class creates the individual news items in the recyclerview
    // it can also handle events when clicked on
    private class NewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private ImageView mImageView;
        private NewsItem mNewsItem;

        public NewsHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.fragment_item_list, parent, false));

            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.artice_title);
            mImageView = (ImageView) itemView.findViewById(R.id.image);
        }

        public void bind(NewsItem article) {
            mNewsItem = article;
            mTitleTextView.setText(mNewsItem.getTitle());

            Picasso.with(getContext())
                    .load(mNewsItem.getImage())
                    .error(R.drawable.error)
                    .into(mImageView);
        }

        @Override
        public void onClick(View view) {
            // Create second activity that has more details of clicked article
            Intent intent = new Intent(getContext(),DetailedArticleActivity.class);
            intent.putExtra("article",mNewsItem);
            startActivity(intent);
        }
    }
    // --------------------------------------------------------------------------------------------

    // Adapter Class ------------------------------------------------------------------------------
    // The adapter class acts a communicator between code and recyclerview code ???
    // the adapter sends the news items to be displayed in the recyclerview
    private class NewsAdapter extends RecyclerView.Adapter<NewsHolder> {
        private List<NewsItem> data;

        public NewsAdapter(List<NewsItem> d) {
            data = d;
        }

        @Override
        // Create the holder object, the part responsible for creating
        // and displaying news articles
        public NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new NewsHolder(layoutInflater, parent);
        }

        @Override
        // Sends a NewsItem object to be listed in the recyclerview
        public void onBindViewHolder(NewsHolder holder, int position) {
            NewsItem article = data.get(position);
            holder.bind(article);
        }

        @Override
        // Required implementation of getItemCount
        public int getItemCount() {
            return data.size();
        }
    }
    // --------------------------------------------------------------------------------------------
}

