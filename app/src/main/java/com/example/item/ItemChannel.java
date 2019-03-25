package com.example.item;

public class ItemChannel {

    private int id;
    private String PlayListUrl;
    private String Image;
    private String PlayListName;

    public ItemChannel() {
        // TODO Auto-generated constructor stub
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayListUrl() {
        return PlayListUrl;
    }

    public void setPlayListUrl(String url) {
        this.PlayListUrl = url;
    }


    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        this.Image = image;
    }

    public String getPlayListName() {
        return PlayListName;
    }

    public void setPlayListName(String channelname) {
        this.PlayListName = channelname;
    }

}
