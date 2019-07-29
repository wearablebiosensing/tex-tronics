package andrewpeltier.smartglovefragments.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class StudyLog {

    private String header = "Time, Age, Gender, Handedness, Diagnoses Duration, Time Since Dose, State, Comments";
    Date date = Calendar.getInstance().getTime();

    // Set Default Output File
    String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date);
    String timeString = new SimpleDateFormat("kk_mm_ss_SSS", Locale.US).format(date);


    public int  StudyLog(int id, String info){

        String fileName = dateString + "/" + id +"/" + timeString + "patient_info_.csv";
        File parentFile = new File("/storage/emulated/0/Documents");    // FIXME
        File file = new File(parentFile, fileName);
        boolean newFile = false;

        String insrt_string = timeString + "," + info;
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

        // write to the file
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);


            if (newFile) {
                // if flagged as a new file, apply the header before the data.
                bufferedWriter.write(header);
                bufferedWriter.newLine();

            }

            bufferedWriter.write(insrt_string);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 1;
    }

}
