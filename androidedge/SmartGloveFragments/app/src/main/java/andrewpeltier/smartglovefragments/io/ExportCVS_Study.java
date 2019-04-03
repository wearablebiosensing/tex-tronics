package andrewpeltier.smartglovefragments.io;

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

    private String header = "ID, Age, Gender, Hand, Feeling, Duration, Dose, Comments, Hands Thighs, Hands out," +
            "Finger Nose, Finger Tap, Open Close, Hand Flip, Heel Stomp, Toe Tap, Gait";
    Date date = Calendar.getInstance().getTime();

    // Set Default Output File
    String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date);
    String dateStringID = new SimpleDateFormat("MM.dd.yyyy", Locale.US).format(date);
    String timeString = new SimpleDateFormat("kk_mm_ss_SSS", Locale.US).format(date);

    String fileName = dateString + "/" + "_glove_info_selectAll.csv";
    File parentFile = new File("/storage/emulated/0/Documents");    // FIXME
    File file = new File(parentFile, fileName);
    boolean newFile = false;

    public int  ExportCSV_Study(List<User> usr){

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

        for(int i = 0; i < usr.size(); i++){

            // write to the file
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

                User tmp_user = usr.get(i);

                String usr_string =
                        tmp_user.getId() + "," +
                        tmp_user.getAge()+ "," +
                        tmp_user.getGender()+ "," +
                        tmp_user.getHand()+ "," +
                        tmp_user.getFeel()+ "," +
                        tmp_user.getDuration()+ "," +
                        tmp_user.getDose()+ "," +
                        tmp_user.getComments()+ "," +
                        tmp_user.getData_hands_thighs()+ "," +
                        tmp_user.getData_hands_out()+ "," +
                        tmp_user.getData_fin_nose()+ "," +
                        tmp_user.getData_fin_tap() + "," +
                        tmp_user.getData_op_cl()+ "," +
                        tmp_user.getData_h_flip()+ "," +
                        tmp_user.getData_heel_stmp()+ "," +
                        tmp_user.getData_toe_tap()+ "," +
                        tmp_user.getData_gait();

            if (newFile) {
                // if flagged as a new file, apply the header before the data.
                bufferedWriter.write(header);
                bufferedWriter.newLine();

            }

                bufferedWriter.write(usr_string);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                bufferedWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return 1;
    }

}
