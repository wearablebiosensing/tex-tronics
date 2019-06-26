package andrewpeltier.wbl_arduino.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import andrewpeltier.wbl_arduino.R;

public class CommandsFragment extends Fragment
{
    private static final String TAG = "CommandsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView: creating CommandsFragment...");
        View view = inflater.inflate(R.layout.fragment_commands, container, false);


        return view;
    }
}
