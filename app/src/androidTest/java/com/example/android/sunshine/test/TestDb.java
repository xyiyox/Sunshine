package com.example.android.sunshine.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.android.sunshine.data.WeatherContract.LocationEntry;
import com.example.android.sunshine.data.WeatherContract.WeatherEntry;
import com.example.android.sunshine.data.WeatherDbHelper;

import java.util.Map;
import java.util.Set;

public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public static String TEST_CITY_NAME = "North Pole";

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

    public void testInsertReadDb() {
        //Prueba para los datos que se van a insertar en la BD
        WeatherDbHelper dbHelper =
                new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Crear un nuevo mapa de valores, donde los nombres de las columnas son las llaves
        ContentValues values = getLocationContentValues();

        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, values);

        //Verificamos que tenemos una fila de regreso
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        //Un cursor es tu interfaz primaria para los resultados de una query
        Cursor cursor = db.query(
                LocationEntry.TABLE_NAME,   //Tabla de la consulta
                null,
                null,   //Columnas para la clausula "WHERE"
                null,   //Valores para la clausula "WHERE"
                null,   //Columnas para group by
                null,   //Columnas para filtrar por row groups
                null   //Ordenamiento
        );

        if (cursor.moveToFirst()) {

            validateCursor(values, cursor);
            //Ahora que ya tenemos datos falsos de una localizacion, es hora de agregar datos
            //falsos para un clima
            ContentValues weatherValues = getWeatherContentValues(locationRowId);
            long weatherRowId;
            weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);
            assertTrue(weatherRowId != -1);

            //Un cursor es tu interfaz primaria para los resultados de una query
            Cursor weatherCursor = db.query(
                    WeatherEntry.TABLE_NAME,    //Tabla de la consulta
                    null,   //Dejando las columnas en null se retornan todas las columnas
                    null,   //Clausula WHERE
                    null,   //Valores para la clausula WHERE
                    null,   //Columnas del GROUP BY
                    null,   //Columnas para filtrar por row groups
                    null    //ordenamiento
            );

            if (weatherCursor.moveToFirst()) {
                validateCursor(weatherValues, weatherCursor);
            } else {
                fail("No se devolvieron datos climaticos!");
            }
            dbHelper.close();
        } else {
            fail("No se retornaron valores =(");
        }
    }
}
