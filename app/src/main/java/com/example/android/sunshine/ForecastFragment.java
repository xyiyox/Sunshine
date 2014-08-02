package com.example.android.sunshine;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Agregar esta linea para que este fragmento pueda manejar eventos de menu
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Referencia del ListView y settear el adaptador a el
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<Void, Void, Void>{

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {

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
                    return null;
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
                    return null;
                }
                forecastJsonStr = buffer.toString();
            }catch (IOException e){
                Log.e(LOG_TAG, "Error", e);
                //Si no se consiguieron los datos del clima con exito no tiene caso analizarlo
                return null;
            }finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
                if (reader != null){
                    try {
                        reader.close();
                    }catch (final IOException e){
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }
    }
}
