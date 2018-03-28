package app.myquizapp.com.myquizapp;

import android.widget.TextView;

/**
 * Created by vaam on 3/21/2018.
 */

public class CardItem {

    private String title;
    private String message;

    public CardItem(String title, String message){
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
