package com.example.qr_readerexample;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import com.bumptech.glide.Glide;

public class GenerateQR extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String user_id = intent.getStringExtra(DecoderActivity.EXTRA_MESSAGE);

        new PostClass(this).execute(user_id, "222");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "http://accords.kz/", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private class PostClass extends AsyncTask<String, Void, Void> {
        private final Context context;

        public PostClass(Context c){

            this.context = c;
        }

        @Override
        protected Void doInBackground(String... params) {
            String login = params[0];
            String password = params[1];
            try {
                final StringBuilder output = new StringBuilder();

                URL url = new URL("http://accords.kz/create_new_link.php");
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                //присваеваем элементу массива MESSAGE текст сообщения
                String urlParameters = "CREATOR_ID="+login+"&PASSWORD=66";
                connection.setRequestMethod("POST");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "ru-RU,ru;0.5");
                connection.setRequestProperty("charset", "utf-8");
                connection.setDoOutput(true);
                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();
                int responseCode = connection.getResponseCode();

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                //пока буферед ридер не равен пустому значению
                while((line = br.readLine()) != null )
                {
                    //добавляем строки лайн в стринг билдер
                    responseOutput.append(line);
                }

                br.close();
                //добавляем один стринг билдер в другой с финал
                output.append(responseOutput.toString());
                final String output_str = output+"";

                GenerateQR.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //Если вернул елиницу, выводим активити чат, если нет, нехуй пиздеть, пусть заново вбивает
                        if(output_str.equals("1"))
                        {
                            //Toast.makeText(GenerateQR.this, output_str, Toast.LENGTH_SHORT).show();
                            TextView textView = (TextView)findViewById(R.id.link_place);
                            textView.setText(output_str);
                            ImageView imageView = (ImageView)findViewById(R.id.imageView2);
                            Glide.with(GenerateQR.this).load("https://chart.googleapis.com/chart?chs=300x300&cht=qr&chl="+output_str+"&choe=UTF-8").into(imageView);
                        }
                        else
                        {
                            //Toast.makeText(GenerateQR.this, output_str, Toast.LENGTH_SHORT).show();
                            TextView textView = (TextView)findViewById(R.id.link_place);
                            textView.setText(output_str);
                            ImageView imageView = (ImageView)findViewById(R.id.imageView2);
                            Glide.with(GenerateQR.this).load("https://chart.googleapis.com/chart?chs=300x300&cht=qr&chl="+output_str+"&choe=UTF-8").into(imageView);
                        }
                    }
                });

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute()
        {

        }

    }

}