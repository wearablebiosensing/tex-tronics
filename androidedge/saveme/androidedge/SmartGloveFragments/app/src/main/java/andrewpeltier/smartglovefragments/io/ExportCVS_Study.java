package andrewpeltier.smartglovefragments.io;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import andrewpeltier.smartglovefragments.database.User;

public class ExportCVS_Study {

    String TAG = "export study ";
    private String header = "ID, Age, Gender, Hand, Feeling, Duration, Dose, Amount, Comments, Hands Thighs, Hands out," +
            "Finger Nose, Finger Tap, Open Close, Hand Flip, Heel Stomp, Toe Tap, Gait";
    Date date = Calendar.getInstance().getTime();

    // Set Default Output File
    String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date);
   // String dateStringID = new SimpleDateFormat("MM.dd.yyyy", Locale.US).format(date);
    String timeString = new SimpleDateFormat("kk_mm_ss_SSS", Locale.US).format(date);

    public int  ExportCSV_Study(User usr, int id){

        String fileName = dateString + "/" + id + "/" + timeString + "_complete_study_smart_glove.csv";
        File parentFile = new File("/storage/emulated/0/Documents");    // FIXME
        File file = new File(parentFile, fileName);
        boolean newFile = false;

        Log.d(TAG, "ExportCSV_Study: user id " + Integer.toString(usr.getId() )+ " act id " + Integer.toString(id));

        if (!file.exists()) {
            newFile = true;
            file.getParentFile().mkdirs();
            try {
                if (!file.createNewFile()) {
                    return 0;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //for(int i = 0; i < usr.size(); i++){

            // write to the file
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

                User tmp_user = usr;

                Log.d(TAG, "ExportCSV_Study: Here in export study");

                String usr_string = tmp_user.getId() + "," +
                                tmp_user.getAge()+ "," +
                                tmp_user.getGender()+ "," +
                                tmp_user.getHand()+ "," +
                                tmp_user.getFeel()+ "," +
                                tmp_user.getDuration()+ "," +
                                tmp_user.getDose()+ "," +
                                tmp_user.getAmount()+ "," +
                                tmp_user.getInit_comments()+ "," +
                                tmp_user.getData_hands_thighs_left()+ "," +
                                tmp_user.getData_hands_thighs_right()+ "," +
                                tmp_user.getScore_hands_thighs()+ "," +
                                tmp_user.getData_hands_out_left()+ "," +
                                tmp_user.getData_hands_out_right()+ "," +
                                tmp_user.getScore_hands_out()+ "," +
                                tmp_user.getData_fin_nose_left()+ "," +
                                tmp_user.getData_fin_nose_right()+ "," +
                                tmp_user.getScore_fin_nose()+ "," +
                                tmp_user.getData_fin_tap_left() + "," +
                                tmp_user.getData_fin_tap_right() + "," +
                                tmp_user.getScore_fin_tap() + "," +
                                tmp_user.getData_op_cl_left()+ "," +
                                tmp_user.getData_op_cl_right()+ "," +
                                tmp_user.getScore_op_cl()+ "," +
                                tmp_user.getData_h_flip_left()+ "," +
                                tmp_user.getData_h_flip_right()+ "," +
                                tmp_user.getScore_h_flip()+ "," +
                                tmp_user.getData_heel_stmp_left()+ "," +
                                tmp_user.getData_heel_stmp_right()+ "," +
                                tmp_user.getScore_heel_stmp()+ "," +
                                tmp_user.getData_toe_tap_left()+ "," +
                                tmp_user.getData_toe_tap_right()+ "," +
                                tmp_user.getScore_toe_tap()+ "," +
                                tmp_user.getData_gait_left()+ "," +
                                tmp_user.getData_gait_right()+ "," +
                                tmp_user.getScore_gait()+ "," +
                                tmp_user.getFin_comments();

                String test = tmp_user.getFin_comments();
                Log.d(TAG, "ExportCSV_Study: test comments " + test);


            if (newFile) {
                // if flagged as a new file, apply the header before the data.
                bufferedWriter.write(header);
                bufferedWriter.newLine();

            }

                Log.d(TAG, "ExportCSV_Study: user string : " + usr_string + "\n");
                bufferedWriter.write(usr_string);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                bufferedWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

       // }

        return 1;
    }

}
