package com.paezand.weatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    TextView weatherTextView;
    Button searchWeatherButton;
    EditText cityNameEditText;

    WeatherRequest weatherRequest = new WeatherRequest();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherTextView = (TextView) findViewById(R.id.weather_response_text_view);
        searchWeatherButton = (Button) findViewById(R.id.search_weather_button);
        cityNameEditText = (EditText) findViewById(R.id.city_name_edit_text);
    }

    public void findWeather(View view) {

        try {
            String city = URLEncoder.encode(cityNameEditText.getText().toString(), "UTF-8");
            weatherRequest.execute("http://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=49518428289cc0c855e928809c57528f").get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class WeatherRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(strings[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;

                    result += current;

                    data = reader.read();
                }

                return result;

            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            try {
                JSONObject jsonObject = new JSONObject(result);

                String weatherInfo = jsonObject.getString("weather");

                JSONArray jsonArray = new JSONArray(weatherInfo);

                for (int i=0; i< jsonArray.length(); i++) {
                    JSONObject singleResult =  jsonArray.getJSONObject(i);

                    Log.d("MainActivity","main: " + singleResult.getString("main"));
                    Log.d("MainActivity","description: " + singleResult.getString("description"));

                    String main = singleResult.getString("main");
                    String description = singleResult.getString("description");
                    if (main != null && description != null) {
                        weatherTextView.setText(main + "\n" + description);
                    } else {
                        weatherTextView.setText("Doesn't find any result!!");
                    }
                }

            } catch(Exception e) {
                e.printStackTrace();
            }

        }
    }
}
