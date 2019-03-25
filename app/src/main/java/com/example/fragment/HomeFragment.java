package com.example.fragment;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.youtubechannel.MoreActivity;
import com.app.youtubechannel.R;
import com.example.adapter.ChannelAdapter;
import com.example.item.ItemChannel;
import com.example.util.Constant;
import com.example.util.ItemOffsetDecoration;
import com.example.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    ScrollView mScrollView;
    ProgressBar mProgressBar;
    Button btnLatest, btnFeatured;
    TextView txtLatest, txtFeatured;
    RecyclerView mLatestView, mFeaturedView;
    ChannelAdapter mLatestAdapter, mFeaturedAdapter;
    ArrayList<ItemChannel> mLatestList, mFeaturedList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mScrollView = rootView.findViewById(R.id.scrollView);
        mProgressBar = rootView.findViewById(R.id.progressBar);
        btnLatest = rootView.findViewById(R.id.btn_latest);
        btnFeatured = rootView.findViewById(R.id.btn_featured);
        txtLatest = rootView.findViewById(R.id.txt_latest_home_size);
        txtFeatured = rootView.findViewById(R.id.txt_featured_home_size);
        mLatestView = rootView.findViewById(R.id.rv_latest);
        mFeaturedView = rootView.findViewById(R.id.rv_featured);
        mLatestList = new ArrayList<>();
        mFeaturedList = new ArrayList<>();

        mLatestView.setHasFixedSize(false);
        mLatestView.setNestedScrollingEnabled(false);
        mLatestView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(requireActivity(), R.dimen.item_offset);
        mLatestView.addItemDecoration(itemDecoration);

        mFeaturedView.setHasFixedSize(false);
        mFeaturedView.setNestedScrollingEnabled(false);
        mFeaturedView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mFeaturedView.addItemDecoration(itemDecoration);

        if (JsonUtils.isNetworkAvailable(requireActivity())) {
            new Home().execute(Constant.HOME_URL);
        } else {
            showToast(getString(R.string.conne_msg1));
        }

        btnLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MoreActivity.class);
                intent.putExtra("which", "0");
                startActivity(intent);
            }
        });

        btnFeatured.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MoreActivity.class);
                intent.putExtra("which", "1");
                startActivity(intent);
            }
        });

        return rootView;
    }

    private class Home extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            mScrollView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressBar.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
            if (null == result || result.length() == 0) {
                showToast(getString(R.string.nodata));
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONObject jsonArray = mainJson.getJSONObject(Constant.ARRAY_NAME);

                    JSONArray jsonLatest = jsonArray.getJSONArray(Constant.HOME_LATEST_ARRAY);
                    JSONObject objJson;
                    for (int i = 0; i < jsonLatest.length(); i++) {
                        objJson = jsonLatest.getJSONObject(i);
                        ItemChannel objItem = new ItemChannel();
                        objItem.setId(objJson.getInt(Constant.CHANNEL_ID));
                        objItem.setPlayListName(objJson.getString(Constant.CHANNEL_TITLE));
                        objItem.setImage(objJson.getString(Constant.CHANNEL_IMAGE));
                        objItem.setPlayListUrl(objJson.getString(Constant.CHANNEL_URL));
                        mLatestList.add(objItem);
                    }

                    JSONArray jsonFeatured = jsonArray.getJSONArray(Constant.HOME_FEATURED_ARRAY);
                    JSONObject objJsonFeature;
                    for (int i = 0; i < jsonFeatured.length(); i++) {
                        objJsonFeature = jsonFeatured.getJSONObject(i);
                        ItemChannel objItem = new ItemChannel();
                        objItem.setId(objJsonFeature.getInt(Constant.CHANNEL_ID));
                        objItem.setPlayListName(objJsonFeature.getString(Constant.CHANNEL_TITLE));
                        objItem.setImage(objJsonFeature.getString(Constant.CHANNEL_IMAGE));
                        objItem.setPlayListUrl(objJsonFeature.getString(Constant.CHANNEL_URL));
                        mFeaturedList.add(objItem);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }
        }
    }

    private void setResult() {

        mLatestAdapter = new ChannelAdapter(getActivity(), mLatestList, false);
        mLatestView.setAdapter(mLatestAdapter);

        mFeaturedAdapter = new ChannelAdapter(getActivity(), mFeaturedList, false);
        mFeaturedView.setAdapter(mFeaturedAdapter);

        txtLatest.setText(mLatestList.size() + " Channel");
        txtFeatured.setText(mFeaturedList.size() + " Channel");

    }

    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }
}
