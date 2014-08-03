package com.example.android.sunshine;

import android.net.Uri;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute("Armenia");
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

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]>{

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        private String getReadableDateString(long time){
            //Debido a que la API retorna un timestamp unix (expresado en segundos)
            //se debe convertir a milisegundos para ser convertida a una fecha valida
            Date date = new Date(time * 1000);
            SimpleDateFormat format = new SimpleDateFormat("E, MMM, d");
            return  format.format(date).toString();
        }

        /*
         * Preparar los high/low del clima para la presentacion
         */
        private String formatHighLows(double high, double low){
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        /*
         * Toma un String con la representacion completa del clima en formato JSON y tomamos
         * los Strings necesarios
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            //Nombres de los objetos JSON que necesitamos extraer
            final String OWN_LIST = "list";
            final String OWN_WEATHER = "weather";
            final String OWN_TEMPERATURE = "temp";
            final String OWN_MAX = "max";
            final String OWN_MIN = "min";
            final String OWN_DATETIME = "dt";
            final String OWN_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWN_LIST);

            String[] resultStr = new String[numDays];
            for (int i = 0; i < resultStr.length; i++){
                //Por ahora, usaremos el formato "Dia, descripcion, high/low"
                String day;
                String description;
                String highAndLow;

                //Obtener el JSON object que representa el dia
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                //El date/time es retornado como un long. Necesitamos convertirlo
                //a algo legible por un humano
                long dateTime = dayForecast.getLong(OWN_DATETIME);
                day = getReadableDateString(dateTime);

                //La descripcion esta en un arreglo hijo llamado "weather" que es 1 elemento long
                JSONObject weatherObject = dayForecast.getJSONArray(OWN_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWN_DESCRIPTION);

                //La temperatura esta en un arregl hijo llamado "temp"
                JSONObject temperatureObject = dayForecast.getJSONObject(OWN_TEMPERATURE);
                double high = temperatureObject.getDouble(OWN_MAX);
                double low = temperatureObject.getDouble(OWN_MIN);

                highAndLow = formatHighLows(high, low);
                resultStr[i] = day + " - " + description + " - " + highAndLow;
            }

            for (String s : resultStr){
                Log.v(LOG_TAG, "Forecast Entry: " + s);
            }
            return resultStr;
        }

        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0){
                return null;
            }

            //Usar API para el Clima con peticiones HTTP
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //Aqui se almacenara la respuesta JSON como String
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;

            try {
                //Construir la Url para la consulta OpenWeatherMap con los posibles
                //parametros disponibles en http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .build();

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                URL url = new URL(builtUri.toString());
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

                Log.v(LOG_TAG, "Forecast JSON String: " + forecastJsonStr);
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
            try {
                return getWeatherDataFromJson(forecastJsonStr, numDays);
            }catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            //Esto solo ocurrira si hubo un error obteniendo o formateando el forecast
            return null;
        }
    }
}
