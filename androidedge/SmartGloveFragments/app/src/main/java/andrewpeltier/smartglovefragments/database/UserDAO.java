package andrewpeltier.smartglovefragments.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;

/* Statements used inside of the database */
@Dao
public interface UserDAO {

    @Query("SELECT * FROM users WHERE id=:userId")
    LiveData<User> getUserById(int userId);

    @Query("SELECT id FROM users")
    List<Integer> getAllIDs();

    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllUsers();

    @Query("SELECT * FROM users")
    List<User> getAllUsersStudyVersion();

    @Insert
    void insertUser(User users);

    @Query("UPDATE users SET ex_hands_rest=:exr WHERE id=:id")
    void insertScr_exer_handRest(String exr, int id);

    @Query("UPDATE users SET ex_hands_out=:exr WHERE id=:id")
    void insertScr_exer_handOut(String exr, int id);

    @Query("UPDATE users SET ex_fin_nose=:exr WHERE id=:id")
    void insertScr_exer_finNose(String exr, int id);

    @Query("UPDATE users SET ex_fin_tap=:exr WHERE id=:id")
    void insertScr_exer_finTap(String exr, int id);

    @Query("UPDATE users SET ex_op_cl=:exr WHERE id=:id")
    void insertScr_exer_opCl(String exr, int id);

    @Query("UPDATE users SET ex_h_flip=:exr WHERE id=:id")
    void insertScr_exer_hFlip(String exr,int id);

    @Query("UPDATE users SET ex_heel_stmp=:exr WHERE id=:id")
    void insertScr_exer_heelStmp(String exr, int id);

    @Query("UPDATE users SET ex_toe_tap=:exr WHERE id=:id")
    void insertScr_exer_toeTap(String exr, int id);

    @Query("UPDATE users SET ex_gait=:exr WHERE id=:id")
    void insertScr_exer_gait(String exr, int id);

    @Delete
    void deleteUser(User user);

    @Query("DELETE FROM users")
    void deleteAllUsers();
}
