package com.example.kenkoku.gallemory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static MyDBHandler dbHandler;
    private Spinner dropdown;
    private static GridView gridView;
    private static ImageAdapter imageAdapter;
    private Button upload;
    private static ArrayList<MyData> photoList;
    private static ArrayList<Integer> idList;
    private static String MY_PREFS_NAME = "theme choice";
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;
    private static int themeInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        themeInt = prefs.getInt("theme", 0);

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
        setContentView(R.layout.activity_main);

        dbHandler = new MyDBHandler(this, null);
        gridView = findViewById(R.id.gridView);
        upload = findViewById(R.id.upload);
        photoList = new ArrayList<MyData>();
        photoList = dbHandler.getAllImages();
        idList = new ArrayList<Integer>();
        idList = dbHandler.getAllIds();

        dropdown = findViewById(R.id.themeSpinner);
        String[] items = new String[]{"Pink Theme", "Blue Theme", "Green Theme", "Black Theme", "Japan Theme"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setSelection(themeInt);

        imageAdapter = new ImageAdapter(this, photoList, idList);
        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), FullImageActivity.class);
                i.putExtra("id", imageAdapter.getImageId(position));
                startActivity(i);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Pop.class));
            }
        });

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                editor = prefs.edit();

                if (position != themeInt) {
                    editor.putInt("theme", position);
                    editor.apply();
                    recreate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public static void uploadPicture(Bitmap bitmap, String title, String caption, String date, boolean round) {
        MyData imgData = new MyData(bitmap);
        int id = dbHandler.addEntry(imgData, title, caption, date, round);
        imageAdapter.addPhoto(id, imgData);
        gridView.setAdapter(imageAdapter);
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
