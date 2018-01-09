package com.example.ryanmak.makx1280_a5;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

/**
 * Created by Ryan Mak on 2017-11-19.
 *
 * MODEL OBJECT
 * This model object is the news object, which will hold information to a specified news item
 */

public class NewsItem implements Parcelable {
    private String title;
    private String image;
    private String intro;
    private String link;

    // Slide Constructor
    public NewsItem(String title, String image, String intro, String link){
        this.image = image;
        this.title = title;
        this.intro = intro;
        this.link = link;
    }

    // Getters for the object properties
    public String getImage(){
        return image;
    }
    public String getTitle(){
        return title;
    }
    public String getIntro(){
        return intro;
    }
    public String getLink() {
        return link;
    }


    /* ======================================================
 * Parcelable part; required in order to be able to pass
 * custom objects to other Activities
 *
 * used:
 *   https://stackoverflow.com/questions/7181526/how-can-i-make-my-custom-objects-parcelable
 *   and class notes
 * ======================================================
 */
    public NewsItem(Parcel in) {
        title = in.readString();
        image = in.readString();
        link = in.readString();
        intro = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(image);
        dest.writeString(link);
        dest.writeString(intro);
    }

    public static final Parcelable.Creator<NewsItem> CREATOR = new Parcelable.Creator<NewsItem>() {
        public NewsItem createFromParcel(Parcel in) {
            return new NewsItem(in);
        }

        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };
}