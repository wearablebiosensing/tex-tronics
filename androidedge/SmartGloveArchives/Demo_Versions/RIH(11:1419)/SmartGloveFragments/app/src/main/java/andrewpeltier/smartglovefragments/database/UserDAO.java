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

    @Query("UPDATE users SET finComments=:fin_com WHERE id=:id")
    void insertFinComments(String fin_com, int id);

    @Query("UPDATE users SET ex_hands_rest_left=:exr WHERE id=:id")
    void insertScr_exer_handRest_left(String exr, int id);

    @Query("UPDATE users SET ex_hands_rest_right=:exr WHERE id=:id")
    void insertScr_exer_handRest_right(String exr, int id);

    @Query("UPDATE users SET score_hands_thighs=:src WHERE id=:id")
    void insertScr_hand_rest(float src, int id);


    @Query("UPDATE users SET ex_hands_out_left=:exr WHERE id=:id")
    void insertScr_exer_handOut_left(String exr, int id);

    @Query("UPDATE users SET ex_hands_out_right=:exr WHERE id=:id")
    void insertScr_exer_handOut_right(String exr, int id);

    @Query("UPDATE users SET score_hands_out=:src WHERE id=:id")
    void insertScr_hand_out(float src, int id);


    @Query("UPDATE users SET ex_fin_nose_left=:exr WHERE id=:id")
    void insertScr_exer_finNose_left(String exr, int id);

    @Query("UPDATE users SET ex_fin_nose_right=:exr WHERE id=:id")
    void insertScr_exer_finNose_right(String exr, int id);

    @Query("UPDATE users SET score_fin_nose=:src WHERE id=:id")
    void insertScr_fin_nose(float src, int id);


    @Query("UPDATE users SET ex_fin_tap_left=:exr WHERE id=:id")
    void insertScr_exer_finTap_left(String exr, int id);

    @Query("UPDATE users SET ex_fin_tap_right=:exr WHERE id=:id")
    void insertScr_exer_finTap_right(String exr, int id);

    @Query("UPDATE users SET score_fin_tap=:src WHERE id=:id")
    void insertScr_fin_tap(float src, int id);



    @Query("UPDATE users SET ex_op_cl_left=:exr WHERE id=:id")
    void insertScr_exer_opCl_left(String exr, int id);

    @Query("UPDATE users SET ex_op_cl_right=:exr WHERE id=:id")
    void insertScr_exer_opCl_right(String exr, int id);

    @Query("UPDATE users SET score_op_cl=:src WHERE id=:id")
    void insertScr_op_cl(float src, int id);


    @Query("UPDATE users SET ex_h_flip_left=:exr WHERE id=:id")
    void insertScr_exer_hFlip_left(String exr,int id);

    @Query("UPDATE users SET ex_h_flip_right=:exr WHERE id=:id")
    void insertScr_exer_hFlip_right(String exr,int id);

    @Query("UPDATE users SET score_h_flip=:src WHERE id=:id")
    void insertScr_h_flip(float src, int id);


    @Query("UPDATE users SET ex_heel_stmp_left=:exr WHERE id=:id")
    void insertScr_exer_heelStmp_left(String exr, int id);

    @Query("UPDATE users SET ex_heel_stmp_right=:exr WHERE id=:id")
    void insertScr_exer_heelStmp_right(String exr, int id);

    @Query("UPDATE users SET score_heel_stmp=:src WHERE id=:id")
    void insertScr_heel_stmp(float src, int id);


    @Query("UPDATE users SET ex_toe_tap_left=:exr WHERE id=:id")
    void insertScr_exer_toeTap_left(String exr, int id);

    @Query("UPDATE users SET ex_toe_tap_right=:exr WHERE id=:id")
    void insertScr_exer_toeTap_right(String exr, int id);

    @Query("UPDATE users SET score_toe_tap=:src WHERE id=:id")
    void insertScr_toe_tap(float src, int id);


    @Query("UPDATE users SET ex_gait_left=:exr WHERE id=:id")
    void insertScr_exer_gait_left(String exr, int id);

    @Query("UPDATE users SET ex_gait_right=:exr WHERE id=:id")
    void insertScr_exer_gait_right(String exr, int id);

    @Query("UPDATE users SET score_gait=:src WHERE id=:id")
    void insertScr_gait(float src, int id);

    @Delete
    void deleteUser(User user);

    @Query("DELETE FROM users")
    void deleteAllUsers();
}
