package com.example.kenkoku.gallemory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.Objects;

public class FullImageActivity extends AppCompatActivity {

    private int id;
    private EditText title;
    private EditText comment;
    private MyDBHandler dbHandler;
    private static String MY_PREFS_NAME = "theme choice";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        int themeInt = prefs.getInt("theme", 0);

        Resources.Theme theme = super.getTheme();
        switch (themeInt) {
            case 1:
                theme.applyStyle(R.style.BlueTheme, true);
                break;
            case 2:
                theme.applyStyle(R.style.GreenTheme, true);
                break;
            case 3:
                theme.applyStyle(R.style.BlackTheme, true);
                break;
            case 4:
                theme.applyStyle(R.style.JapanTheme, true);
                break;
            default:
                theme.applyStyle(R.style.PinkTheme, true);
                break;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        Intent i = getIntent();

        dbHandler = new MyDBHandler(this, null);

        TextView dateView = findViewById(R.id.dateView);
        ImageView enlargedImage = findViewById(R.id.enlargedImage);
        comment = findViewById(R.id.comment);
        title = findViewById(R.id.title);
        Button save = findViewById(R.id.save);
        Button delete = findViewById(R.id.delete);

        id = Objects.requireNonNull(i.getExtras()).getInt("id");

        Bitmap bitmap = dbHandler.getImage(id).getImageDataInBitmap();
        Resources res = getResources();

        if (dbHandler.getRound(id)) {
            int width = enlargedImage.getWidth();
            int height = enlargedImage.getHeight();
            if (width >= height) {
                enlargedImage.setMaxWidth(height);
            } else {
                enlargedImage.setMaxHeight(width);
            }

            GlideApp
                    .with(this)
                    .load(bitmap)
                    .placeholder(R.drawable.totoro)
                    .transform(new CircleCrop())
                    .into(enlargedImage);
        } else {
            GlideApp
                    .with(this)
                    .load(bitmap)
                    .placeholder(R.drawable.totoro)
                    .transform(new RoundedCorners(50))
                    .into(enlargedImage);
        }

        title.setText(dbHandler.getTitle(id));
        comment.setText(dbHandler.getCaption(id));
        dateView.setText(dbHandler.getDate(id));

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler.updateEntry(id, title.getText().toString(), comment.getText().toString());
                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler.deleteEntry(id);
                finish();
            }
        });
    }
}
