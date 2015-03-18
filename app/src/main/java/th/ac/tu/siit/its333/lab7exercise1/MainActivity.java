package th.ac.tu.siit.its333.lab7exercise1;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity {
    int oldcase =1 ;
    int newcase ;

double ts,tn;
    Long tsLong,tnLong,tsLongmin,tnLongmin;


    //1min = 60000 milli

    public void savetime(){
        tsLong = System.currentTimeMillis();
        //tsLongmin = TimeUnit.MILLISECONDS.toMinutes(tsLong);
        //ts = tsLong.doubleValue();

    }

    public boolean checktime(){
        tnLong = System.currentTimeMillis();
        //tnLongmin = TimeUnit.MILLISECONDS.toMinutes(tnLong);
        //tn = tnLong.doubleValue();


        if( tnLong-tsLong < 5000 && oldcase == newcase){



            Toast toast = Toast.makeText(getApplicationContext(), "please try again later in 1 minute", Toast.LENGTH_SHORT);
            toast.show();

            return false;
        }

        else return true;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        savetime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        WeatherTask w = new WeatherTask();
        if(newcase == 1) {
            savetime();
        w.execute("http://ict.siit.tu.ac.th/~cholwich/bangkok.json", "Bangkok Weather");
    }
        else if ( newcase == 2){
            savetime();
            w.execute("http://ict.siit.tu.ac.th/~cholwich/pathumthani.json", "Pathumthani Weather");
        }

        else if (newcase == 3){
                savetime();
            w.execute("http://ict.siit.tu.ac.th/~cholwich/nonthaburi.json", "Nonthaburi Weather");
        }
    }

    public void buttonClicked(View v) {
        int id = v.getId();
        WeatherTask w = new WeatherTask();
        switch (id) {
            case R.id.btBangkok:
                newcase = 1;
                checktime();
                if(checktime() == true) {
                    w.execute("http://ict.siit.tu.ac.th/~cholwich/bangkok.json", "Bangkok Weather");
                    //newcase = 1;
                    savetime();
                    oldcase = newcase ;

                }

                break;


            case R.id.btPathum:
                newcase = 2;
                checktime();
                if(checktime() == true){
                w.execute("http://ict.siit.tu.ac.th/~cholwich/pathumthani.json", "Phatumthani Weather");


                //newcase = 2;
                savetime();
                    oldcase = newcase ;

                }

                break;
            case R.id.btNon:
                newcase = 3;
                checktime();
                if(checktime() == true) {
                w.execute("http://ict.siit.tu.ac.th/~cholwich/nonthaburi.json", "Nonthaburi Weather");
                //newcase = 3;
                savetime();
                    oldcase = newcase ;

                }
                break;

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    class WeatherTask extends AsyncTask<String, Void, Boolean> {
        String errorMsg = "";
        ProgressDialog pDialog;
        String title,cloud;

        double windSpeed,ktemp,ktempmin,ktempmax,temp,tempmin,tempmax,humid;



        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading weather data ...");
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            BufferedReader reader;
            StringBuilder buffer = new StringBuilder();
            String line;
            try {
                title = params[1];
                URL u = new URL(params[0]);
                HttpURLConnection h = (HttpURLConnection)u.openConnection();
                h.setRequestMethod("GET");
                h.setDoInput(true);
                h.connect();

                int response = h.getResponseCode();
                if (response == 200) {
                    reader = new BufferedReader(new InputStreamReader(h.getInputStream()));
                    while((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    //Start parsing JSON
                    JSONObject jWeather = new JSONObject(buffer.toString());
                    JSONObject jWind = jWeather.getJSONObject("wind");
                    JSONObject jtemp = jWeather.getJSONObject("main");
                    JSONArray jcloud = jWeather.getJSONArray("weather");
                    ktemp = jtemp.getDouble("temp");
                    ktempmin = jtemp.getDouble("temp_min");
                    ktempmax = jtemp.getDouble("temp_max");
                    humid = jtemp.getDouble("humidity");

                    cloud = jcloud.getJSONObject(0).getString("main");

                    temp = ktemp - 273.15 ;
                    tempmin = ktempmin -273.15;
                    tempmax = ktempmax - 273.15 ;



                    windSpeed = jWind.getDouble("speed");
                    errorMsg = "";
                    return true;
                }
                else {
                    errorMsg = "HTTP Error";
                }
            } catch (MalformedURLException e) {
                Log.e("WeatherTask", "URL Error");
                errorMsg = "URL Error";
            } catch (IOException e) {
                Log.e("WeatherTask", "I/O Error");
                errorMsg = "I/O Error";
            } catch (JSONException e) {
                Log.e("WeatherTask", "JSON Error");
                errorMsg = "JSON Error";
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            TextView tvTemp,tvTitle, tvWeather, tvWind,tvHumid;
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            tvTitle = (TextView)findViewById(R.id.tvTitle);
            tvWeather = (TextView)findViewById(R.id.tvWeather);
            tvWind = (TextView)findViewById(R.id.tvWind);
            tvTemp = (TextView)findViewById(R.id.tvTemp);
            tvHumid = (TextView)findViewById(R.id.tvHumid);


            if (result) {
                tvTitle.setText(title);
                tvWind.setText(String.format("%.1f", windSpeed));
                tvTemp.setText(String.format("%.1f" + "(max = " + "%.1f" + ", min = " + "%.1f" + ")" , temp,tempmax,tempmin));
                tvHumid.setText(String.format("%.1f",humid));
                tvWeather.setText(String.format("%s",cloud));




            }
            else {
                tvTitle.setText(errorMsg);
                tvWeather.setText("");
                tvWind.setText("");
            }
        }
    }
}
