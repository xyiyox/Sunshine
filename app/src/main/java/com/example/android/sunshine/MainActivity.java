package com.example.android.sunshine;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            //Datos de prueba para el ListView.
            //Representados como "dia, clima, high/low"
            String[] forecastArray = {
                    "Lunes - Soleado - 88/63 ",
                    "Martes - Neblina - 70/40",
                    "Miercoles - Nublado - 72/63",
                    "Jueves - Asteroides - 75/65",
                    "Viernes - Lluvia Fuerte - 65/56",
                    "Sabado - AYUDA...ATRAPADO EN ESTACION DEL CLIMA - 60/51",
                    "Domingo - Soleado - 80/68"
            };
            List<String> weekForecast = new ArrayList<String>(
                    Arrays.asList(forecastArray)
            );

            ArrayAdapter<String> mForecastAdapter = new ArrayAdapter<String>(
                    //Contexto actual
                    getActivity(),
                    //Id del layout de la lista
                    R.layout.list_item_forecast,
                    //Id del textview para el listado
                    R.id.list_item_forecast_textview,
                    //Datos
                    weekForecast
            );

            ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
            listView.setAdapter(mForecastAdapter);

            //Usar API para el Clima con peticiones HTTP
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //Aqui se almacenara la respuesta JSON como String
            String forecastJsonStr = null;

            try {
                //Construir la Url para la consulta OpenWeatherMap con los posibles
                //parametros disponibles en http://openweathermap.org/API#forecast
                URL url = new URL(
                        "http://api.openweathermap.org/data/2.5/forecast/daily?q=Armenia&mode=json&units=metric&cnt=7"
                );

                //Crear la peticion a OpenWeatherMap y abrir la conexion
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Leer el Input Stream y guardarlo como String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null){
                    //Nada que hacer
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null){
                    //Mientras sea JSON, agregar una nueva linea no es necesario
                    //Pero esto hace la depuracion mas sencilla si se quiere imprimir
                    //el buffer completo
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0){
                    //Stream esta vacio
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();

            }catch (IOException e){
                Log.e("PlaceholderFragment", "Error", e);
                //Si no se consiguieron los datos del clima con exito no tiene caso analizarlo
                forecastJsonStr = null;
            }finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
                if (reader != null){
                    try {
                        reader.close();
                    }catch (final IOException e){
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            return rootView;
        }
    }
}
