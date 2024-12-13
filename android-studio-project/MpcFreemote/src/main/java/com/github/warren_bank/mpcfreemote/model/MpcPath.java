package com.github.warren_bank.mpcfreemote.model;

import com.github.warren_bank.mpcfreemote.local_settings.Bookmarks;
import com.github.warren_bank.mpcfreemote.local_settings.PlayedFiles;
import com.github.warren_bank.mpcfreemote.local_settings.RememberedServers;
import com.github.warren_bank.mpcfreemote.mpc_connector.RemoteMpc;

import com.eeeeeric.mpc.hc.api.FileInfo;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MpcPath {

    public static class ExtraFileInfo extends FileInfo {
        public String  filePath;
        public boolean wasPlayedBefore;

        public ExtraFileInfo(FileInfo fileInfo, String currentPath, boolean wasPlayedBefore) {
            super(fileInfo.getFileName(), fileInfo.getHref(), fileInfo.getFileType(), fileInfo.getFileSize(), fileInfo.getLastModified(), fileInfo.isDirectory());

            if (".".equals(fileInfo.getFileName())) {
                this.filePath = currentPath;
            }
            else if ("..".equals(fileInfo.getFileName())) {
                int lastIndex = currentPath.lastIndexOf("/");

                this.filePath = (lastIndex <= 0)
                    ? "/"
                    : currentPath.substring(0, lastIndex);
            }
            else {
                this.filePath = currentPath + (currentPath.endsWith("/") ? "" : "/") + fileInfo.getFileName();
            }

            this.wasPlayedBefore = wasPlayedBefore;
        }

        public static FileInfo fromPath(String path, boolean isDirectory) {
            String href = "/browser.html?path=" + encodeURIComponent(path);
            return new FileInfo(null, href, null, null, null, isDirectory);
        }

        public static String encodeURIComponent(String s) {
          try {
            s = URLEncoder.encode(s, "UTF-8");
          }
          catch(Exception e) {}
          return s;
        }
    }

    public interface UICallback {
        void onNewDirListAvailable(List<ExtraFileInfo> results);
    }

    private static final String MPC_DEFAULT_START_PATH = "/";

    // Usually a person will cd to several directories before settling for one: there's no point
    // in saving them all, so there will be a timeout after which we can consider the user has
    // decided; only then will the directory be saved.
    private static final int LAST_PATH_SAVE_DELAY_MS = 5000;

    private final RemoteMpc.ConnectionProvider mpcProvider;
    private final Context dbContext;
    private final UICallback uiCallback;
    private final Handler uiRunner;
    private final Handler bgRunner;

    private String currentPath;

    // @see thereIsCDWithNoUIUpdate
    private boolean cdPendingUIUpdate = true;

    private Runnable saveLastPathTask = null;

    public MpcPath(RemoteMpc.ConnectionProvider mpcProvider, Context dbContext, UICallback uiCallback) {
        final Server srv = mpcProvider.getActiveMpcConnection().getServer();
        onServerChanged(srv);

        this.mpcProvider = mpcProvider;
        this.dbContext   = dbContext;
        this.uiCallback  = uiCallback;
        this.uiRunner    = new Handler(dbContext.getMainLooper());
        this.bgRunner    = new Handler();
    }

    public void onServerChanged(final Server srv) {
        this.cdPendingUIUpdate = true;

        if ((srv == null) || (srv.getLastPath() == null)) {
            this.currentPath = MPC_DEFAULT_START_PATH;
        } else {
            this.currentPath = srv.getLastPath();
        }
    }

    /**
     * Changes directory to $path and saves $path as the last known directory for the current server
     * @param path CD to $path
     */
    public void cd(final String path) {
        this.currentPath = path;
        this.cdPendingUIUpdate = true;

        final Server srv = mpcProvider.getActiveMpcConnection().getServer();
        srv.setLastPath(path);
        saveCurrentPath(srv);
    }

    private void saveCurrentPath(final Server srv) {
        if (saveLastPathTask != null) {
            bgRunner.removeCallbacks(saveLastPathTask);
        }

        saveLastPathTask = new Runnable() {
            @Override
            public void run() {
                new RememberedServers(dbContext).saveLastPathForServer(srv);
            }
        };

        bgRunner.postDelayed(saveLastPathTask, LAST_PATH_SAVE_DELAY_MS);
    }

    public String getCWD() {
        return currentPath;
    }

    /**
     * Useful to prevent reloading the same dir twice (that'd result in an ugly flicker)
     * @return True if there was a dir change and no UI update
     */
    public boolean thereIsCDWithNoUIUpdate() { return cdPendingUIUpdate; }

    public void updateDirContents() {
        new Thread() {
            @Override
            public void run() {
                updateDirContents_impl();
            }
        }
        .start();
    }

    private void updateDirContents_impl() {
        this.cdPendingUIUpdate = false;

        final Server srv = mpcProvider.getActiveMpcConnection().getServer();
        List<String> filesPlayedInDir = (new PlayedFiles(dbContext)).getListOfPlayedFiles(srv, currentPath);

        FileInfo currentDirectory = ExtraFileInfo.fromPath(currentPath, true);
        List<FileInfo> results = mpcProvider.getActiveMpcConnection().browse(currentDirectory);

        List<ExtraFileInfo> extraResults = new ArrayList<ExtraFileInfo>();
        if (results != null) {
          for (FileInfo fileInfo : results) {
              boolean wasPlayedBefore = false;

              if ((filesPlayedInDir != null) && !filesPlayedInDir.isEmpty()) {
                  wasPlayedBefore = filesPlayedInDir.contains(fileInfo.getHref());
              }

              ExtraFileInfo xtraInfo = new ExtraFileInfo(fileInfo, currentPath, wasPlayedBefore);
              extraResults.add(xtraInfo);
          }
        }

        sendCallback(extraResults);
    }

    private void sendCallback(final List<ExtraFileInfo> extraResults) {
        uiRunner.post(new Runnable() {
            @Override
            public void run() {
                uiCallback.onNewDirListAvailable(extraResults);
            }
        });
    }

    public void bookmarkCurrentDirectory() {
        final Server srv = mpcProvider.getActiveMpcConnection().getServer();
        (new Bookmarks(dbContext)).addBookmark(srv, currentPath);
    }

    public List<String> getBookmarks() {
        final Server srv = mpcProvider.getActiveMpcConnection().getServer();
        return (new Bookmarks(dbContext)).getBookmarks(srv);
    }

    public void deleteBookmark(final String path) {
        final Server srv = mpcProvider.getActiveMpcConnection().getServer();
        (new Bookmarks(dbContext)).deleteBookmark(srv, path);
    }

    public void toggleSeen(String path, boolean seen) {
        final Server srv = mpcProvider.getActiveMpcConnection().getServer();
        if (seen) {
            (new PlayedFiles(dbContext)).addPlayedFile(srv, path);
        } else {
            (new PlayedFiles(dbContext)).rmPlayedFile(srv, path);
        }
    }
}
