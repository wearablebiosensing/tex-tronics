package andrewpeltier.smartglovefragments.database;

import android.content.Context;
import android.util.Log;

import andrewpeltier.smartglovefragments.main_activity.MainActivity;

public class
UpdateData {

    String TAG = "update data";

    public  UpdateData(){

    }

    public void UpdateData(String exe_nm, String dev_nm, int id, String json, Context cnt){

        Log.d(TAG, "UpdateData: logging the data");

        if (exe_nm.equals("Finger_Tap") && dev_nm.equals("LEFT_GLOVE_ADDR"))
        {
            UserRepository.getInstance(cnt).updateData_finTap_left(json,id);
        }
        else if (exe_nm.equals("Closed_Grip") && dev_nm.equals("LEFT_GLOVE_ADDR"))
        {
            UserRepository.getInstance(cnt).updateData_opCl_left(json,id);
        }
        else if (exe_nm.equals("Hand_Flip") && dev_nm.equals("LEFT_GLOVE_ADDR"))
        {
            UserRepository.getInstance(cnt).updateData_h_flip_left(json,id);
        }
        else if (exe_nm.equals("Finger_to_Nose") && dev_nm.equals("LEFT_GLOVE_ADDR"))
        {
            UserRepository.getInstance(cnt).updateData_finNose_left(json,id);
        }
        else if (exe_nm.equals("Hold_Hands_Out") && dev_nm.equals("LEFT_GLOVE_ADDR"))
        {
            UserRepository.getInstance(cnt).updateData_handout_left(json,id);
        }
        else if (exe_nm.equals("Resting_Hands_on_Thighs") && dev_nm.equals("LEFT_GLOVE_ADDR"))
        {
            UserRepository.getInstance(cnt).updateData_handrest_left(json,id);
        }
        else if (exe_nm.equals("Finger_Tap") && dev_nm.equals("RIGHT_GLOVE_ADDR"))
        {
            UserRepository.getInstance(cnt).updateData_finTap_right(json,id);
        }
        else if (exe_nm.equals("Closed_Grip") && dev_nm.equals("RIGHT_GLOVE_ADDR"))
        {
            UserRepository.getInstance(cnt).updateData_opCl_right(json,id);
        }
        else if (exe_nm.equals("Hand_Flip") && dev_nm.equals("RIGHT_GLOVE_ADDR"))
        {
            UserRepository.getInstance(cnt).updateData_h_flip_right(json,id);
        }
        else if (exe_nm.equals("Finger_to_Nose") && dev_nm.equals("RIGHT_GLOVE_ADDR"))
        {
            UserRepository.getInstance(cnt).updateData_finNose_right(json,id);
        }
        else if (exe_nm.equals("Hold_Hands_Out") && dev_nm.equals("RIGHT_GLOVE_ADDR"))
        {
            UserRepository.getInstance(cnt).updateData_handout_right(json,id);
        }
        else if (exe_nm.equals("Resting_Hands_on_Thighs") && dev_nm.equals("RIGHT_GLOVE_ADDR"))
        {
            UserRepository.getInstance(cnt).updateData_handrest_right(json,id);
        }
        else if (exe_nm.equals("Heel_Stomp") && dev_nm.equals("RIGHT_SHOE_ADDR"))
        {
            UserRepository.getInstance(cnt).updateData_heelStmp_right(json,id);
        }
        else if (exe_nm.equals("Toe_Tap") && dev_nm.equals("RIGHT_SHOE_ADDR"))
        {
            UserRepository.getInstance(cnt).updateData_toeTap_right(json,id);
        }
        else if (exe_nm.equals("Walk_Steps") && dev_nm.equals("RIGHT_SHOE_ADDR"))
        {
            UserRepository.getInstance(cnt).updateData_gait_right(json,id);
        }
        else if (exe_nm.equals("Heel_Stomp") && dev_nm.equals("LEFT_SHOE_ADDR"))
        {
            UserRepository.getInstance(cnt).updateData_heelStmp_left(json,id);
        }
        else if (exe_nm.equals("Toe_Tap") && dev_nm.equals("LEFT_SHOE_ADDR"))
        {
            UserRepository.getInstance(cnt).updateData_toeTap_left(json,id);
        }
        else if (exe_nm.equals("Walk_Steps") && dev_nm.equals("LEFT_SHOE_ADDR"))
        {
            UserRepository.getInstance(cnt).updateData_gait_left(json,id);
        }


    }

}
