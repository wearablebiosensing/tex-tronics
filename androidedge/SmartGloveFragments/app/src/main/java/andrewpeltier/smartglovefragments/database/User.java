package andrewpeltier.smartglovefragments.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.sql.Blob;

import io.reactivex.annotations.NonNull;

/* Setting up the table that will be inside the database. */
@Entity(tableName = "users")
public class User {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "age")
    private int age;


    @ColumnInfo(name = "gender")
    private int gender;

    @ColumnInfo(name = "handedness")
    private int hand;

    @ColumnInfo(name = "duration")
    private int duration;

    @ColumnInfo(name = "dose")
    private float dose;

    @ColumnInfo(name = "on_off")
    private int feel;

    @ColumnInfo(name = "comments")
    private String comments;

    @ColumnInfo(name = "ex_hands_rest",typeAffinity = ColumnInfo.TEXT)
    private String data_hands_thighs;

//    @ColumnInfo(name = "score_hands_r")
//    private int score_hands_thighs;

    @ColumnInfo(name = "ex_hands_out",typeAffinity = ColumnInfo.TEXT)
    private String data_hands_out;

//    @ColumnInfo(name = "score_hands_out")
//    private int score_hands_out;

    @ColumnInfo(name = "ex_fin_nose",typeAffinity = ColumnInfo.TEXT)
    private String data_fin_nose;

//    @ColumnInfo(name = "score_fin_nose")
//    private int score_fin_nose;

    @ColumnInfo(name = "ex_fin_tap",typeAffinity = ColumnInfo.TEXT)
    private String data_fin_tap;

//    @ColumnInfo(name = "score_fin_tap")
//    private int score_fin_tap;

    @ColumnInfo(name = "ex_op_cl",typeAffinity = ColumnInfo.TEXT)
    private String data_op_cl;

//    @ColumnInfo(name = "score_op_cl")
//    private int score_op_cl;

    @ColumnInfo(name = "ex_h_flip",typeAffinity = ColumnInfo.TEXT)
    private String data_h_flip;

//    @ColumnInfo(name = "score_h_flip")
//    private int score_h_flip;

    @ColumnInfo(name = "ex_heel_stmp",typeAffinity = ColumnInfo.TEXT)
    private String data_heel_stmp;

//    @ColumnInfo(name = "score_heel_stmp")
//    private int score_heel_stmp;

    @ColumnInfo(name = "ex_toe_tap",typeAffinity = ColumnInfo.TEXT)
    private String data_toe_tap;

//    @ColumnInfo(name = "score_toe_tap")
//    private int score_toe_tap;

    @ColumnInfo(name = "ex_gait",typeAffinity = ColumnInfo.TEXT)
    private String data_gait;

//    @ColumnInfo(name = "score_gait")
//    private int score_gait;

    public User(){

    }

    @Ignore
    public User(int age, int gender, int hand, int duration, float dose, int feel, String comments){
        this.age = age;
        this.gender = gender;
        this.hand = hand;
        this.duration = duration;
        this.dose = dose;
        this.feel = feel;
        this.comments = comments;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getHand() {
        return hand;
    }

    public void setHand(int hand) {
        this.hand = hand;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getDose() {
        return dose;
    }

    public void setDose(float dose) {
        this.dose = dose;
    }

    public int getFeel() {
        return feel;
    }

    public void setFeel(int feel) {
        this.feel = feel;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getData_hands_thighs() {
        return data_hands_thighs;
    }

    public void setData_hands_thighs(String data_hands_thighs) {
        this.data_hands_thighs = data_hands_thighs;
    }

//    public int getScore_hands_thighs() {
//        return score_hands_thighs;
//    }
//
//    public void setScore_hands_thighs(int score_hands_thighs) {
//        this.score_hands_thighs = score_hands_thighs;
//    }

    public String getData_hands_out() {
        return data_hands_out;
    }

    public void setData_hands_out(String data_hands_out) {
        this.data_hands_out = data_hands_out;
    }

//    public int getScore_hands_out() {
//        return score_hands_out;
//    }
//
//    public void setScore_hands_out(int score_hands_out) {
//        this.score_hands_out = score_hands_out;
//    }

    public String getData_fin_nose() {
        return data_fin_nose;
    }

    public void setData_fin_nose(String data_fin_nose) {
        this.data_fin_nose = data_fin_nose;
    }

//    public int getScore_fin_nose() {
//        return score_fin_nose;
//    }
//
//    public void setScore_fin_nose(int score_fin_nose) {
//        this.score_fin_nose = score_fin_nose;
//    }

    public String getData_fin_tap() {
        return data_fin_tap;
    }

    public void setData_fin_tap(String data_fin_tap) {
        this.data_fin_tap = data_fin_tap;
    }

//    public int getScore_fin_tap() {
//        return score_fin_tap;
//    }
//
//    public void setScore_fin_tap(int score_fin_tap) {
//        this.score_fin_tap = score_fin_tap;
//    }

    public String getData_op_cl() {
        return data_op_cl;
    }

    public void setData_op_cl(String data_op_cl) {
        this.data_op_cl = data_op_cl;
    }

//    public int getScore_op_cl() {
//        return score_op_cl;
//    }
//
//    public void setScore_op_cl(int score_op_cl) {
//        this.score_op_cl = score_op_cl;
//    }

    public String getData_h_flip() {
        return data_h_flip;
    }

    public void setData_h_flip(String data_h_flip) {
        this.data_h_flip = data_h_flip;
    }

//    public int getScore_h_flip() {
//        return score_h_flip;
//    }
//
//    public void setScore_h_flip(int score_h_flip) {
//        this.score_h_flip = score_h_flip;
//    }

    public String getData_heel_stmp() {
        return data_heel_stmp;
    }

    public void setData_heel_stmp(String data_heel_stmp) {
        this.data_heel_stmp = data_heel_stmp;
    }

//    public int getScore_heel_stmp() {
//        return score_heel_stmp;
//    }
//
//    public void setScore_heel_stmp(int score_heel_stmp) {
//        this.score_heel_stmp = score_heel_stmp;
//    }

    public String getData_toe_tap() {
        return data_toe_tap;
    }

    public void setData_toe_tap(String data_toe_tap) {
        this.data_toe_tap = data_toe_tap;
    }

//    public int getScore_toe_tap() {
//        return score_toe_tap;
//    }
//
//    public void setScore_toe_tap(int score_toe_tap) {
//        this.score_toe_tap = score_toe_tap;
//    }

    public String getData_gait() {
        return data_gait;
    }

    public void setData_gait(String data_gait) {
        this.data_gait = data_gait;
    }

//    public int getScore_gait() {
//        return score_gait;
//    }
//
//    public void setScore_gait(int score_gait) {
//        this.score_gait = score_gait;
//    }
}
