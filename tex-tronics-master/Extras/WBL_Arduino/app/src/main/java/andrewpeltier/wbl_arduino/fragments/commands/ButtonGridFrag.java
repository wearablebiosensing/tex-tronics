package andrewpeltier.wbl_arduino.fragments.commands;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import andrewpeltier.wbl_arduino.R;
import andrewpeltier.wbl_arduino.bluetooth.BluetoothService;

public class ButtonGridFrag extends Fragment
{
    private static final String TAG = "ButtonGridFrag";
    private Button  btn0, btn1, btn2,
                    btn3, btn4, btn5,
                    btn6, btn7, btn8, btn9;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fcom_button_grid, container, false);

        setButtons(view);

        Log.d(TAG, "onCreateView: View created");
        return view;
    }

    private View.OnClickListener sendButtonVal(String value)
    {
        final String mValue = value;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "sendButtonVal: sending value: "  + mValue);
//        BluetoothService.write(getActivity(), value);
            }
        };
        return listener;
    }

    private void setButtons(View view)
    {
        Log.d(TAG, "setButtons: Setting buttons...");
        btn0 = view.findViewById(R.id.btn0);
        btn1 = view.findViewById(R.id.btn1);
        btn2 = view.findViewById(R.id.btn2);
        btn3 = view.findViewById(R.id.btn3);
        btn4 = view.findViewById(R.id.btn4);
        btn5 = view.findViewById(R.id.btn5);
        btn6 = view.findViewById(R.id.btn6);
        btn7 = view.findViewById(R.id.btn7);
        btn8 = view.findViewById(R.id.btn8);
        btn9 = view.findViewById(R.id.btn9);

        btn0.setOnClickListener(sendButtonVal(btn0.getText().toString()));
        btn1.setOnClickListener(sendButtonVal(btn1.getText().toString()));
        btn2.setOnClickListener(sendButtonVal(btn2.getText().toString()));
        btn3.setOnClickListener(sendButtonVal(btn3.getText().toString()));
        btn4.setOnClickListener(sendButtonVal(btn4.getText().toString()));
        btn5.setOnClickListener(sendButtonVal(btn5.getText().toString()));
        btn6.setOnClickListener(sendButtonVal(btn6.getText().toString()));
        btn7.setOnClickListener(sendButtonVal(btn7.getText().toString()));
        btn8.setOnClickListener(sendButtonVal(btn8.getText().toString()));
        btn9.setOnClickListener(sendButtonVal(btn9.getText().toString()));
    }
}
