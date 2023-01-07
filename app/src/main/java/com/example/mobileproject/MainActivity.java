package com.example.mobileproject;
import static java.lang.String.valueOf;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Button konwertuj;
    EditText wartosc;
    String wybranaWaluta;
    float srednia;
    TextView rezultat, data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = findViewById(R.id.Data);
        JodaTime jd = new JodaTime();
        data.setText(jd.jodaTime());

        konwertuj = findViewById(R.id.buttonKonwertuj);
        rezultat = findViewById(R.id.textViewResult);
        wartosc = findViewById(R.id.editTextNumberPLN);

        Spinner Waluta = (Spinner) findViewById(R.id.spinnerWaluta);
        Waluta.setOnItemSelectedListener(this);
        List<String> list = new ArrayList<String>();
        list.add("THB");
        list.add("USD");
        list.add("AUD");
        list.add("HKD");
        list.add("CAD");
        list.add("NZD");
        list.add("SGD");
        list.add("EUR");
        list.add("HUF");
        list.add("CHF");
        list.add("GBP");
        list.add("UAH");
        list.add("JPY");
        list.add("CZK");
        list.add("DKK");
        list.add("ISK");
        list.add("NOK");
        list.add("SEK");
        list.add("HRK");
        list.add("RON");
        list.add("BGN");
        list.add("TRY");
        list.add("ILS");
        list.add("CLP");
        list.add("PHP");
        list.add("MXN");
        list.add("ZAR");
        list.add("BRL");
        list.add("MYR");
        list.add("RUB");
        list.add("IDR");
        list.add("INR");
        list.add("KRW");
        list.add("CNY");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Waluta.setAdapter(adapter);



        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor szejker = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorEventListener sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                if(sensorEvent != null){
                    float x_accl = sensorEvent.values[0];
                    float y_accl = sensorEvent.values[1];
                    float z_accl = sensorEvent.values[2];

                    if(y_accl > 14 || y_accl < -14){
                        wartosc.setText(losuj());
                    }
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        sensorManager.registerListener(sensorEventListener, szejker, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        wybranaWaluta = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        wybranaWaluta = parent.getItemAtPosition(0).toString();

    }
    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... parms) {
            String json = "";
            URL url;
            HttpURLConnection urlConnection = null;
            String wybranaWaluta = parms[0];
            try {
                url = new URL("https://api.nbp.pl/api/exchangerates/rates/a/"+wybranaWaluta+"/?format=json");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();

                while (data != -1) {
                    char letter = (char) data;
                    json += letter;
                    data = reader.read();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            try {
                JSONObject jsonObject = new JSONObject(json);
                String rates = jsonObject.getString("rates");
                JSONArray array = new JSONArray(rates);
                JSONObject mid = array.getJSONObject(0);
                srednia = (float) mid.getDouble("mid");
                if(wartosc.getText().toString().isEmpty()){
                    wartosc.setText("1");
                }
                float f = (float) (Float.parseFloat(wartosc.getText().toString()) / srednia);
                rezultat.setText(valueOf(wartosc.getText()+" PLN to " +String.format("%.2f",f)+ " " + wybranaWaluta));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }
    public void konwertuj(View view){
        DownloadTask task = new DownloadTask();
        task.execute(wybranaWaluta);

    }

    public String losuj(){
        int l = (int) (Math.random()*100);
        int p = (int) (Math.random()*100);
        String value = l + "." + p;
        return value;
    }
}