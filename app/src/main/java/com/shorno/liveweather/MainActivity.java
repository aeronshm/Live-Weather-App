package com.shorno.liveweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    TextView geoView, tempView,lookView , feelView , humidView;
    EditText city;

    VideoView vv;

    public void clearAll(){
        geoView.setText("");
        tempView.setText("");
        lookView.setText("");
        feelView.setText("");
        humidView.setText("");
    }
    public void getWeather(View view){
        clearAll();
        weatherTask task = new weatherTask();
        try {
            // task.execute("https://api.openweathermap.org/data/2.5/weather?q="+city.getText().toString()+"&appid=649dafda2d10341ad5d90aced42bf396");

            //http://api.weatherapi.com/v1/current.json?key=ed105a27eb1a4a7d97b41820231104&q=silchar&aqi=yes
            task.execute("http://api.weatherapi.com/v1/current.json?key=ed105a27eb1a4a7d97b41820231104&q="+city.getText().toString()+"&aqi=yes");
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(city.getWindowToken(),0);

        }catch (Exception e){
            e.getStackTrace();

        }

    }

    public void dynamicBg(){

        String path ="android.resource://com.shorno.liveweather/"+R.raw.bg;
        Uri u = Uri.parse(path);
        vv.setVideoURI(u);
        vv.start();

        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         geoView= findViewById(R.id.geoView);
        tempView= findViewById(R.id.tempView);
        lookView= findViewById(R.id.lookView);
        feelView= findViewById(R.id.feelView);
        humidView= findViewById(R.id.humidView);
        city = findViewById(R.id.city);
        vv= findViewById(R.id.vv);

        dynamicBg();

    }



    public class weatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection connection = null;
            try{
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1){
                    char current = (char) data;
                    result+=current;

                    data = reader.read();
                }
                return result;

            }catch (Exception e){
                e.getStackTrace();
                //Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject obj = new JSONObject(s);
                //location obj
                JSONObject LocationInfo = obj.getJSONObject("location");
                //geoView
                String name = LocationInfo.getString("name");
                String region = LocationInfo.getString("region");
                String country = LocationInfo.getString("country");

                //current obj
                JSONObject tempInfo = obj.getJSONObject("current");
                //tempView
                Double temp = tempInfo.getDouble("temp_c");
                //humidView
                Double humid = tempInfo.getDouble("humidity");
                //feelView
                Double feels = tempInfo.getDouble("feelslike_c");

                //condition obj inside current obj
                JSONObject lookslike = tempInfo.getJSONObject("condition");
                //lookView
                String condition = lookslike.getString("text");

                String geoMsg= "";

                if(!name.equals("") && !region.equals("") && !country.equals("")){
                    geoView.setText( name + ", "+region+ ", " + country );
                }if (!geoMsg.equals("")){
                    geoView.setText(geoMsg);
                }

                if (!temp.equals("")){
                    tempView.setText(String.valueOf(temp)+ " °c");
                }

                if (!humid.equals("")){
                    humidView.setText("Humidity : "+String.valueOf(humid) + "%");
                }

                if(!feels.equals("")){
                    feelView.setText("Feels like : "+ String.valueOf(feels) + " °c");
                }

                if(!condition.equals("")){
                    lookView.setText(condition);
                }


                else {
                    Toast.makeText(getApplicationContext(), "Could not find weather ", Toast.LENGTH_SHORT).show();
                }



            }catch(Exception e){
                Toast.makeText(getApplicationContext(), "Could not find Location", Toast.LENGTH_SHORT).show();
                e.getStackTrace();


            }
          //  String anibg = lookView.getText();


             /*  if (anibg.equals("Sunny")){
            String path ="android.resource://com.shorno.liveweather/"+R.raw.cool;
            Uri u = Uri.parse(path);
            vv.setVideoURI(u);
            vv.start();

            vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setLooping(true);
                }
            });
        } */

        }
    }



}
