package com.example.mausam.guessthecelebrity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebURLs = new ArrayList<>();
    ArrayList<String> celebNames = new ArrayList<>();
    int choosenCeleb = 0;

    int locationOfCorretAnswer;
    String[] answers = new String[4];

    ImageView celebratyImageView;

    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void celebChoosen(View view) {

        if (view.getTag().toString().equals(Integer.toString(locationOfCorretAnswer))) {

            Toast.makeText(this, "Correct!", Toast.LENGTH_LONG).show();

        } else {

            Toast.makeText(this, "Wrong! It was " + celebNames.get(choosenCeleb), Toast.LENGTH_LONG).show();
        }
        createQuestion();

    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {

                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                return bitmap;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    public class DownloadContent extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";

            URL url;

            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();

                while (data != -1) {

                    char currentChar = (char) data;

                    result += currentChar;

                    data = reader.read();

                }

                return result;

            } catch (Exception e) {

                e.printStackTrace();

                return "caught Exception";

            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        celebratyImageView = (ImageView) findViewById(R.id.celebratyImageView);

        button0 = (Button) findViewById(R.id.button1);
        button1 = (Button) findViewById(R.id.button2);
        button2 = (Button) findViewById(R.id.button3);
        button3 = (Button) findViewById(R.id.button4);

        DownloadContent downloadContent = new DownloadContent();

        String result = null;

        try {

            result = downloadContent.execute("http://www.posh24.se/kandisar").get();

            String[] splitResult = result.split("<div class=\"sidebarInnerContainer\">");


            Pattern pattern = Pattern.compile("<img src=\"(.*?)\"");

            Matcher matcher = pattern.matcher(splitResult[0]);

            while (matcher.find()) {

                celebURLs.add(matcher.group(1));

            }


            pattern = Pattern.compile("alt=\"(.*?)\"");

            matcher = pattern.matcher(splitResult[0]);

            while (matcher.find()) {

                celebNames.add(matcher.group(1));
            }


            createQuestion();


        } catch (InterruptedException e) {

            e.printStackTrace();

        } catch (ExecutionException e) {

            e.printStackTrace();

        }


    }

    public void createQuestion() {

        Random random = new Random();

        choosenCeleb = random.nextInt(celebURLs.size());

        DownloadImage imageTask = new DownloadImage();

        Bitmap celebImage;

        try {
            celebImage = imageTask.execute(celebURLs.get(choosenCeleb)).get();

            celebratyImageView.setImageBitmap(celebImage);


            locationOfCorretAnswer = random.nextInt(4);

            int incorrectAnswerLocaton;

            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorretAnswer) {

                    answers[i] = celebNames.get(choosenCeleb);

                } else {
                    incorrectAnswerLocaton = random.nextInt(celebURLs.size());

                    while (incorrectAnswerLocaton == choosenCeleb) {

                        incorrectAnswerLocaton = random.nextInt(celebURLs.size());
                    }

                    answers[i] = celebNames.get(incorrectAnswerLocaton);
                }
            }

            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
