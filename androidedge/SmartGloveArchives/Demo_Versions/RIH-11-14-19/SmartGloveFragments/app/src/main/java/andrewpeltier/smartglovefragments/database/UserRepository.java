package andrewpeltier.smartglovefragments.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private String TAG = "USER_REP_";
    private String DB_NAME = "userdb";

    private Context cont;
    private UserDatabase user_db;
    private static UserRepository mInstance;
    private List<Integer> mIds = new ArrayList<>();

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            // If you want to keep the data through app restarts,
            // comment out the following line.
            // new PopulateDbAsync(INSTANCE).execute();
        }
    };

    private UserRepository(Context context){

        this.cont = context;
        user_db = Room.databaseBuilder(context,UserDatabase.class,DB_NAME).addCallback(sRoomDatabaseCallback).build();
    }

    public static synchronized  UserRepository  getInstance(Context context){

        if(mInstance == null){
            mInstance = new UserRepository(context);

        }
        return mInstance;
    }

    public UserDatabase getAppDatabase() {
        return user_db;
    }

    public void insertUsr(int age, int gender, int hand, int duration, float dose, float amount, int o_f, String comments){

        Log.d(TAG, "insertUsr: Insderting the user");
        User usr = new User();
        usr.setAge(age);
        usr.setGender(gender);
        usr.setHand(hand);
        usr.setDuration(duration);
        usr.setDose(dose);
        usr.setAmount(amount);
        usr.setFeel(o_f);
        usr.setInit_comments(comments);

        insertTask(usr, user_db);
    }

    public void delUsr(User usr){
        deleteTask(usr,user_db);
    }


    // TODO h flip left
    public void updateData_h_flip_left(String exe, int id){
        updateTask_Data_h_flip_left(exe, id, user_db);
    }

    public static void  updateTask_Data_h_flip_left(final String exe, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_hFlip_left(exe,id);
                return null;
            }
        }.execute();
    }

    // TODO h flip right
    public void updateData_h_flip_right(String exe, int id){
        updateTask_Data_h_flip_right(exe, id, user_db);
    }

    public static void  updateTask_Data_h_flip_right(final String exe, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_hFlip_right(exe,id);
                return null;
            }
        }.execute();
    }


    // TODO h flip score
    public void updateData_h_flip_score(float scr, int id){
        updateTask_Data_h_flip_score(scr, id, user_db);
    }

    public static void  updateTask_Data_h_flip_score(final float scr, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_h_flip(scr,id);
                return null;
            }
        }.execute();
    }


    // TODO hand rest left
    public void updateData_handrest_left(String exe, int id){
        updateTask_Data_handrest_left(exe,id, user_db);
    }

    public static void  updateTask_Data_handrest_left(final String exe, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_handRest_left(exe,id);
                return null;
            }
        }.execute();
    }

    // TODO hand rest left
    public void updateData_handrest_right(String exe, int id){
        updateTask_Data_handrest_right(exe,id, user_db);
    }

    public static void  updateTask_Data_handrest_right(final String exe, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_handRest_right(exe,id);
                return null;
            }
        }.execute();
    }

    // TODO h rest score
    public void updateData_h_rest_score(float scr, int id){
        updateTask_Data_h_rest_score(scr, id, user_db);
    }

    public static void  updateTask_Data_h_rest_score(final float scr, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_hand_rest(scr,id);
                return null;
            }
        }.execute();
    }

    // TODO hanout left
    public void updateData_handout_left(String exe, int id){
        updateTask_Data_handout_left(exe, id, user_db);
    }


    public static void  updateTask_Data_handout_left(final String exe, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_handOut_left(exe,id);
                return null;
            }
        }.execute();
    }

    // TODO hanout right
    public void updateData_handout_right(String exe, int id){
        updateTask_Data_handout_right(exe, id, user_db);
    }

    public static void  updateTask_Data_handout_right(final String exe, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_handOut_right(exe,id);
                return null;
            }
        }.execute();
    }

    // TODO handout  score
    public void updateData_handout_score(float scr, int id){
        updateTask_Data_handout_score(scr, id, user_db);
    }

    public static void  updateTask_Data_handout_score(final float scr, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_hand_out(scr,id);
                return null;
            }
        }.execute();
    }


    // TODO fin nose left
    public void updateData_finNose_left(String exe, int id){
        updateTask_Data_finNose_left(exe, id, user_db);
    }

    public static void  updateTask_Data_finNose_left(final String exe, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_finNose_left(exe,id);
                return null;
            }
        }.execute();
    }

    // TODO fin nose right
    public void updateData_finNose_right(String exe, int id){
        updateTask_Data_finNose_right(exe, id, user_db);
    }

    public static void  updateTask_Data_finNose_right(final String exe, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_finNose_right(exe,id);
                return null;
            }
        }.execute();
    }

    // TODO fin nose score
    public void updateData_fin_nose_score(float scr, int id){
        updateTask_Data_fin_nose_score(scr, id, user_db);
    }

    public static void  updateTask_Data_fin_nose_score(final float scr, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_fin_nose(scr,id);
                return null;
            }
        }.execute();
    }

    //TODO fin tap left
    public void updateData_finTap_left(String exe, int id){
        updateTask_Data_finTap_left(exe, id, user_db);
    }

    public static void  updateTask_Data_finTap_left(final String exe, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_finTap_left(exe,id);
                return null;
            }
        }.execute();
    }

    //TODO fin tap right
    public void updateData_finTap_right(String exe, int id){
        updateTask_Data_finTap_right(exe, id, user_db);
    }

    public static void  updateTask_Data_finTap_right(final String exe, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_finTap_right(exe,id);
                return null;
            }
        }.execute();
    }

    // TODO fin tap score
    public void updateData_fin_tap_score(float scr, int id){
        updateTask_Data_fin_tap_score(scr, id, user_db);
    }

    public static void  updateTask_Data_fin_tap_score(final float scr, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_fin_tap(scr,id);
                return null;
            }
        }.execute();
    }


    //TODO open close left
    public void updateData_opCl_left(String exe, int id){
        updateTask_Data_opCl_left(exe, id, user_db);
    }

    public static void  updateTask_Data_opCl_left(final String exe, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_opCl_left(exe,id);
                return null;
            }
        }.execute();
    }

    //TODO open close right
    public void updateData_opCl_right(String exe, int id){
        updateTask_Data_opCl_right(exe, id, user_db);
    }

    public static void  updateTask_Data_opCl_right(final String exe, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_opCl_right(exe,id);
                return null;
            }
        }.execute();
    }

    // TODO open close score
    public void updateData_op_cl_score(float scr, int id){
        updateTask_Data_op_cl_score(scr, id, user_db);
    }

    public static void  updateTask_Data_op_cl_score(final float scr, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_op_cl(scr,id);
                return null;
            }
        }.execute();
    }


    //TODO heel stomp left
    public void updateData_heelStmp_left(String exe, int id){
        updateTask_Data_heelStmp_left(exe, id, user_db);
    }

    public static void  updateTask_Data_heelStmp_left(final String exe, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_heelStmp_left(exe,id);
                return null;
            }
        }.execute();
    }

    //TODO heel stomp right
    public void updateData_heelStmp_right(String exe, int id){
        updateTask_Data_heelStmp_right(exe, id, user_db);
    }

    public static void  updateTask_Data_heelStmp_right(final String exe, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_heelStmp_right(exe,id);
                return null;
            }
        }.execute();
    }

    // TODO heel stmp score
    public void updateData_heel_stmp_score(float scr, int id){
        updateTask_Data_heel_stmp_score(scr, id, user_db);
    }

    public static void  updateTask_Data_heel_stmp_score(final float scr, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_heel_stmp(scr,id);
                return null;
            }
        }.execute();
    }

    // TODO toe tap left
    public void updateData_toeTap_left(String exe, int id){
        updateTask_Data_toeTap_left(exe, id, user_db);
    }

    public static void  updateTask_Data_toeTap_left(final String exe, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_toeTap_left(exe,id);
                return null;
            }
        }.execute();
    }

    // TODO toe tap right
    public void updateData_toeTap_right(String exe, int id){
        updateTask_Data_toeTap_right(exe, id, user_db);
    }

    public static void  updateTask_Data_toeTap_right(final String exe, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_toeTap_right(exe,id);
                return null;
            }
        }.execute();
    }

    // TODO toe tap score
    public void updateData_toe_tap_score(float scr, int id){
        updateTask_Data_toe_tap_score(scr, id, user_db);
    }

    public static void  updateTask_Data_toe_tap_score(final float scr, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_toe_tap(scr,id);
                return null;
            }
        }.execute();
    }


    // TODO gait left
    public void updateData_gait_left(String exe, int id){
        updateTask_Data_gait_left(exe, id, user_db);
    }

    public static void  updateTask_Data_gait_left(final String exe,  final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_gait_left(exe,id);
                return null;
            }
        }.execute();
    }

    // TODO gait right
    public void updateData_gait_right(String exe, int id){
        updateTask_Data_gait_right(exe, id, user_db);
    }

    public static void  updateTask_Data_gait_right(final String exe,  final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_gait_right(exe,id);
                return null;
            }
        }.execute();
    }

    // TODO gait score
    public void updateData_gait_score(float scr, int id){
        updateTask_Data_gait_score(scr, id, user_db);
    }

    public static void  updateTask_Data_gait_score(final float scr, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_gait(scr,id);
                return null;
            }
        }.execute();
    }


    // TODO final comments
    public void updateData_final_comments(String fin_com, int id){
        updateTask_Data_fin_comm(fin_com, id, user_db);
    }

    public static void  updateTask_Data_fin_comm(final String fin_c, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Log.d("UPDATE FIN COMM", "doInBackground: fin comm " + fin_c);
                udb.userDAO().insertFinComments(fin_c,id);
                return null;
            }
        }.execute();
    }

    public static void insertTask(final User usr, final UserDatabase udb) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertUser(usr);
                return null;
            }
        }.execute();
    }

    public static void deleteTask(final User usr, final UserDatabase udb) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().deleteUser(usr);
                return null;
            }
        }.execute();
    }

    public List<Integer> getAllIdentities() throws Exception{
        return new GetUsersIdAsyncTask().execute().get();
    }

    private class GetUsersIdAsyncTask extends AsyncTask<Void, Void,List<Integer>>
    {
        @Override
        protected List<Integer> doInBackground(Void... voids) {
            return user_db.userDAO().getAllIDs();
        }
    }

    public LiveData<User> getUser(int id) {
        return user_db.userDAO().getUserById(id);
    }

    public LiveData<List<User>> getUsers() {
        return user_db.userDAO().getAllUsers();
    }

//    public User[] getUsersStudy() {
//        return user_db.userDAO().getAllUsersStudyVersion();
//    }


    public List<User> getAllUsersFromStudy() throws Exception{
        return new GetUsersAsyncTask().execute().get();
    }

    private class GetUsersAsyncTask extends AsyncTask<Void, Void,List<User>>
    {
        @Override
        protected List<User> doInBackground(Void... voids) {
            return user_db.userDAO().getAllUsersStudyVersion();
        }
    }
}
