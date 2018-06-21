package com.roman.abaev.fulldivetest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {
    ArrayList<String> titles;
    String name = "";
    String link = "";
    String date = "";
    private List<News> newsList;
    private List<String> urls;
    private RecyclerView recyclerView;
    private NewsAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        newsList = new ArrayList<News>();
        urls = new ArrayList<String>();
        titles = new ArrayList<String>();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_list_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        //назначение объекта LinearLayoutManager, который управляет позиционированием элементов
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        new ProcessInBackground().execute();

        return view;
    }


    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();

        } catch (IOException e) {
            return null;
        }
    }

    private class NewsHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        private TextView titleNews;
        private TextView linksNews;
        private TextView pubDateNews;
        private News news;


        public NewsHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            titleNews = (TextView) itemView.findViewById(R.id.news_title);
            linksNews = (TextView) itemView.findViewById(R.id.news_link);
            pubDateNews = (TextView) itemView.findViewById(R.id.news_pub_date);
        }

        public void bindNews(News news) {
            this.news = news;
            titleNews.setText(this.news.getName());
            linksNews.setText(this.news.getLinks());
            pubDateNews.setText(this.news.getDate());
        }


        @Override
        public void onClick(View view) {
            Uri uri = Uri.parse(news.getLinks());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    private class NewsAdapter extends RecyclerView.Adapter<NewsHolder> {
        private List<News> newsList;

        public NewsAdapter(List<News> newsList) {
            this.newsList = newsList;
        }

        @NonNull
        @Override
        public NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.item_list, parent, false);
            return new NewsHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NewsHolder holder, int position) {
            News news = newsList.get(position);
            holder.bindNews(news);
        }

        @Override
        public int getItemCount() {
            return newsList.size();
        }

        public void setNews(List<News> newsList) {
            this.newsList = newsList;
        }
    }

    public class ProcessInBackground extends AsyncTask<Integer, Void, Exception> {

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        Exception exception = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Busy Loading rss feed... please wait...");
            progressDialog.show();
        }

        @Override
        protected Exception doInBackground(Integer... integers) {
            urls.add("http://feeds.feedburner.com/TechCrunch/");
            urls.add("http://lifehacker.com/rss");

            for (int i = 0; i < urls.size(); i++) {
                try {

                    URL url = new URL(urls.get(i));

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                    factory.setNamespaceAware(false);

                    XmlPullParser xpp = factory.newPullParser();

                    xpp.setInput(getInputStream(url), "UTF_8");

                    boolean insideItem = false;

                    int eventType = xpp.getEventType();

                    while (eventType != XmlPullParser.END_DOCUMENT) {

                        if (eventType == XmlPullParser.START_TAG) {
                            if (xpp.getName().equalsIgnoreCase("item")) {

                                insideItem = true;
                            } else if (xpp.getName().equalsIgnoreCase("title")) {
                                if (insideItem) {
                                    name = xpp.nextText();
                                }
                            } else if (xpp.getName().equalsIgnoreCase("link")) {
                                if (insideItem) {
                                    link = xpp.nextText();
                                }
                            } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                                if (insideItem) {
                                    date = xpp.nextText();
//                                    SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
//                                    Date date = format.parse(s);
//                                    news.setDate(date);
                                }
                                newsList.add(new News(name, link, date));
                            }
                        } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = false;
                        }
                        eventType = xpp.next();
                    }
                } catch (MalformedURLException e) {
                    exception = e;
                } catch (XmlPullParserException e) {
                    exception = e;
                } catch (IOException e) {
                    exception = e;
                }
            }
            return exception;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);
            adapter = new NewsAdapter(newsList);
            recyclerView.setAdapter(adapter);
            progressDialog.dismiss();
        }


    }

}
