package com.github.warren_bank.mpcfreemote.local_settings;

import com.github.warren_bank.mpcfreemote.model.Server;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;

import java.util.ArrayList;
import java.util.List;

public class Bookmarks extends LocalSettings {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "bookmarks.db";
    private static final String TABLE_NAME = "bookmarks";
    private static final String COLUMN_HOSTNAME = "hostname";
    private static final String COLUMN_PORT = "port";
    private static final String COLUMN_PATH = "path";

    public Bookmarks(Context context) {
        super(context, DB_NAME, DB_VERSION);
    }

    @Override
    protected String getDeleteTableSQL() {
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    @Override
    protected String getCreateTableSQL() {
        return "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_HOSTNAME      + " VARCHAR(15), " +
                    COLUMN_PORT + " INTEGER, " +
                    COLUMN_PATH    + " TEXT, " +
                    "PRIMARY KEY ("+ COLUMN_HOSTNAME + "," + COLUMN_PORT + ", " + COLUMN_PATH + ") " +
                " )";
    }

    public void addBookmark(final Server srv, final String path) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_HOSTNAME, srv.hostname);
        values.put(COLUMN_PORT, srv.port);
        values.put(COLUMN_PATH, path);

        try {
            insert(TABLE_NAME, values);
        } catch (SQLiteConstraintException ignored) {
            // If a unique constraint fails, it means the bookmark was already there
        }
    }

    public List<String> getBookmarks(final Server srv) {
        final String query =
                "SELECT " + COLUMN_PATH + " FROM " + TABLE_NAME +
                        " WHERE " + COLUMN_HOSTNAME + " = ? " +
                        "   AND " + COLUMN_PORT + " = ? ";

        final String[] args = new String[]{srv.hostname, String.valueOf(srv.port)};

        final List<String> results = new ArrayList<>();

        readQuery(query, args, new String[]{COLUMN_PATH}, new QueryReadCallback() {
            @Override
            public void onCursorReady(Cursor res) {
                while (res.moveToNext()) {
                    results.add(res.getString(res.getColumnIndex(COLUMN_PATH)));
                }
            }
        });

        return results;
    }

    public void deleteBookmark(final Server srv, final String path) {
        final String query = "DELETE FROM " + TABLE_NAME +
                             "      WHERE " + COLUMN_HOSTNAME + "=? " +
                             "        AND " + COLUMN_PORT + "=?" +
                             "        AND " + COLUMN_PATH + "=?";
        final String[] args = new String[]{srv.hostname, String.valueOf(srv.port), path};
        run(query, args);
    }
}
