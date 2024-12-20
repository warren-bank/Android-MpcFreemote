package com.github.warren_bank.mpcfreemote.local_settings;

import com.github.warren_bank.mpcfreemote.model.Server;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;

import java.util.ArrayList;
import java.util.List;

public class PlayedFiles extends LocalSettings {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "played_files.db";
    private static final String TABLE_NAME = "played_files";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_HOSTNAME = "hostname";
    private static final String COLUMN_PORT = "port";
    private static final String COLUMN_PATH = "path";

    private static final int HISTORY_LIMIT = 1000;

    public PlayedFiles(Context context) {
        super(context, DB_NAME, DB_VERSION);
    }

    @Override
    protected String getDeleteTableSQL() {
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    @Override
    protected String getCreateTableSQL() {
        return "CREATE TABLE " + TABLE_NAME + " ( " +
                    COLUMN_ID      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_HOSTNAME      + " VARCHAR(15), " +
                    COLUMN_PORT + " INTEGER, " +
                    COLUMN_PATH    + " TEXT, " +
               "   UNIQUE (" + COLUMN_HOSTNAME + ", " + COLUMN_PORT + ", " + COLUMN_PATH + ") " +
               ")";
    }

    /**
     * Adds a file to the recently played files list and truncates the list as configured
     * @param srv Server on which $path was played
     * @param path Path to store
     */
    public void addPlayedFile(final Server srv, final String path) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_HOSTNAME, srv.hostname);
        values.put(COLUMN_PORT, srv.port);
        values.put(COLUMN_PATH, path);

        try {
            insert(TABLE_NAME, values);
        } catch (SQLiteConstraintException ignored) {
            // If a unique constraint fails, it means the bookmark was already there
        }

        deleteOldPlayedFiles();
    }

    /**
     * Removes a file from the recently played files list
     * @param srv Server on which $path was played
     * @param path Path to forget
     */
    public void rmPlayedFile(final Server srv, final String path) {
        final String query =
                "DELETE FROM " + TABLE_NAME +
                " WHERE " + COLUMN_HOSTNAME + " = ? " +
                "   AND " + COLUMN_PORT + " = ? " +
                "   AND " + COLUMN_PATH + " = ? ";

        final String[] args = new String[]{srv.hostname, String.valueOf(srv.port), path};
        run(query, args);
    }

    private void deleteOldPlayedFiles() {
        // Hint: read from the inside out
        final String query = "DELETE FROM " + TABLE_NAME +
                             " WHERE " + COLUMN_ID + " < ( " +
                             "   SELECT min(t.id) " +
                             "   FROM ( " +
                             "      SELECT " + COLUMN_ID +
                             "      FROM " + TABLE_NAME +
                             "      ORDER BY " + COLUMN_ID + " DESC " +
                             "      LIMIT ? " +
                             "   ) AS t " +
                             " )";

        final String[] args = new String[]{String.valueOf(HISTORY_LIMIT)};
        run(query, args);
    }

    public List<String> getListOfPlayedFiles(final Server srv, final String path) {

        final String pathSearch  = path + '%';
        final String query = "SELECT " + COLUMN_PATH + " FROM " + TABLE_NAME +
                             " WHERE " + COLUMN_HOSTNAME + " = ? " +
                             "   AND " + COLUMN_PORT + " = ? " +
                             "   AND " + COLUMN_PATH + " like ? ";

        final String[] args = new String[]{srv.hostname, String.valueOf(srv.port), pathSearch};

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
}
