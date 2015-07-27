package it.scripto.fiscalcode;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Simple cities database access helper class.
 */
public class DatabaseAdapter {

    public static final String KEY_RIPARTIZIONE_GEOGRAFICA = "RipartizioneGeografica";
    public static final String KEY_CODICE_NUTS2_2010 = "CodiceNUTS2_2010";
    public static final String KEY_CODICE_NUTS3_2010 = "CodiceNUTS3_2010";
    public static final String KEY_CODICE_REGIONE = "CodiceRegione";
    public static final String KEY_CODICE_PROVINCIA = "CodiceProvincia";
    public static final String KEY_CODICE_CITTA_METROPOLITANA = "CodiceCittaMetropolitana";
    public static final String KEY_NUMERO_PROGRESSIVO_COMUNE = "NumeroProgressivoComune";
    public static final String KEY_CODICE_ISTAT_COMUNE_ALFANUMERICO = "CodiceIstatComuneAlfanumerico";
    public static final String KEY_CODICE_ISTAT_COMUNE_NUMERICO = "CodiceIstatComuneNumerico";
    public static final String KEY_CODICE_ISTAT_107_PROVINCE_NUMERICO = "CodiceIstat107ProvinceNumerico";
    public static final String KEY_CODICE_ISTAT_103_PROVINCE_NUMERICO = "CodiceIstat103ProvinceNumerico";
    public static final String KEY_CODICE_CATASTALE = "CodiceCatastale";
    public static final String KEY_DENOMINAZIONE_ITALIANO = "DenominazioneItaliano";
    public static final String KEY_DENOMINAZIONE_TEDESCO = "DenominazioneTedesco";
    public static final String KEY_CAPOLUOGO_PROVINCIA = "CapoluogoProvincia";
    public static final String KEY_ZONA_ALTIMETRICA = "ZonaAltimetrica";
    public static final String KEY_ALTITUDINE = "Altitudine";
    public static final String KEY_LITORANEO = "Litoraneo";
    public static final String KEY_MONTANO = "Montano";
    public static final String KEY_SUPERFICIE = "Superficie";
    public static final String KEY_POPOLAZIONE_2001 = "Popolazione2001";
    public static final String KEY_POPOLAZIONE_2011 = "Popolazione2011";

    private static final String TAG = "DatabaseAdapter";
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase sqLiteDatabase;

    private static final String DATABASE_NAME = "ISTAT_unita_amministrative.db";
    public static final String DATABASE_TABLE = "ElencoComuniItaliani";
    private static final int DATABASE_VERSION = 1;

    private final Context context;

    /**
     * Helper class that create a database from asset
     */
    private class DatabaseHelper extends SQLiteAssetHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created.
     *
     * @param context the Context within which to work
     */
    public DatabaseAdapter(Context context) {
        this.context = context;
    }

    /**
     * Open the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure.
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public DatabaseAdapter open() throws SQLException {
        databaseHelper = new DatabaseHelper(context);
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        databaseHelper.close();
    }

    /**
     * Return a Cursor over the list of all cities in the database.
     *
     * @return Cursor over all cities.
     */
    public Cursor fetchAllCities() {

        String table = DATABASE_TABLE;
        String[] columns = new String[] {KEY_CODICE_CATASTALE, KEY_DENOMINAZIONE_ITALIANO, KEY_DENOMINAZIONE_TEDESCO};
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = KEY_DENOMINAZIONE_ITALIANO + " ASC";

        return sqLiteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    private Cursor fetchCode(String city) {
        String table = DATABASE_TABLE;
        String[] columns = new String[] {KEY_CODICE_CATASTALE, KEY_DENOMINAZIONE_ITALIANO};
        String selection = KEY_DENOMINAZIONE_ITALIANO + " = ?";
        String[] selectionArgs = new String[] {city};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        return sqLiteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public String getCode(String city) {
        Cursor cursor = fetchCode(city);

        if (cursor.getCount() > 0) {
            String str = "";
            while (cursor.moveToNext()) {
                str = cursor.getString(cursor.getColumnIndex(KEY_CODICE_CATASTALE));
            }
            return str;
        } else {
            return "";
        }
    }

    public String[] getAllCitiesArray() {
        Cursor cursor = fetchAllCities();

        if (cursor.getCount() > 0) {
            String[] str = new String[cursor.getCount()];
            int i = 0;

            while (cursor.moveToNext()) {
                str[i] = cursor.getString(cursor.getColumnIndex(KEY_DENOMINAZIONE_ITALIANO));
                i++;
            }
            return str;
        } else {
            return new String[] {};
        }
    }

    /**
     * Return a Cursor over the list of all cities by provinceCode in the database.
     *
     * @param provinceCode the code of the province.
     * @return Cursor over all cities.
     */
    private Cursor fetchCitiesFromGivenProvinceWithLimit(String provinceCode, int limit) {
        String table = DATABASE_TABLE;
        String[] columns = new String[] {KEY_CODICE_CATASTALE, KEY_DENOMINAZIONE_ITALIANO, KEY_DENOMINAZIONE_TEDESCO, KEY_CODICE_PROVINCIA};
        String selection = KEY_CODICE_PROVINCIA + " = ?";
        String[] selectionArgs = new String[] {provinceCode};
        String groupBy = null;
        String having = null;
        String orderBy = KEY_DENOMINAZIONE_ITALIANO + " ASC";
        String limitString = String.valueOf(limit);

        return sqLiteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limitString);
    }

    private Cursor fetchCitiesFromGivenQueryWithLimit(String query, int limit) {
        String table = DATABASE_TABLE;
        String[] columns = new String[] {KEY_CODICE_CATASTALE, KEY_DENOMINAZIONE_ITALIANO, KEY_DENOMINAZIONE_TEDESCO, KEY_CODICE_PROVINCIA};
        String selection = KEY_DENOMINAZIONE_ITALIANO + " like ?";
        String[] selectionArgs = new String[] {query + "%"};
        String groupBy = null;
        String having = null;
        String orderBy = KEY_DENOMINAZIONE_ITALIANO + " ASC";
        String limitString = String.valueOf(limit);

        return sqLiteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limitString);
    }

    private Cursor fetchCitiesFromGivenQueryAndProvinceWithLimiti(String query, String provinceCode, int limit) {
        String table = DATABASE_TABLE;
        String[] columns = new String[] {KEY_CODICE_CATASTALE, KEY_DENOMINAZIONE_ITALIANO, KEY_DENOMINAZIONE_TEDESCO, KEY_CODICE_PROVINCIA};
        String selection = KEY_CODICE_PROVINCIA + " = ? AND " + KEY_DENOMINAZIONE_ITALIANO + " like ?";
        String[] selectionArgs = new String[] {provinceCode, query + "%"};
        String groupBy = null;
        String having = null;
        String orderBy = KEY_DENOMINAZIONE_ITALIANO + " ASC";
        String limitString = String.valueOf(limit);

        return sqLiteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limitString);
    }
}