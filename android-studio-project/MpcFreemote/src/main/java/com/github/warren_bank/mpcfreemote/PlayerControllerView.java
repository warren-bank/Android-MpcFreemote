package com.github.warren_bank.mpcfreemote;

import com.github.warren_bank.mpcfreemote.R;
import com.github.warren_bank.mpcfreemote.model.MpcStatus;
import com.github.warren_bank.mpcfreemote.mpc_connector.MpcCommand;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class PlayerControllerView extends MpcFragment
                                  implements View.OnClickListener,
                                             SeekBar.OnSeekBarChangeListener,
                                             MpcStatus.UICallback {
    private Activity activity;
    private MpcStatus mpcStatus = null;

    /* Android stuff                                                          */
    /**************************************************************************/
    public PlayerControllerView() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_player_controller_view, container, false);

        v.findViewById(R.id.wPlayer_PlayPosition_JumpBack).setOnClickListener(this);
        v.findViewById(R.id.wPlayer_PlayPosition).setOnClickListener(this);
        v.findViewById(R.id.wPlayer_PlayPosition_JumpForward).setOnClickListener(this);
        v.findViewById(R.id.wPlayer_BtnPrev).setOnClickListener(this);
        v.findViewById(R.id.wPlayer_BtnNext).setOnClickListener(this);
        v.findViewById(R.id.wPlayer_Volume).setOnClickListener(this);
        v.findViewById(R.id.wPlayer_BtnPlayPause).setOnClickListener(this);

        ((SeekBar) v.findViewById(R.id.wPlayer_Volume)).setOnSeekBarChangeListener(this);
        ((SeekBar) v.findViewById(R.id.wPlayer_PlayPosition)).setOnSeekBarChangeListener(this);

        v.findViewById(R.id.wPlayer_ToggleMoreOptions).setOnClickListener(this);
        v.findViewById(R.id.wPlayer_SetTheme).setOnClickListener(this);
        v.findViewById(R.id.wPlayer_ToggleStatusUpdates).setOnClickListener(this);

        return v;
    }

    @Override
    public void onAttach(@NonNull Context activity) {
        super.onAttach(activity);
        this.activity = (Activity) activity;
        this.mpcStatus = new MpcStatus(mpcProvider, activity, this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
        this.mpcStatus.setEnabled(false);
        this.mpcStatus = null;
    }

    /* Event handlers                                                         */
    /**************************************************************************/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wPlayer_PlayPosition_JumpBack: onPlayPosition_JumpBackClicked(); break;
            case R.id.wPlayer_PlayPosition_JumpForward: onPlayPosition_JumpForwardClicked(); break;
            case R.id.wPlayer_BtnPrev: onBtnPrevClicked(); break;
            case R.id.wPlayer_BtnNext: onBtnNextClicked(); break;
            case R.id.wPlayer_BtnPlayPause: onBtnPlayPauseClicked(); break;
            case R.id.wPlayer_ToggleMoreOptions: onToggleMoreOptionsClicked(); break;
            case R.id.wPlayer_SetTheme: toggleTheme(); break;
            case R.id.wPlayer_ToggleStatusUpdates: toggleStatusUpdates(); break;
            default:
                throw new RuntimeException(getClass().getName() + " received an event it doesn't know how to handle.");
        }
    }

    @Override public void onStartTrackingTouch(SeekBar seekBar) {}
    @Override public void onStopTrackingTouch(SeekBar seekBar) {}
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) return;
        switch (seekBar.getId()) {
            case R.id.wPlayer_PlayPosition: onPlayPositionClicked(progress); break;
            case R.id.wPlayer_Volume: onVolumeClicked(progress); break;
        }
    }

    @Override
    public void onMpcStatusUpdate(MpcStatus.Info info) {
        if (this.activity == null) return;

        final TextView statusTxt     = this.activity.findViewById(R.id.wPlayer_CurrentlyPlaying);
        final SeekBar volumeCtrl     = this.activity.findViewById(R.id.wPlayer_Volume);
        final SeekBar posCtrl        = this.activity.findViewById(R.id.wPlayer_PlayPosition);
        final TextView currentPosTxt = this.activity.findViewById(R.id.wPlayer_PlayPosition_CurrentPositionText);
        final TextView lengthTxt     = this.activity.findViewById(R.id.wPlayer_PlayPosition_Length);

        int positionPercent = (info.duration > 0)
            ? (int) (((float)info.position / (float)info.duration) * 100)
            : 0;

        statusTxt.setText(info.statestring + " - " + info.file);
        volumeCtrl.setProgress(info.volumelevel);
        posCtrl.setProgress(positionPercent);
        currentPosTxt.setText(info.positionstring);
        lengthTxt.setText(info.durationstring);
    }

    private void onToggleMoreOptionsClicked() {
        if (this.activity == null) return;

        View panel = this.activity.findViewById(R.id.wPlayer_ExtraOptions);
        if (panel.getVisibility() == View.GONE) {
            panel.setVisibility(View.VISIBLE);
        } else {
            panel.setVisibility(View.GONE);
        }
    }

    /* Mpc interaction                                                        */
    /**************************************************************************/

    private void onPlayPosition_JumpBackClicked() {
        getMpc().execute(MpcCommand.JUMP_BACKWARD_SMALL);
    }

    private void onPlayPosition_JumpForwardClicked() {
        getMpc().execute(MpcCommand.JUMP_FORWARD_MEDIUM);
    }

    private void onBtnPrevClicked() {
        getMpc().execute(MpcCommand.PREVIOUS);
    }

    private void onBtnNextClicked() {
        getMpc().execute(MpcCommand.NEXT);
    }

    private void onBtnPlayPauseClicked() {
        getMpc().execute(MpcCommand.PLAY_PAUSE);
    }

    private void onBtnMuteClicked() {
        getMpc().execute(MpcCommand.VOLUME_MUTE);
    }

    private void onPlayPositionClicked(int progress) {
        getMpc().seekByPercent(progress);
    }

    private void onVolumeClicked(int progress) {
        getMpc().setVolume(progress);
    }

    /* Settings: Theme                                                        */
    /**************************************************************************/
    /* TODO: These actually belong in local settings.
             They are here simply to prototype theme setting,
             and because there's no 'official' settings view.                 */
    /**************************************************************************/
    /* NOTE: Adding a theme-toggle in the main menu is somewhat hackish,
     *       but the alternative is adding this in a dedicated settings tab.
     *       This should be ok for now.                                       */
    /**************************************************************************/

    private static SharedPreferences getSharedPrefs(final Context ctx) {
        return ctx.getSharedPreferences("app_startup", Context.MODE_PRIVATE);
    }

    public static boolean shouldUseDarkTheme(final Context ctx) {
        return shouldUseDarkTheme(getSharedPrefs(ctx));
    }

    public static boolean shouldUseDarkTheme(final SharedPreferences cfg) {
        return cfg.getBoolean("UseDarkTheme", false);
    }

    private void toggleTheme() {
        getSharedPrefs(activity).edit()
                                .putBoolean("UseDarkTheme", ! shouldUseDarkTheme(activity))
                                .apply();

        try {
            activity.setTheme(R.style.DarkTheme);

            final String name = requireContext().getPackageName();
            final Intent intent = requireContext().getPackageManager().getLaunchIntentForPackage(name);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } catch (Exception ex) {
            final String msg = getString(R.string.status_theme_apply_fail);
            Toast toast = Toast.makeText(getContext(), msg, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void toggleStatusUpdates() {
        if (this.mpcStatus == null) return;
        this.mpcStatus.toggleEnabled();

        if (this.mpcStatus.isEnabled()) return;
        if (this.activity == null) return;

        final TextView statusTxt     = this.activity.findViewById(R.id.wPlayer_CurrentlyPlaying);
        final TextView currentPosTxt = this.activity.findViewById(R.id.wPlayer_PlayPosition_CurrentPositionText);
        final TextView lengthTxt     = this.activity.findViewById(R.id.wPlayer_PlayPosition_Length);

        statusTxt.setText("");
        currentPosTxt.setText("00:00");
        lengthTxt.setText("100%");
    }
}
