package com.example.qr_readerexample;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ArrayList<String> list = new ArrayList<String>();
    ArrayList<String> qId_list = new ArrayList<String>();
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String message = intent.getStringExtra(DecoderActivity.EXTRA_MESSAGE);
        EditText user_id_ed_text = (EditText) findViewById(R.id.user_id);
        user_id_ed_text.setText(message);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), DecoderActivity.class);
                startActivity(i);
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                */
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button btn_buy = (Button) findViewById(R.id.btn_buy);
        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buy();
            }
        });
    }

    public void buy() {
        EditText user_id_ed_text = (EditText) findViewById(R.id.user_id);
        String user_id = user_id_ed_text.getText().toString();
        EditText price_ed_text = (EditText) findViewById(R.id.price);
        String price = price_ed_text.getText().toString();
        new PostClass(this).execute(user_id, price);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent i = new Intent(getApplicationContext(), DecoderActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_gallery) {
            Intent i = new Intent(getApplicationContext(), GenerateQR.class);
            EditText user_id_ed_text = (EditText) findViewById(R.id.user_id);
            String user_id = user_id_ed_text.getText().toString();
            i.putExtra(EXTRA_MESSAGE, user_id);
            startActivity(i);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void buildGraph(String[] all_q)
    {

        //чистим массив с раундами
        list.clear();
        //добавляем в массив данные раунда
        for(int i = 0; i < 5; i++)
        {
            this.list.add(all_q[i]);
            Toast.makeText(MainActivity.this, all_q[i], Toast.LENGTH_SHORT).show();
        }
        buildGraphWithArray();
    }

    //метод строит графику раунда
    public void buildGraphWithArray()
    {
        String fio = list.get(0);
        String card_num = list.get(1);
        String parent_id = list.get(2);
        String money_amount = list.get(3);
        String cashback_sum = list.get(4);

        TextView name_val = (TextView) findViewById(R.id.name_val);
        TextView iban_val = (TextView) findViewById(R.id.iban_val);
        TextView parent_val = (TextView) findViewById(R.id.parent_val);
        TextView money_amount_val = (TextView) findViewById(R.id.child_count_val);
        TextView cashback_val = (TextView) findViewById(R.id.cashback_val);

        name_val.setText(fio);
        iban_val.setText(card_num);
        parent_val.setText(parent_id);
        money_amount_val.setText(money_amount);
        cashback_val.setText(cashback_sum);
    }

    private class PostClass extends AsyncTask<String, Void, Void> {
        private final Context context;

        public PostClass(Context c){

            this.context = c;
        }

        @Override
        protected Void doInBackground(String... params) {
            String user_id = params[0];
            String price = params[1];
            //Toast.makeText(MainActivity.this, "user_id - "+user_id+"/price - "+price, Toast.LENGTH_SHORT).show();
            try {
                final StringBuilder output = new StringBuilder();

                URL url = new URL("http://accords.kz/buy_something.php");
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                //присваеваем элементу массива MESSAGE текст сообщения
                String urlParameters = "USER_ID="+user_id+"&PRICE="+price;
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

                MainActivity.this.runOnUiThread
                (new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            //приводим стринг билдер к обычному стрингу

                            String strList = ""+output;
                            //сплитом добавляем в строковый массив деля символами '//'
                            String[] array = strList.split("//");
                            Toast.makeText(MainActivity.this, "post request", Toast.LENGTH_SHORT).show();
                            buildGraph(array);
                        }
                    }
                );

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

