package com.example.bookshelf;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Book implements Serializable {

    private int id;
    private String title;
    private String author;
    private String coverURL;


    private static final String ID_FIELD = "book_id";
    private static final String TITLE_FIELD = "title";
    private static final String AUTHOR_FIELD = "author";
    private static final String COVER_FIELD = "cover_url";



    public Book(){

    }

    public Book(int id) {
        this.id = id;
    }

    public Book(String title, String author, String coverURL){
        this.title=title;
        this.author=author;
        this.coverURL=coverURL;
    }

    public Book(JSONObject jBook){
        try {
            this.id=jBook.getInt(ID_FIELD);
            this.title=jBook.getString(TITLE_FIELD);
            this.author=jBook.getString(AUTHOR_FIELD);
            this.coverURL=jBook.getString(COVER_FIELD);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }



}
