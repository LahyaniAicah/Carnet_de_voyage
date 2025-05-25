package fr.upjv.carnet_de_voyage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "carnet.db";
    public static final int DB_VERSION = 1;

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
                    COL_DATEF + " TEXT NOT NULL, " +
                    COL_ENRGPS + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_VOYAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOYAGE);
        onCreate(db);
    }
}
