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

    public void insertUsr(int age, int gender, int hand, int duration, float dose, int o_f, String comments){

        Log.d(TAG, "insertUsr: Insderting the user");
        User usr = new User();
        usr.setAge(age);
        usr.setGender(gender);
        usr.setHand(hand);
        usr.setDuration(duration);
        usr.setDose(dose);
        usr.setFeel(o_f);
        usr.setComments(comments);

        insertTask(usr, user_db);
    }

    public void delUsr(User usr){
        deleteTask(usr,user_db);
    }

    public void updateData_h_flip(String exe, int id){
        updateTask_Data_h_flip(exe, id, user_db);
    }

    // TODO possible need an update task for each column
    public static void  updateTask_Data_h_flip(final String exe, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_hFlip(exe,id);
                return null;
            }
        }.execute();
    }

    public void updateData_handrest(String exe, int id){
        updateTask_Data_handrest(exe,id, user_db);
    }

    // TODO possible need an update task for each column
    public static void  updateTask_Data_handrest(final String exe, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_handRest(exe,id);
                return null;
            }
        }.execute();
    }

    public void updateData_handout(String exe, int id){
        updateTask_Data_handout(exe, id, user_db);
    }

    // TODO possible need an update task for each column
    public static void  updateTask_Data_handout(final String exe, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_handOut(exe,id);
                return null;
            }
        }.execute();
    }

    public void updateData_finNose(String exe, int id){
        updateTask_Data_finNose(exe, id, user_db);
    }

    // TODO possible need an update task for each column
    public static void  updateTask_Data_finNose(final String exe, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_finNose(exe,id);
                return null;
            }
        }.execute();
    }

    public void updateData_finTap(String exe, int id){
        updateTask_Data_finTap(exe, id, user_db);
    }

    // TODO possible need an update task for each column
    public static void  updateTask_Data_finTap(final String exe, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_finTap(exe,id);
                return null;
            }
        }.execute();
    }

    public void updateData_opCl(String exe, int id){
        updateTask_Data_opCl(exe, id, user_db);
    }

    // TODO possible need an update task for each column
    public static void  updateTask_Data_opCl(final String exe, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_opCl(exe,id);
                return null;
            }
        }.execute();
    }


    public void updateData_heelStmp(String exe, int id){
        updateTask_Data_heelStmp(exe, id, user_db);
    }

    // TODO possible need an update task for each column
    public static void  updateTask_Data_heelStmp(final String exe, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_heelStmp(exe,id);
                return null;
            }
        }.execute();
    }


    public void updateData_toeTap(String exe, int id){
        updateTask_Data_toeTap(exe, id, user_db);
    }

    // TODO possible need an update task for each column
    public static void  updateTask_Data_toeTap(final String exe, final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_toeTap(exe,id);
                return null;
            }
        }.execute();
    }


    public void updateData_gait(String exe, int id){
        updateTask_Data_gait(exe, id, user_db);
    }

    // TODO possible need an update task for each column
    public static void  updateTask_Data_gait(final String exe,  final int id, final UserDatabase udb ){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                udb.userDAO().insertScr_exer_gait(exe,id);
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
