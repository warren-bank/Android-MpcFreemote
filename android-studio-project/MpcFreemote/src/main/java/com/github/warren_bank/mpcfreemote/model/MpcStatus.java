package com.github.warren_bank.mpcfreemote.model;

import com.github.warren_bank.mpcfreemote.mpc_connector.RemoteMpc;

import android.content.Context;
import android.os.Handler;

import java.util.Map;

public class MpcStatus {

    public static class Info {
        public String file;
        public String filepath;
        public String statestring;
        public String positionstring;
        public String durationstring;
        public int position;
        public int duration;
        public int volumelevel;
        public boolean muted;
        public float playbackrate;

        public Info(Map<String, String> variables) {
          this.file           = variables.get("file");
          this.filepath       = variables.get("filepath");
          this.statestring    = variables.get("statestring");
          this.positionstring = variables.get("positionstring");
          this.durationstring = variables.get("durationstring");

          try {
            this.position = Integer.parseInt(
              variables.get("position"),
              10
            );
          }
          catch(Exception e) {
            this.position = 0;
          }

          try {
            this.duration = Integer.parseInt(
              variables.get("duration"),
              10
            );
          }
          catch(Exception e) {
            this.duration = 0;
          }

          try {
            this.volumelevel = Integer.parseInt(
              variables.get("volumelevel"),
              10
            );
          }
          catch(Exception e) {
            this.volumelevel = 1;
          }

          try {
            this.muted = (1 == Integer.parseInt(
              variables.get("muted"),
              10
            ));
          }
          catch(Exception e) {
            this.muted = false;
          }

          try {
            this.playbackrate = Float.parseFloat(
              variables.get("playbackrate")
            );
          }
          catch(Exception e) {
            this.playbackrate = 1.0f;
          }
        }
    }

    public interface UICallback {
        void onMpcStatusUpdate(Info info);
    }

    private static final int updateIntervalMs = 5000;

    private final RemoteMpc.ConnectionProvider mpcProvider;
    private final UICallback uiCallback;
    private final Handler uiRunner;

    private boolean isActive;

    public MpcStatus(RemoteMpc.ConnectionProvider mpcProvider, Context context, UICallback uiCallback) {
        this.mpcProvider = mpcProvider;
        this.uiCallback  = uiCallback;
        this.uiRunner    = new Handler(context.getMainLooper());
        this.isActive    = false;

        updateStatus();
    }

    public boolean isEnabled() {
        return this.isActive;
    }

    public void setEnabled(boolean isActive) {
        this.isActive = isActive;
    }

    public void toggleEnabled() {
        this.isActive = !this.isActive;
    }

    private void updateStatus() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (isActive) {
                        updateStatus_impl();
                    }
                    try {
                        Thread.sleep(updateIntervalMs);
                    }
                    catch(Exception e) {
                        setEnabled(false);
                        break;
                    }
                }
            }
        }
        .start();
    }

    private void updateStatus_impl() {
        Map<String, String> variables = mpcProvider.getActiveMpcConnection().getVariables();

        if (variables != null) {
            fixVariables(variables);

            Info info = new Info(variables);
            sendCallback(info);
        }
    }

    /*
     * derive unsupported variables:
     *   https://github.com/eeeeeric/mpc-hc-api/blob/0.1.0/src/main/java/com/eeeeeric/mpc/hc/api/MediaPlayerClassicHomeCinema.java#L80
     */
    private void fixVariables(Map<String, String> variables) {
        String file     = variables.get("file");
        String filepath = variables.get("filepath");

        if ((file == null) && (filepath != null)) {
            file = filepath.substring(filepath.lastIndexOf("/") + 1);

            variables.put("file", file);
        }
    }

    private void sendCallback(final Info info) {
        uiRunner.post(new Runnable() {
            @Override
            public void run() {
                uiCallback.onMpcStatusUpdate(info);
            }
        });
    }
}
