package com.example.kenkoku.gallemory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.io.IOException;

public class Pop extends Activity {

    private ImageView image;
    private EditText title;
    private EditText caption;
    private TextView error;
    private CheckBox checkBox;
    private Bitmap chosenImage;
    private EditText date;
    public static final int GET_FROM_GALLERY = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_view);

        image = findViewById(R.id.pop_image);
        int placeHoldImg = R.drawable.totoro;
        image.setImageResource(placeHoldImg);

        Button select = findViewById(R.id.pop_select);
        Button add = findViewById(R.id.pop_add);
        title = findViewById(R.id.pop_title);
        caption = findViewById(R.id.pop_caption);
        error = findViewById(R.id.pop_error);
        checkBox = findViewById(R.id.pop_radio);
        date = findViewById(R.id.date);
        chosenImage = null;

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                        GET_FROM_GALLERY);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tText = title.getText().toString();
                String cText = caption.getText().toString();
                String dText = date.getText().toString();
                boolean round = checkBox.isChecked();

                if (chosenImage != null) {
                    error.setVisibility(View.GONE);
                    MainActivity.uploadPicture(chosenImage, tText, cText, dText, round);
                    finish();
                } else {
                    error.setVisibility(View.VISIBLE);
                }
            }
        });


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .6));
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                chosenImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                chosenImage = MainActivity.getResizedBitmap(chosenImage, 850);

                GlideApp
                        .with(this)
                        .load(chosenImage)
                        .transforms(new CenterCrop(), new RoundedCorners(30))
                        .into(image);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
