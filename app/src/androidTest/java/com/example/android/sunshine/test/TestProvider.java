package com.example.android.sunshine.test;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.android.sunshine.data.WeatherContract.LocationEntry;
import com.example.android.sunshine.data.WeatherContract.WeatherEntry;
import com.example.android.sunshine.data.WeatherDbHelper;

import java.util.Map;
import java.util.Set;

public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void testDeleteDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    public static String TEST_CITY_NAME = "North Pole";
    public static String TEST_LOCATION = "99705";
    public static String TEST_DATE = "20141205";

    static ContentValues getLocationContentValues() {

        ContentValues values = new ContentValues();
        String testLocationSetting = "99705";
        double testLatitude = 64.772;
        double testLongitude = -147.355;
        values.put(LocationEntry.COLUMN_CITY_NAME, TEST_CITY_NAME);
        values.put(LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        values.put(LocationEntry.COLUMN_COORD_LAT, testLatitude);
        values.put(LocationEntry.COLUMN_COORD_LONG, testLongitude);
        return values;
    }

    static public ContentValues getWeatherContentValues(long locationRowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroides");
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);
        return weatherValues;
    }

    static public void validateCursor(ContentValues expectedValues, Cursor valueCursor) {

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(-1 == idx);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }

    public void testInsertReadProvider() {

        //Crear un nuevo mapa de valores, donde los nombres de las columnas son las llaves
        ContentValues values = getLocationContentValues();

        Uri locationUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

        //Verificamos que tenemos una fila de regreso
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        //Un cursor es tu interfaz primaria para los resultados de una query
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.buildLocationUri(locationRowId),   //Tabla de la consulta
                null,
                null,   //Columnas para la clausula "WHERE"
                null,   //Valores para la clausula "WHERE"
                null   //Ordenamiento
        );

        if (cursor.moveToFirst()) {

            validateCursor(values, cursor);
            //Ahora que ya tenemos datos falsos de una localizacion, es hora de agregar datos
            //falsos para un clima
            ContentValues weatherValues = getWeatherContentValues(locationRowId);
            Uri insertUri = mContext.getContentResolver().insert(WeatherEntry.CONTENT_URI, weatherValues);
            long weatherRowId = ContentUris.parseId(insertUri);

            Cursor weatherCursor = mContext.getContentResolver().query(
                    WeatherEntry.CONTENT_URI,
                    null,   //Dejando las columnas en null trae todas las columnas
                    null,   //Columnas para la clausula Where
                    null,   //Valores para la clausula Where
                    null    //Ordenamiento
            );

            if (weatherCursor.moveToFirst()) {
                validateCursor(weatherValues, weatherCursor);
            } else {
                fail("No se devolvieron datos climaticos!");
            }

            weatherCursor.close();

            weatherCursor = mContext.getContentResolver().query(
                    WeatherEntry.buildWeatherLocation(TEST_LOCATION),
                    null,   //Dejando las columnas en null trae todas las columnas
                    null,   //Columnas para la clausula Where
                    null,   //Valores para la clausula Where
                    null    //Ordenamiento
            );

            if (weatherCursor.moveToFirst()) {
                validateCursor(weatherValues, weatherCursor);
            } else {
                fail("No se devolvieron datos climaticos!");
            }

            weatherCursor.close();

            weatherCursor = mContext.getContentResolver().query(
                    WeatherEntry.buildWeatherLocationWithStartDate(TEST_LOCATION, TEST_DATE),
                    null,   //Dejando las columnas en null trae todas las columnas
                    null,   //Columnas para la clausula Where
                    null,   //Valores para la clausula Where
                    null    //Ordenamiento
            );

            if (weatherCursor.moveToFirst()) {
                validateCursor(weatherValues, weatherCursor);
            } else {
                fail("No se devolvieron datos climaticos!");
            }

            weatherCursor.close();

            weatherCursor = mContext.getContentResolver().query(
                    WeatherEntry.buildWeatherLocationWithDate(TEST_LOCATION, TEST_DATE),
                    null,   //Dejando las columnas en null trae todas las columnas
                    null,   //Columnas para la clausula Where
                    null,   //Valores para la clausula Where
                    null    //Ordenamiento
            );

            if (weatherCursor.moveToFirst()) {
                validateCursor(weatherValues, weatherCursor);
            } else {
                fail("No se devolvieron datos climaticos!");
            }

        } else {
            fail("No se retornaron valores =(");
        }
    }

    public void testGetType() {
        // content://com.example.android.sunshine/weather/
        String type = mContext.getContentResolver().getType(WeatherEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine/weather
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String testLocation = "94074";
        // content://com.example.android.sunshine/weather/94074
        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocation(testLocation));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String testDate = "20140612";
        // content://com.example.android.sunshine/weather/94074/20140612
        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocationWithDate(testLocation, testDate));
        // vnd.android.cursor.item/com.example.android.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_ITEM_TYPE, type);

        // content://com.example.android.sunshine/location/
        type = mContext.getContentResolver().getType(LocationEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/location
        assertEquals(LocationEntry.CONTENT_TYPE, type);

        // content://com.example.android.sunshine/location/1
        type = mContext.getContentResolver().getType(LocationEntry.buildLocationUri(1L));
        // vnd.android.cursor.item/com.example.android.sunshine/location
        assertEquals(LocationEntry.CONTENT_ITEM_TYPE, type);
    }
}
