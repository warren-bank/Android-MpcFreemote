package com.github.warren_bank.mpcfreemote.local_settings;

import com.github.warren_bank.mpcfreemote.model.Server;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class RememberedServers extends LocalSettings {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "servers.db";
    private static final String TABLE_NAME = "remembered_server";
    private static final String COLUMN_HOSTNAME = "hostname";
    private static final String COLUMN_PORT = "port";
    private static final String COLUMN_DISPLAY_NAME = "display_name";
    private static final String COLUMN_LAST_USED = "last_used";
    private static final String COLUMN_LAST_PATH = "last_path";
    private static final String[] ALL_COLS = new String[]{COLUMN_HOSTNAME, COLUMN_PORT, COLUMN_DISPLAY_NAME, COLUMN_LAST_PATH};

    public RememberedServers(Context context) {
        super(context, DB_NAME, DB_VERSION);
    }

    @Override
    protected String getCreateTableSQL() {
        return "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_HOSTNAME + " TEXT, " +
                        COLUMN_PORT + " INTEGER, " +
                        COLUMN_DISPLAY_NAME + " TEXT, " +
                        COLUMN_LAST_USED + " INTEGER, " +
                        COLUMN_LAST_PATH + " TEXT, " +
                        "PRIMARY KEY ("+ COLUMN_HOSTNAME + "," + COLUMN_PORT + ") " +
               " )";
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    protected String getDeleteTableSQL() {
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /**
     * Remember a server and its settings. Will automatically set the last used flag.
     * @param srv Last used server
     */
    public void rememberServer(final Server srv) {
        final String query = " REPLACE INTO " + TABLE_NAME + "( " +
                                    COLUMN_HOSTNAME + "," +
                                    COLUMN_PORT + "," +
                                    COLUMN_DISPLAY_NAME + "," +
                                    COLUMN_LAST_PATH + ", " +
                                    COLUMN_LAST_USED +
                             " ) VALUES ( ?, ?, ?, ?, 1)";

        final String[] args = new String[]{srv.hostname, String.valueOf(srv.port), srv.getDisplayName(), srv.getLastPath()};

        run(query, args);
    }

    /**
     * Forget last used server
     */
    private void resetLastUsedServer() {
        final String query = "UPDATE " + TABLE_NAME +
                             "   SET " + COLUMN_LAST_USED + "=?";
        final String[] args = new String[]{"0"};
        run(query, args);
    }

    /**
     * Returns the server with LAST_USED=1. Will return a random one if multiple servers have
     * this column or null if there are no recent servers.
     *
     * @return Last used server
     */
    public Server getLastUsedServer() {
        final String query = "SELECT * " +
                             "   FROM "  + TABLE_NAME +
                             "  WHERE "  + COLUMN_LAST_USED+ " =?";
        final String[] args = new String[]{"1"};

        final Server[] dbSrv = new Server[1];
        dbSrv[0] = null;

        readQuery(query, args, ALL_COLS, new QueryReadCallback() {
            @Override
            public void onCursorReady(Cursor res) {
                if (res.getCount() == 1) {
                    res.moveToFirst();
                    dbSrv[0] = readServerFrom(res);
                }
            }
        });

        if (dbSrv[0] == null) {
            Log.w(getClass().getSimpleName(), "Multiple last used servers. Will reset flag.");
            resetLastUsedServer();
        }

        return dbSrv[0];
    }

    /**
     * Returns the remembered server for an hostname:port set (in a Server object).
     * This is useful to retrieve the saved settings (ie last path).
     * @param srv hostname:port
     * @return Fully rehydrated Server, or null if not found
     */
    public Server getRememberedServer(final Server srv)  {
        final String query = "SELECT * " +
                             "  FROM " + TABLE_NAME +
                             " WHERE " + COLUMN_HOSTNAME + " =? " +
                             "   AND " + COLUMN_PORT + " =? ";

        final String[] args = new String[]{srv.hostname, String.valueOf(srv.port)};

        final Server[] dbSrv = new Server[1];
        dbSrv[0] = null;

        readQuery(query, args, ALL_COLS, new QueryReadCallback() {
            @Override
            public void onCursorReady(Cursor res) {
                if (res.getCount() == 1) {
                    res.moveToFirst();
                    dbSrv[0] = readServerFrom(res);
                }
            }
        });

        return dbSrv[0];
    }

    /**
     * Returns the last N used server, sorted by IP
     * @param count Expected number of servers
     * @return List of servers
     */
    public List<Server> getLastUsedServers(final int count) {
        final String query = "SELECT * FROM " + TABLE_NAME +
                " ORDER BY " + COLUMN_HOSTNAME + " LIMIT ? ";

        final String[] args = new String[]{String.valueOf(count)};

        final List<Server> results = new ArrayList<>();
        readQuery(query, args, ALL_COLS, new QueryReadCallback() {
            @Override
            public void onCursorReady(Cursor res) {
                while (res.moveToNext()) {
                    results.add(readServerFrom(res));
                }
            }
        });

        return results;
    }

    private Server readServerFrom(final Cursor c)  {
        final String hostname;
        final int port;
        final String displayName;
        final String pass;
        final String lastPath;

        try {
            hostname = c.getString(c.getColumnIndexOrThrow(COLUMN_HOSTNAME));
            port = c.getInt(c.getColumnIndexOrThrow(COLUMN_PORT));
            displayName = c.getString(c.getColumnIndexOrThrow(COLUMN_DISPLAY_NAME));
            lastPath = c.getString(c.getColumnIndexOrThrow(COLUMN_LAST_PATH));
        } catch (Exception e) {
            return null;
        }

        Server srv = new Server(hostname, port);
        srv.setLastPath(lastPath);
        srv.setDisplayName(displayName);
        return srv;
    }

    /**
     * Updates the last known path for a server
     * @param srv Current server
     */
    public void saveLastPathForServer(final Server srv) {
        final String query = "UPDATE " + TABLE_NAME +
                             "   SET " + COLUMN_LAST_PATH + "=? " +
                             " WHERE " + COLUMN_HOSTNAME + "=? " +
                             "   AND " + COLUMN_PORT + "=? ";
        final String[] args = new String[]{srv.getLastPath(), srv.hostname, String.valueOf(srv.port)};
        run(query, args);
    }

    public void forget(Server srv) {
        final String query = " DELETE FROM " + TABLE_NAME + " WHERE " +
                                COLUMN_HOSTNAME + " = ? AND " +
                                COLUMN_PORT + " = ?";
        final String[] args = new String[]{srv.hostname, String.valueOf(srv.port)};
        run(query, args);
    }
}
