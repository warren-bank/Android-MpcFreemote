package com.github.warren_bank.mpcfreemote.net_utils;

import com.github.warren_bank.mpcfreemote.model.Server;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class ServerScanner extends AsyncTask<Void, Server, List<Server>> {

    // A small scan timeout should be enough for LANs
    private static final int SERVER_SCAN_TIMEOUT = 100;
    private static final int MPC_PORT = 8192;

    public interface Callback {
        void onServerDiscovered(final Server srv);
        void onScanFinished();
        void onScanCancelled();
        void onNoNetworkAvailable();
    }

    private final Callback cb;
    private final int port;
    private boolean no_network;

    public ServerScanner(Callback cb) {
        this(cb, null);
    }

    public ServerScanner(Callback cb, Integer port) {
        this.cb = cb;
        this.port = ((port == null) || (port < 0)) ? MPC_PORT : port;
        this.no_network = false;
        this.execute();
    }

    @Override
    protected List<Server> doInBackground(Void... nothing) {
        List<Server> servers = new ArrayList<>();
        
        List<String> localIps = getLocalNetworks();
        if (localIps.size() == 0) {
            no_network = true;
        }

        for (final String ip : localIps) {
            for (int i = 1; i < 255; i++) {
                if (isCancelled()) break;

                final String scanIp = ip + i;
                Server srv = getInterestingServer(scanIp);
                if (srv != null) {
                    servers.add(srv);
                    publishProgress(srv);
                }
            }
        }

        return servers;
    }

    @Override
    protected void onPostExecute(final List<Server> discoveredServers) {
        if (no_network) cb.onNoNetworkAvailable();
        cb.onScanFinished();
    }

    @Override
    protected void onCancelled(final List<Server> discoveredServers) {
        cb.onScanCancelled();
    }

    @Override
    protected void onProgressUpdate(Server... srvLst) {
        for (Server srv : srvLst) {
            if (srv == null) throw new RuntimeException("Bad programmer error: Found a null server, this shouldn't happen.");
            cb.onServerDiscovered(srv);
        }
    }


    /* Get local network interfaces                             */
    /************************************************************/

    private static final String IP_ADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    private static String getNetworkFromIP(final String ip) {
        // This assumes a /24 is used: if that's not the case, we won't support it anyway
        // as scanning would be too slow. Also, if the user is not using a /24 he's probably
        // smart enough to manually enter his IP
        return ip.substring(0, ip.lastIndexOf(".")+1);
    }

    /**
     * @return list of IPs for local networks, eg ["192.168.0.", "127.0.0."]
     */
    public static List<String> getLocalNetworks() {
        final Pattern ip_pattern = Pattern.compile(IP_ADDRESS_PATTERN);

        List<String> addresses = new ArrayList<>();
        final List<NetworkInterface> interfaces;

        try {
            interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        } catch (SocketException e) {
            return addresses;
        }

        for (NetworkInterface netInterface : interfaces) {
            for (InetAddress address : Collections.list(netInterface.getInetAddresses())) {
                if (address.isLoopbackAddress()) {
                    continue;
                }

                final String ip = address.getHostAddress().toUpperCase();
                if (!ip_pattern.matcher(ip).matches()) {
                    Log.w(ServerScanner.class.getName(), "IP " + ip + " is not of a supported type (IPv6?)");
                    continue;
                }

                addresses.add(getNetworkFromIP(ip));
            }
        }

        return addresses;
    }

    /**
     * @param ip ip to scan
     * @param port port to scan
     * @return true if it's possible to connect to $ip:$port
     */
    private boolean isPortOpen(final String ip, int port) {
        final SocketAddress address = new InetSocketAddress(ip, port);
        final Socket serverConn = new Socket();
        try {
            serverConn.connect(address, SERVER_SCAN_TIMEOUT );
            serverConn.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Scans an IP to detect if it contains an interesting server
     * @param ip Ip to scan
     * @return null if there's no interesting server, a valid server object otherwise
     */
    private Server getInterestingServer(final String ip) {
        if (isPortOpen(ip, this.port)) {
            return new Server(ip, this.port);
        }

        return null;
    }
}

