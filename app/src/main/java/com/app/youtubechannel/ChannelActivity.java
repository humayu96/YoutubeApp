package com.app.youtubechannel;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.adapter.PlayListAdapter;
import com.example.item.YtChannelItem;
//import com.example.util.BannerAds;
import com.example.util.Constant;
import com.example.util.IsRTL;
import com.example.util.ItemOffsetDecoration;
import com.example.util.JsonUtils;
import com.example.util.OnLoadMoreListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ChannelActivity extends AppCompatActivity {

    ArrayList<YtChannelItem> mListItem;
    public RecyclerView recyclerView;
    PlayListAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;
    String Id, Name;
    String nextPaegToken, prevPageToken;
    boolean isLoadMore = false, isFirst = true, isFromNotification = false;
    LinearLayoutManager lLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_item);
        IsRTL.ifSupported(ChannelActivity.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Intent intent = getIntent();
        Id = intent.getStringExtra("Id");
        Name = intent.getStringExtra("name");
        if (intent.hasExtra("isNotification")) {
            isFromNotification = true;
        }
        setTitle(Name);

        mListItem = new ArrayList<>();
        lyt_not_found = findViewById(R.id.lyt_not_found);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.vertical_courses_list);
        recyclerView.setHasFixedSize(true);
        lLayout = new LinearLayoutManager(ChannelActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(lLayout);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(ChannelActivity.this, R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        if (JsonUtils.isNetworkAvailable(ChannelActivity.this)) {
            new getPlayList().execute(Constant.YTPLAYURL + Id + Constant.YTAPIKEY + getResources().getString(R.string.youtube_api_key));
        }

        LinearLayout mAdViewLayout = findViewById(R.id.adView);
//        BannerAds.ShowBannerAds(getApplicationContext(), mAdViewLayout);


    }

    private class getPlayList extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isFirst)
                showProgress(true);
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            return readPlayList(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (isFirst)
                showProgress(false);
            try {
                JSONObject mainJson = new JSONObject(result);
                if (mainJson.has("nextPageToken")) {
                    nextPaegToken = mainJson.getString("nextPageToken");
                    isLoadMore = true;
                } else {
                    isLoadMore = false;
                }

                if (mainJson.has("prevPageToken")) {
                    prevPageToken = mainJson.getString("prevPageToken");
                }

                JSONObject pageInfo = mainJson.getJSONObject("pageInfo");
                String totalResults = pageInfo.getString("totalResults");
                String resultsPerPage = pageInfo.getString("resultsPerPage");

                JSONArray jsonArray = mainJson.getJSONArray("items");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    YtChannelItem item = new YtChannelItem();

                    JSONObject id = jsonObject.getJSONObject("id");
                    String videoId = id.getString("videoId");

                    JSONObject snippet = jsonObject.getJSONObject("snippet");
                    String title = snippet.getString("title");
                    String description = snippet.getString("description");

                    JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                    JSONObject medium = thumbnails.getJSONObject("medium");
                    String url = medium.getString("url");

                    item.setChannelId(videoId);
                    item.setChannelName(title);
                    item.setChannelUserName(description);
                    item.setChannelImageurl(url);
                    mListItem.add(item);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isFirst) {
                displayData();
            } else {
                adapter.notifyDataSetChanged();
            }

        }

    }

    private void displayData() {
        adapter = new PlayListAdapter(ChannelActivity.this, mListItem, recyclerView);
        recyclerView.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (isLoadMore) {
                    mListItem.add(null);
                    recyclerView.post(new Runnable() {
                        public void run() {
                            adapter.notifyItemInserted(mListItem.size() - 1);
                        }
                    });

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mListItem.remove(mListItem.size() - 1);
                            adapter.notifyItemRemoved(mListItem.size());
                            adapter.notifyDataSetChanged();
                            if (JsonUtils.isNetworkAvailable(ChannelActivity.this)) {
                                isFirst = false;
                                new getPlayList().execute(Constant.YTPLAYURL + Id + Constant.YTAPIKEY + getResources().getString(R.string.youtube_api_key) + "&pageToken=" + nextPaegToken);
                            }
                            adapter.setLoaded();
                        }
                    }, 1200);
                } else {
                    Toast.makeText(ChannelActivity.this, "NO MORE VIDEO", Toast.LENGTH_LONG).show();
                }

            }
        });

        if (adapter.getItemCount() == 0) {
            lyt_not_found.setVisibility(View.VISIBLE);
        } else {
            lyt_not_found.setVisibility(View.GONE);
        }
    }


    private void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isFromNotification) {
            Intent intent = new Intent(ChannelActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }

    }

    public String readPlayList(String Url) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(Url);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e(ChannelActivity.class.toString(), "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
