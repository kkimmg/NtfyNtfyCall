package k_kim_mg.ntfyntfycall;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class RecvListProvider extends ContentProvider {
    public static final String PROVIDER_NAME = "kkimmg.ntfyntfycall.dbprovider";
    private static final String DB_NAME = "shit.db";
    public static final String TABLE_NAME = "RECVLIST";
    private static final int DB_VERSION = 1;
    public static final Uri CONTENT_EVENTS = Uri.parse("content://" + PROVIDER_NAME + "/" + TABLE_NAME);
    private SQLiteDatabase db;
    private static final int TYPE_LIST = 0;
    private static final int TYPE_DEVICE = 1;
    private static final UriMatcher uriMatcher;
    private static final class InnerHelper extends SQLiteOpenHelper {
        public InnerHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL( //
                    "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( " + //
                            "_ID INTEGER PRIMARY KEY AUTOINCREMENT," + //
                            "_DATETIME NUMERIC," + //
                            "_TYPE INTEGER DEFAULT 0," + //
                            "DEVICENAME TEXT);");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion > oldVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
                onCreate(db);
            }
        }
    }
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, TABLE_NAME + "/#", TYPE_DEVICE);
        uriMatcher.addURI(PROVIDER_NAME, TABLE_NAME, TYPE_LIST);
    }
    public RecvListProvider() {
    }


    @Override
    public boolean onCreate() {
        Context context = getContext();
        InnerHelper innerHelper = new InnerHelper(context);
        db = innerHelper.getWritableDatabase();
        innerHelper.onCreate(db);
        return (db != null);
    }

    @Override
    public int delete(Uri uri, String whereClause, String[] whereArgs) {
        int ret = 0;
        switch (uriMatcher.match(uri)) {
            case TYPE_DEVICE:
                String id = uri.getPathSegments().get(1);
                whereClause = "_ID = " + id;
                ret = db.delete(TABLE_NAME, whereClause, whereArgs);
                break;
            case TYPE_LIST:
                ret = db.delete(TABLE_NAME, whereClause, whereArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ret;
    }
    @Override
    public String getType(Uri uri) {
        String ret = null;
        switch (uriMatcher.match(uri)) {
            case TYPE_LIST:
                ret = "vnd.android.cursor.dir/events";
                break;
            case TYPE_DEVICE:
                ret = "vnd.android.cursor.item/events";
                break;
        }
        return ret;
    }
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // db.execSQL("INSERT INTO EVENTS (_TYPE, _DATETIME, LATITUDE, LONGITUDE) VALUES (0, 123456767, 37.422005, -122.084095)");
        Uri ret = null;
        long row = db.insert(TABLE_NAME, null, values);
        if (row > 0) {
            ret = ContentUris.withAppendedId(CONTENT_EVENTS, row);
            getContext().getContentResolver().notifyChange(ret, null);
        }
        return ret;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(TABLE_NAME);
        if (uriMatcher.match(uri) == TYPE_DEVICE) {
            String id = uri.getPathSegments().get(1);
            builder.appendWhere("_ID = " + id);
        }
        if (sortOrder == null || sortOrder == "") {
            sortOrder = "_ID";
        }
        Cursor ret = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        ret.setNotificationUri(getContext().getContentResolver(), uri);
        return ret;
    }
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int ret = 0;
        switch (uriMatcher.match(uri)) {
            case TYPE_DEVICE:
                String id = uri.getPathSegments().get(1);
                selection = "_ID = " + id;
                ret = db.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            case TYPE_LIST:
                ret = db.update(TABLE_NAME, values, selection, selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ret;
    }
}
