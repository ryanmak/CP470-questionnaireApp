package com.example.ryanmak.makx1280_a5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * Created by Ryan Mak on 2017-11-19.
 */

public class DetailedArticleActivity extends AppCompatActivity{
    private NewsItem article;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        setContentView(R.layout.activity_detailed_article);

        // get article data from parent activity
        Intent intent = getIntent();
        article = intent.getParcelableExtra("article");

        // set image view as article image
        ImageView iv = (ImageView) findViewById(R.id.detailedImageView);
        Picasso.with(getBaseContext())
                .load(article.getImage())
                .error(R.drawable.error)
                .into(iv);

        // set text box as the synopsis text in the article
        TextView synopsis = (TextView) findViewById(R.id.detailedTextView);
        synopsis.setText(article.getIntro());
    }

    // When the 'see more' button is clicked, this function will run
    public void openWebview(View view){
        // This will open a webview activity that will display the full article
        Intent intent = new Intent(getBaseContext(),WebViewActivity.class);
        intent.putExtra("article",article);
        startActivity(intent);
    }
}
