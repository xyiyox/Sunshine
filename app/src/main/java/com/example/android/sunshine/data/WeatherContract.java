package com.example.android.sunshine.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Yiyo on 09/08/2014.
 */
public class WeatherContract {

    //El "Content Authority" es un nombre para el Content Provider, similar a la relacion entre
    //un nombre de dominio y su website. Un String conveniente para el "Content Authority" es el
    //nombre del paquete de la aplicacion, lo cual garantiza que sea unico en el dispositivo
    public static final String CONTENT_AUTHORITY = "com.example.android.sunshine";

    //Usa el CONTENT_AUTHORITY para crear la base de todas las URI que las apps van a usar para
    //contactar el Content Provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Posibles rutas que se agregarian a la base
    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";

    //Clase interna que define los contenidos de la tabla del clima
    public static class WeatherEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        public static final String TABLE_NAME = "weather";

        //Columna con la llave foranea en la tabla Location
        public static final String COLUMN_LOC_KEY = "location_id";
        //Fecha almacenada como texto con formato yyyy-MM-dd
        public static final String COLUMN_DATETEXT = "date";
        //Weather id retornado por la API, para identificar el icono a ser usado
        public static final String COLUMN_WEATHER_ID = "weather_id";

        //Descripcion corta y descripcion larga del clima, proveida por la API
        public static final String COLUMN_SHORT_DESC = "short_desc";

        //Temperaturas Minimas y Maximas para el dia
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        //La humedad es almacenada como un flotante representando porcentaje
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_PRESSURE = "pressure";

        //La velocidad del viento es almacenada como un flotante representada en mph
        public static final String COLUMN_WIND_SPEED = "wind";

        //Grados meteorologicos (ej: 0 es norte, 180 es sur)
        public static final String COLUMN_DEGREES = "degrees";

        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWeatherLocation(String locationSetting) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        public static Uri buildWeatherLocationWithStartDate(
                String locationSetting, String startDate) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_DATETEXT, startDate).build();
        }

        public static Uri buildWeatherLocationWithDate(String locationSetting, String date) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).appendPath(date).build();
        }

        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getDateFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static String getStartDateFromUri(Uri uri) {
            return uri.getQueryParameter(COLUMN_DATETEXT);
        }
    }

    //Clase interna que define los contenidos de la tabla del clima
    public static class LocationEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String TABLE_NAME = "location";

        //El location setting es el String que sera enviado a openweathermap
        //como consulta por localizacion
        public static final String COLUMN_LOCATION_SETTING = "location_setting";

        //Nombre de la ciudad en un formato legible por un humano
        public static final String COLUMN_CITY_NAME = "city_name";

        //Almacenamos la latitud y la longitud devueltos por openweathermap
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";

        public static Uri buildLocationUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }
    }

}
