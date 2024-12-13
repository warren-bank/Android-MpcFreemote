package com.github.warren_bank.mpcfreemote;

import com.github.warren_bank.mpcfreemote.model.Server;
import com.github.warren_bank.mpcfreemote.mpc_connector.RemoteMpc;

import com.eeeeeric.mpc.hc.api.FileInfo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends FragmentActivity
                          implements RemoteMpc.ConnectionProvider,
                                     RemoteMpc.GeneralCallback,
                                     ServerSelectView.ServerSelectionCallback,
                                     DirListingView.DirListingCallback {

    private RemoteMpc mpcConnection = null;
    private PlayerControllerView playerControllerView;
    private ServerSelectView serverSelectView;
    private DirListingView dirListView;
    private RemoteControlView remoteControlView;
    private MainMenuNavigation mainMenu;
    private boolean periodicStatusUpdateRequested = false;

    private class MainMenuNavigation extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {
        private final ViewPager parentView;
        private final ServerSelectView serverSelectView;
        private final DirListingView dirListView;
        private final RemoteControlView remoteControlView;

        MainMenuNavigation(ViewPager view, FragmentManager fm, ServerSelectView serverSelectView, DirListingView dirListView, RemoteControlView remoteControlView)
        {
            super(fm);
            this.parentView        = view;
            this.serverSelectView  = serverSelectView;
            this.dirListView       = dirListView;
            this.remoteControlView = remoteControlView;

            parentView.setAdapter(this);
            parentView.addOnPageChangeListener(this);
        }

        @NonNull
        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0: return serverSelectView;
                case 1: return dirListView;
                case 2: return remoteControlView;
                default: throw new RuntimeException(MainMenuNavigation.class.getName() + " tried to select a page item which doesn't exist.");
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return getString(R.string.main_menu_title_servers);
                case 1: return getString(R.string.main_menu_title_dir_listing);
                case 2: return getString(R.string.main_menu_title_remote_control);
                default: throw new RuntimeException(MainMenuNavigation.class.getName() + " tried to get a title for a page which doesn't exist.");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override public void onPageScrolled(int i, float v, int i2) {}
        @Override public void onPageScrollStateChanged(int i) {}

        @Override
        public void onPageSelected(int i) {
            switch (i) {
                case 0: /*serverSelectView.scanServers();*/ return;
                case 1: /*dirListView.triggerCurrentPathListUpdate();*/ return;
                case 2: return;
                default: throw new RuntimeException(MainMenuNavigation.class.getName() + " selected a page which doesn't exist.");
            }
        }

        void jumpToServerSelection() { parentView.setCurrentItem(0, true); }
        void jumpToDirectoryList()   { parentView.setCurrentItem(1, true); }
        void jumpToRemoteControl()   { parentView.setCurrentItem(2, true); }
    }

    private void safePutFragment(final Bundle outState, final String name, Fragment obj) {
        try {
            if (obj.isAdded()) {
                getSupportFragmentManager().putFragment(outState, name, obj);
            }
        } catch (IllegalStateException e) {
            // Some fragments might not be in the fragment manager: if this is the case, just save a null
            // object to give the activity a chance of recreating the fragment when resuming
        }
    }

    @Override
    // This should handle things like device rotation: if state is not saved then the fragment may
    // be recreated and all sort of funny crashes will happen.
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        safePutFragment(outState, PlayerControllerView.class.getName(), playerControllerView);
        safePutFragment(outState, ServerSelectView.class.getName(), serverSelectView);
        safePutFragment(outState, DirListingView.class.getName(), dirListView);
        safePutFragment(outState, RemoteControlView.class.getName(), remoteControlView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PlayerControllerView.shouldUseDarkTheme(this)) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }

        // Setting the content must be done after setting the theme
        setContentView(R.layout.activity_main);

        // Create or restore all views
        if (savedInstanceState != null) {
            playerControllerView = (PlayerControllerView) getSupportFragmentManager().getFragment(savedInstanceState, PlayerControllerView.class.getName());
            serverSelectView     = (ServerSelectView)     getSupportFragmentManager().getFragment(savedInstanceState, ServerSelectView.class.getName());
            dirListView          = (DirListingView)       getSupportFragmentManager().getFragment(savedInstanceState, DirListingView.class.getName());
            remoteControlView    = (RemoteControlView)    getSupportFragmentManager().getFragment(savedInstanceState, RemoteControlView.class.getName());
        }

        if (this.playerControllerView == null) this.playerControllerView = new PlayerControllerView();
        if (this.serverSelectView     == null) this.serverSelectView     = new ServerSelectView();
        if (this.dirListView          == null) this.dirListView          = new DirListingView();
        if (this.remoteControlView    == null) this.remoteControlView    = new RemoteControlView();

        this.mainMenu = new MainMenuNavigation(((ViewPager) super.findViewById(R.id.wMainMenu)), getSupportFragmentManager(), serverSelectView, dirListView, remoteControlView);

        getActiveMpcConnection();
    }

    @Override
    public void onServerSelection(final Server srv) {
        if (srv != null) {
            Log.i(getClass().getSimpleName(), "Connecting to server " + srv.hostname + ":" + srv.port);
            this.mpcConnection = new RemoteMpc(srv, this);

            // This method may be called without an activity attached
            if (dirListView != null && mainMenu != null) {
                dirListView.onServerChanged(srv);
                mainMenu.jumpToDirectoryList();
            }
        }
        else {
            // Connect to dummy server: the first command will fail and prompt a new server select
            this.mpcConnection = new RemoteMpc(new Server("", null), this);
        }
    }

    @Override
    public void onOpenFileRequest(final FileInfo fileInfo) {
        Log.i(getClass().getSimpleName(), "Add to playlist: " + fileInfo.getHref());
        mpcConnection.openFile(fileInfo);
    }

    @Override
    public Server getActiveServer() {
        return getActiveMpcConnection().getServer();
    }

    @Override
    public RemoteMpc getActiveMpcConnection() {
        if (mpcConnection == null) {
            final Server srv = ServerSelectView.getLastUsedServer(this);
            if (srv == null) {
                onConnectionError(null);
            }
            onServerSelection(srv);
        }

        return mpcConnection;
    }

    @Override
    public void onConnectionError(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainMenu.jumpToServerSelection();

                String msg = getString(R.string.status_mpc_cant_connect);
                String at  = "";

                if ((mpcConnection != null) && (mpcConnection.getServer() != null) && (mpcConnection.getServer().port != null)) {
                    String server = mpcConnection.getServer().hostname + ':' + mpcConnection.getServer().port;

                    at = getString(R.string.status_mpc_cant_connect_at);
                    at = String.format(at, server);
                }

                msg = String.format(msg, at);

                Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }
}
