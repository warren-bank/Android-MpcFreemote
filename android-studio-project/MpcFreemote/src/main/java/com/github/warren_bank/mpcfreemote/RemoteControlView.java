package com.github.warren_bank.mpcfreemote;

import com.github.warren_bank.mpcfreemote.R;
import com.github.warren_bank.mpcfreemote.mpc_connector.MpcCommand;

import com.google.android.flexbox.FlexboxLayout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

public class RemoteControlView extends MpcFragment implements View.OnClickListener {

    protected static List<MpcCommand> getRemoteControlCommands() {
        List<MpcCommand> remoteControlCommands = List.of(
            MpcCommand.PLAY_PAUSE, MpcCommand.PAUSE, MpcCommand.PLAY, MpcCommand.STOP, MpcCommand.CLOSE, MpcCommand.EXIT,
            MpcCommand.PREVIOUS, MpcCommand.NEXT,
            MpcCommand.JUMP_TO_BEGINNING, MpcCommand.JUMP_BACKWARD_LARGE, MpcCommand.JUMP_BACKWARD_MEDIUM, MpcCommand.JUMP_BACKWARD_SMALL, MpcCommand.JUMP_FORWARD_SMALL, MpcCommand.JUMP_FORWARD_MEDIUM, MpcCommand.JUMP_FORWARD_LARGE,
            MpcCommand.VOLUME_MUTE, MpcCommand.VOLUME_DOWN, MpcCommand.VOLUME_UP, MpcCommand.VOLUME_BOOST_MIN, MpcCommand.VOLUME_BOOST_DECREASE, MpcCommand.VOLUME_BOOST_INCREASE,
            MpcCommand.AUDIO_DELAY_MINUS_10_MS, MpcCommand.AUDIO_DELAY_PLUS_10_MS,
            MpcCommand.SUBTITLE_DELAY_MINUS, MpcCommand.SUBTITLE_DELAY_PLUS,
            MpcCommand.RESET_RATE, MpcCommand.DECREASE_RATE, MpcCommand.INCREASE_RATE,
            MpcCommand.VIEW_MINIMAL, MpcCommand.VIEW_NORMAL, MpcCommand.FULLSCREEN, MpcCommand.ALWAYS_ON_TOP
        );

        return remoteControlCommands;
    }

    /* Mostly Android boilerplate                               */
    /************************************************************/
    public RemoteControlView() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_remote_control_view, container, false);

        FlexboxLayout flexbox_layout = v.findViewById(R.id.flexbox_layout);

        for (MpcCommand command : getRemoteControlCommands()) {
            Button button = new Button(getActivity());
            button.setTag(command);
            button.setText(command.toString());
            button.setOnClickListener(this);

            flexbox_layout.addView(button);
        }

        return v;
    }

    /* Display & event handling                                 */
    /************************************************************/

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();

        if ((tag != null) && (tag instanceof MpcCommand)) {
            MpcCommand command = (MpcCommand) tag;

            getMpc().execute(command);
        }
    }

}
