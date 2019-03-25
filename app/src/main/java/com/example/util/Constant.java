package com.example.util;

import com.app.youtubechannel.BuildConfig;

import java.io.Serializable;

public class Constant implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    private static String SERVER_URL = BuildConfig.SERVER_URL;

    public static final String IMAGE_PATH = SERVER_URL + "images/";

    public static final String CATEGORY_URL = SERVER_URL + "api.php?cat_list";

    public static final String LATEST_URL = SERVER_URL + "api.php?latest";

    public static final String CATEGORY_ITEM_URL = SERVER_URL + "api.php?cat_id=";

    public static final String ABOUT_URL = SERVER_URL + "api.php?app_details";

    public static final String HOME_URL = SERVER_URL + "api.php?home";

    public static final String SEARCH_URL = SERVER_URL + "api.php?search=";

    public static final String FEATURED_URL = SERVER_URL + "api_featured.php";

    public static final String ARRAY_NAME = "YOUTUBE_CHANNEL";

    public static final String CATEGORY_NAME = "category_name";
    public static final String CATEGORY_CID = "cid";
    public static final String CATEGORY_IMAGE = "category_image";

    public static final String CHANNEL_ID = "id";
    public static final String CHANNEL_TITLE = "channel_name";
    public static final String CHANNEL_URL = "channel_username";
    public static final String CHANNEL_IMAGE = "channel_image";


    public static final String APP_NAME = "app_name";
    public static final String APP_IMAGE = "app_logo";
    public static final String APP_VERSION = "app_version";
    public static final String APP_AUTHOR = "app_author";
    public static final String APP_CONTACT = "app_contact";
    public static final String APP_EMAIL = "app_email";
    public static final String APP_WEBSITE = "app_website";
    public static final String APP_DESC = "app_description";
    public static final String APP_PRIVACY_POLICY = "app_privacy_policy";


    public static final String HOME_LATEST_ARRAY = "latest_channels";
    public static final String HOME_FEATURED_ARRAY = "featured_channels";

    public static final String YTPLAYURL = "https://www.googleapis.com/youtube/v3/search?part=snippet,id&order=date&maxResults=20&channelId=";
    public static final String YTAPIKEY = "&key=";

    public static int AD_COUNT = 0;
    public static int AD_COUNT_SHOW;

    public static boolean isBanner = false, isInterstitial = false;
    public static String adMobBannerId, adMobInterstitialId, adMobPublisherId;

}
