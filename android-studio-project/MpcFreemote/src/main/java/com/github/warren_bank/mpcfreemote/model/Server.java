package com.github.warren_bank.mpcfreemote.model;

public class Server {
    public final String hostname;
    public final Integer port;

    private String displayName;
    private String lastPath;

    public Server(String hostname, Integer port) {
        this.hostname    = hostname;
        this.port        = port;
        this.displayName = null;
        this.lastPath    = null;
    }

    public void setDisplayName(final String name) { this.displayName = name; }
    public String getDisplayName() { return displayName; }

    public void setLastPath(final String lastPath) { this.lastPath = lastPath; }
    public String getLastPath() { return this.lastPath; }

    public String getFullDisplayName() {
        String name = hostname + ":" + port;
        if (displayName != null) {
            name = displayName + " (" + name + ")";
        }
        return name;
    }
}
