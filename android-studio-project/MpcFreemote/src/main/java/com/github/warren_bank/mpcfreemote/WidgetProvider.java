package com.github.warren_bank.mpcfreemote;

import com.github.warren_bank.mpcfreemote.R;
import com.github.warren_bank.mpcfreemote.MainApp;
import com.github.warren_bank.mpcfreemote.local_settings.RememberedServers;
import com.github.warren_bank.mpcfreemote.model.Server;
import com.github.warren_bank.mpcfreemote.mpc_connector.MpcCommand;
import com.github.warren_bank.mpcfreemote.mpc_connector.RemoteMpc;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

    protected enum IntentAttribute {
        ACTION_EXECUTE_COMMAND,
        EXTRA_COMMAND_ENUM_NAME;

        public String addContext(Context context) {
            return context.getPackageName() + "." + name();
        }
    }

    protected static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        SharedPreferences sp               = PreferenceManager.getDefaultSharedPreferences(context);
        final String enumName              = sp.getString("widget_" + appWidgetId + "_enum_name",          null);
        final int    commandNameResourceId = sp.getInt(   "widget_" + appWidgetId + "_command_name_resid", Integer.MIN_VALUE);
        if ((enumName == null) || (commandNameResourceId == Integer.MIN_VALUE)) return;

        Intent intent = new Intent(context, WidgetProvider.class);
        intent.setAction(
          IntentAttribute.ACTION_EXECUTE_COMMAND.addContext(context)
        );
        intent.putExtra(
            IntentAttribute.EXTRA_COMMAND_ENUM_NAME.addContext(context),
            enumName
        );
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, getPendingIntentFlags());

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        views.setTextViewText(R.id.widget_button, context.getString(commandNameResourceId));
        views.setOnClickPendingIntent(R.id.widget_button, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    protected static int getPendingIntentFlags() {
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= 23)
            flags |= PendingIntent.FLAG_IMMUTABLE;
        return flags;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    private static class WidgetMpcCallback implements RemoteMpc.GeneralCallback {
        private final Context context;

        WidgetMpcCallback(Context context) {
            this.context = context;
        }

        public void reviveApp() {
            Intent openApp = new Intent(context, MainActivity.class);
            PendingIntent pendingOpenApp = PendingIntent.getActivity(context, -1, openApp, WidgetProvider.getPendingIntentFlags());

            try {
                pendingOpenApp.send();
            } catch (PendingIntent.CanceledException ignored) {
                // Nothing to do...
            }
        }

        @Override
        public void onConnectionError(final Exception e) {
            reviveApp();
        }
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final WidgetMpcCallback mpcCallback = new WidgetMpcCallback(context);

        final Server srv = (new RememberedServers(context)).getLastUsedServer();
        if (srv == null) {
            mpcCallback.reviveApp();
        }
        else {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(
                    IntentAttribute.ACTION_EXECUTE_COMMAND.addContext(context)
                )) {
                    String enumName = intent.getStringExtra(
                        IntentAttribute.EXTRA_COMMAND_ENUM_NAME.addContext(context)
                    );
                    if (enumName != null) {
                        try {
                            MpcCommand command      = MpcCommand.valueOf(enumName);
                            RemoteMpc mpcConnection = new RemoteMpc(srv, mpcCallback);

                            mpcConnection.execute(command);
                        }
                        catch(Exception e) {}
                    }
                }
            }
        }

        super.onReceive(context, intent);
    }
}
