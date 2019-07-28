package com.mycon.barsh.finalproj;


public class NodeObject{
    public String title;
    public String rank;
    public String url;
    public String imageurl;
    public String category;

    public NodeObject(String title, String rank,String url, String imageurl, String category) {

        this.title = title;
        this.rank = rank;
        this.url = url;
        this.imageurl = imageurl;
        this.category = category;
    }
    public NodeObject(String title) {
        this.title = title;
    }

}
