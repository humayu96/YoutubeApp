package com.app.youtubechannel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.example.fragment.CategoryFragment;
import com.example.fragment.FavouriteFragment;
import com.example.fragment.HomeFragment;
import com.example.fragment.LatestFragment;
//import com.example.util.BannerAds;
import com.example.util.Constant;
import com.example.util.IsRTL;
import com.example.util.JsonUtils;
import com.ixidev.gdpr.GDPRChecker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    MyApplication MyApp;
    LinearLayout mAdViewLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IsRTL.ifSupported(MainActivity.this);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fragmentManager = getSupportFragmentManager();
        MyApp = MyApplication.getInstance();
        NavigationView navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        mAdViewLayout = findViewById(R.id.adView);
        HomeFragment homeFragment = new HomeFragment();
        fragmentManager.beginTransaction().replace(R.id.Container, homeFragment).commit();

//        if (JsonUtils.isNetworkAvailable(MainActivity.this)) {
//            new MyTaskConsent().execute(Constant.ABOUT_URL);
//        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.menu_go_home:
                        toolbar.setTitle(getString(R.string.menu_home));
                        HomeFragment homeFragment = new HomeFragment();
                        fragmentManager.beginTransaction().replace(R.id.Container, homeFragment).commit();
                        return true;
                    case R.id.menu_go_category:
                        toolbar.setTitle(getString(R.string.menu_category));
                        CategoryFragment currentCategory = new CategoryFragment();
                        fragmentManager.beginTransaction().replace(R.id.Container, currentCategory).commit();
                        return true;
                    case R.id.menu_go_latest:
                        toolbar.setTitle(getString(R.string.menu_latest));
                        LatestFragment latestFragment = new LatestFragment();
                        fragmentManager.beginTransaction().replace(R.id.Container, latestFragment).commit();
                        return true;
                    case R.id.menu_go_favourite:
                        toolbar.setTitle(getString(R.string.menu_favourite));
                        FavouriteFragment favouriteFragment = new FavouriteFragment();
                        fragmentManager.beginTransaction().replace(R.id.Container, favouriteFragment).commit();
                        return true;
                    case R.id.menu_go_about:
                        Intent about = new Intent(MainActivity.this, AboutUsActivity.class);
                        startActivity(about);
                        return true;
                    case R.id.menu_go_rate:
                        RateApp();
                        return true;
                    case R.id.menu_go_share:
                        ShareApp();
                        return true;
                    case R.id.menu_go_privacy:
                        Intent privacy = new Intent(MainActivity.this, PrivacyActivity.class);
                        startActivity(privacy);
                        return true;
                    default:
                        return true;
                }
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (!hasFocus) {
                    searchMenuItem.collapseActionView();
                    searchView.setQuery("", false);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("search", arg0);
                startActivity(intent);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String arg0) {
                // TODO Auto-generated method stub
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void ShareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg) + getPackageName());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void RateApp() {
        final String appName = getPackageName();//your application package name i.e play store application url
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id="
                            + appName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id="
                            + appName)));
        }
    }

    @Override
    public void onBackPressed() {
        ExitApp();
    }

    private void ExitApp() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.exit_msg))
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

//    private class MyTaskConsent extends AsyncTask<String, Void, String> {

//        @Override
//        protected String doInBackground(String... params) {
//            return JsonUtils.getJSONString(params[0]);
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            try {
//                JSONObject mainJson = new JSONObject(result);
//                JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
//                JSONObject objJson = jsonArray.getJSONObject(0);
//                Constant.isBanner = objJson.getBoolean("banner_ad");
//                Constant.isInterstitial = objJson.getBoolean("interstital_ad");
//                Constant.adMobBannerId = objJson.getString("banner_ad_id");
//                Constant.adMobInterstitialId = objJson.getString("interstital_ad_id");
//                Constant.adMobPublisherId = objJson.getString("publisher_id");
//                Constant.AD_COUNT_SHOW = objJson.getInt("interstital_ad_click");
//
//                new GDPRChecker()
//                        .withContext(MainActivity.this)
//                        .withPrivacyUrl(getString(R.string.privacy_url)) // your privacy url
//                        .withPublisherIds(Constant.adMobPublisherId) // your admob account Publisher id
//                     //   .withTestMode("9424DF76F06983D1392E609FC074596C") // remove this on real project
//                        .check();
//
////                BannerAds.ShowBannerAds(MainActivity.this, mAdViewLayout);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
