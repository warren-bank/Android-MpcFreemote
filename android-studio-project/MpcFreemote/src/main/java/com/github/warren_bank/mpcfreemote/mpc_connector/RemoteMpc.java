package com.github.warren_bank.mpcfreemote.mpc_connector;

import com.github.warren_bank.mpcfreemote.model.Server;
import com.github.warren_bank.mpcfreemote.mpc_connector.MpcCommand;

import com.eeeeeric.mpc.hc.api.FileInfo;
import com.eeeeeric.mpc.hc.api.MediaPlayerClassicHomeCinema;
import com.eeeeeric.mpc.hc.api.WMCommand;

import java.util.List;
import java.util.Map;

public class RemoteMpc {

    /**
     * An interface to retrieve the current active connection:
     * useful so that fragments won't need to keep a field across constructions/displays.
     */
    public interface ConnectionProvider {
        RemoteMpc getActiveMpcConnection();
    }

    /**
     * A callback to invoke on generic failure conditions
     */
    public interface GeneralCallback {
        void onConnectionError(Exception e);
    }

    private final Server srv;
    private final GeneralCallback cbs;
    private final MediaPlayerClassicHomeCinema mpc;

    /**
     * Construct an object to connect to a remote Mpc
     * @param srv Server to connect to
     * @param cbs A general callback listener.
     */
    public RemoteMpc(final Server srv, final GeneralCallback cbs) {
        this.srv = srv;
        this.cbs = cbs;
        this.mpc = ((srv == null) || (srv.port == null) || (srv.port < 0))
          ? null
          : new MediaPlayerClassicHomeCinema(srv.hostname, srv.port);
    }

    public Server getServer() { return srv; }

    // IMPORTANT: the caller (MpcStatus) is responsible for using a background thread
    public synchronized Map<String, String> getVariables() {
        if (mpc == null) {
            onConnectionError(null);
            return null;
        }

        try {
            return mpc.getVariables();
        }
        catch(Exception e) {
            onConnectionError(e);
        }
        return null;
    }

    // IMPORTANT: the caller (MpcPath) is responsible for using a background thread
    public synchronized List<FileInfo> browse(final FileInfo directory) {
        if (mpc == null) {
            onConnectionError(null);
            return null;
        }

        try {
            return mpc.browse(directory);
        }
        catch(Exception e) {
            onConnectionError(e);
        }
        return null;
    }

    public synchronized void openFile(final FileInfo file) {
        if (mpc == null) {
            onConnectionError(null);
            return;
        }

        new Thread() {
            @Override
            public void run() {
                try {
                    mpc.openFile(file);
                }
                catch(Exception e) {
                    onConnectionError(e);
                }
            }
        }
        .start();
    }

    public synchronized void execute(final MpcCommand mpc_command) {
        if (mpc == null) {
            onConnectionError(null);
            return;
        }

        new Thread() {
            @Override
            public void run() {
                try {
                    WMCommand command = mpc_command.getWMCommand();

                    if (command != null) {
                        mpc.execute(command);
                    }
                }
                catch(Exception e) {
                    onConnectionError(e);
                }
            }
        }
        .start();
    }

    public synchronized void setVolume(final int volume) {
        if (mpc == null) {
            onConnectionError(null);
            return;
        }

        new Thread() {
            @Override
            public void run() {
                try {
                    mpc.setVolume(volume);
                }
                catch(Exception e) {
                    onConnectionError(e);
                }
            }
        }
        .start();
    }

    public synchronized void seekByPercent(final int _percent) {
        if (mpc == null) {
            onConnectionError(null);
            return;
        }

        new Thread() {
            @Override
            public void run() {
                try {
                    int percent = _percent;

                    if (percent < 0)
                      percent = 0;
                    else if (percent > 100)
                      percent = 100;

                    mpc.execute(
                        WMCommand.SEEK,
                        new MediaPlayerClassicHomeCinema.KeyValuePair("percent", Integer.toString(percent))
                    );
                }
                catch(Exception e) {
                    onConnectionError(e);
                }
            }
        }
        .start();
    }

    private void onConnectionError(Exception e) {
        if (cbs != null) {
            cbs.onConnectionError(e);
        }
    }
}
