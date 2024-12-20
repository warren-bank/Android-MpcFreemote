package com.github.warren_bank.mpcfreemote;

import com.github.warren_bank.mpcfreemote.R;
import com.github.warren_bank.mpcfreemote.RemoteControlView;
import com.github.warren_bank.mpcfreemote.WidgetProvider;
import com.github.warren_bank.mpcfreemote.mpc_connector.MpcCommand;

import androidx.appcompat.app.AppCompatActivity;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class WidgetConfigActivity extends AppCompatActivity
                                  implements View.OnClickListener {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private Spinner commandSpinner = null;
    private Button submitButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setResult(RESULT_CANCELED);
        setContentView(R.layout.activity_widget_config);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            );
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        final int spinnerPosition = sp.getInt("widget_" + appWidgetId + "_spinner_position", Spinner.INVALID_POSITION);

        commandSpinner = (Spinner) findViewById(R.id.widget_config_command_spinner);
        submitButton   = (Button)  findViewById(R.id.widget_config_submit_button);

        commandSpinner.setAdapter(
            new ArrayAdapter<MpcCommand>(this, android.R.layout.simple_spinner_item, RemoteControlView.getRemoteControlCommands())
        );
        if (spinnerPosition != Spinner.INVALID_POSITION) {
            commandSpinner.setSelection(spinnerPosition);
        }
        commandSpinner.setVisibility(View.VISIBLE);

        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.widget_config_submit_button:
                try {
                    MpcCommand command = (MpcCommand) commandSpinner.getSelectedItem();

                    final int    spinnerPosition       = commandSpinner.getSelectedItemPosition();
                    final String enumName              = command.name();
                    final int    commandNameResourceId = command.getCommandNameResourceId();
                    onConfigured(spinnerPosition, enumName, commandNameResourceId);
                }
                catch (Exception e) {}
                break;
        }
    }

    public void onConfigured(final int spinnerPosition, final String enumName, final int commandNameResourceId) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();

        editor.putInt(   "widget_" + appWidgetId + "_spinner_position",   spinnerPosition);
        editor.putString("widget_" + appWidgetId + "_enum_name",          enumName);
        editor.putInt(   "widget_" + appWidgetId + "_command_name_resid", commandNameResourceId);
        editor.commit();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        WidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetId);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}
