package com.github.warren_bank.mpcfreemote;

import com.github.warren_bank.mpcfreemote.mpc_connector.RemoteMpc;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.Context;

public abstract class MpcFragment extends Fragment {
    protected RemoteMpc.ConnectionProvider mpcProvider;

    @Override
    public void onAttach(@NonNull Context activity) {
        super.onAttach(activity);

        try {
            mpcProvider = (RemoteMpc.ConnectionProvider) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement RemoteMpc.ConnectionProvider");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mpcProvider = null;
    }

    RemoteMpc getMpc() {
        return mpcProvider.getActiveMpcConnection();
    }
}
