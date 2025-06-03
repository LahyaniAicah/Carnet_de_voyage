package fr.upjv.carnet_de_voyage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Infos générales
    public static final String DB_NAME = "carnet.db";
    public static final int DB_VERSION = 2; // ✅ version incrémentée pour appliquer les changements

    // Table voyages
    public static final String TABLE_VOYAGE = "voyages";
    public static final String COL_ID = "id";
    public static final String COL_TITRE = "titre";
    public static final String COL_DESC = "description";
    public static final String COL_DATED = "date_debut";
    public static final String COL_DATEF = "date_fin";
    public static final String COL_ENRGPS = "enr_gps";

    private static final String CREATE_VOYAGES =
            "CREATE TABLE " + TABLE_VOYAGE + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_TITRE + " TEXT NOT NULL, " +
                    COL_DESC + " TEXT, " +
                    COL_DATED + " TEXT NOT NULL, " +
                    COL_DATEF + " TEXT, " + // ✅ plus de NOT NULL ici
                    COL_ENRGPS + " TEXT);";

    // Table positions
    public static final String TABLE_POSITION = "positions";
    public static final String COL_LAT = "latitude";
    public static final String COL_LON = "longitude";
    public static final String COL_DATE = "datetime";
    public static final String COL_VID = "voyage_id";

    private static final String CREATE_POSITIONS =
            "CREATE TABLE " + TABLE_POSITION + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_LAT + " REAL, " +
                    COL_LON + " REAL, " +
                    COL_DATE + " TEXT, " +
                    COL_VID + " INTEGER);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_VOYAGES);   // ✅ une seule fois
        db.execSQL(CREATE_POSITIONS); // ✅ création de la 2e table
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOYAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSITION); // ✅ supprime aussi la table position
        onCreate(db); // ✅ recrée les deux
    }
}
