package com.example.android.sunshine.data;

import android.provider.BaseColumns;

/**
 * Created by Yiyo on 09/08/2014.
 */
public class WeatherContract {

    //Clase interna que define los contenidos de la tabla del clima
    public static class WeatherEntry implements BaseColumns{

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
    }

    //Clase interna que define los contenidos de la tabla del clima
    public static class LocationEntry implements BaseColumns{

        public static final String TABLE_NAME = "location";

        //El location setting es el String que sera enviado a openweathermap
        //como consulta por localizacion
        public static final String COLUMN_LOCATION_SETTING = "location_setting";

        //Nombre de la ciudad en un formato legible por un humano
        public static final String COLUMN_CITY_NAME = "city_name";

        //Almacenamos la latitud y la longitud devueltos por openweathermap
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";
    }

}
