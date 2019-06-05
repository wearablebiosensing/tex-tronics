package andrewpeltier.smartglovefragments.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

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

    /*added a new column*/
    @ColumnInfo(name = "amount")
    private float amount;
    /*and also a constructor as well as getter and setter function*/

    @ColumnInfo(name = "initComments")
    private String init_comments;

    @ColumnInfo(name = "finComments")
    private String fin_comments;

    @ColumnInfo(name = "ex_hands_rest_left",typeAffinity = ColumnInfo.TEXT)
    private String data_hands_thighs_left;

    @ColumnInfo(name = "ex_hands_rest_right",typeAffinity = ColumnInfo.TEXT)
    private String data_hands_thighs_right;

    @ColumnInfo(name = "score_hands_thighs")
    private int score_hands_thighs;

    @ColumnInfo(name = "ex_hands_out_left",typeAffinity = ColumnInfo.TEXT)
    private String data_hands_out_left;

    @ColumnInfo(name = "ex_hands_out_right",typeAffinity = ColumnInfo.TEXT)
    private String data_hands_out_right;

    @ColumnInfo(name = "score_hands_out")
    private int score_hands_out;

    @ColumnInfo(name = "ex_fin_nose_left",typeAffinity = ColumnInfo.TEXT)
    private String data_fin_nose_left;

    @ColumnInfo(name = "ex_fin_nose_right",typeAffinity = ColumnInfo.TEXT)
    private String data_fin_nose_right;

    @ColumnInfo(name = "score_fin_nose")
    private int score_fin_nose;

    @ColumnInfo(name = "ex_fin_tap_left",typeAffinity = ColumnInfo.TEXT)
    private String data_fin_tap_left;

    @ColumnInfo(name = "ex_fin_tap_right",typeAffinity = ColumnInfo.TEXT)
    private String data_fin_tap_right;

    @ColumnInfo(name = "score_fin_tap")
    private int score_fin_tap;

    @ColumnInfo(name = "ex_op_cl_left",typeAffinity = ColumnInfo.TEXT)
    private String data_op_cl_left;

    @ColumnInfo(name = "ex_op_cl_right",typeAffinity = ColumnInfo.TEXT)
    private String data_op_cl_right;

    @ColumnInfo(name = "score_op_cl")
    private int score_op_cl;

    @ColumnInfo(name = "ex_h_flip_left",typeAffinity = ColumnInfo.TEXT)
    private String data_h_flip_left;

    @ColumnInfo(name = "ex_h_flip_right",typeAffinity = ColumnInfo.TEXT)
    private String data_h_flip_right;

    @ColumnInfo(name = "score_h_flip")
    private int score_h_flip;

    @ColumnInfo(name = "ex_heel_stmp_left",typeAffinity = ColumnInfo.TEXT)
    private String data_heel_stmp_left;

    @ColumnInfo(name = "ex_heel_stmp_right",typeAffinity = ColumnInfo.TEXT)
    private String data_heel_stmp_right;

    @ColumnInfo(name = "score_heel_stmp")
    private int score_heel_stmp;

    @ColumnInfo(name = "ex_toe_tap_left",typeAffinity = ColumnInfo.TEXT)
    private String data_toe_tap_left;

    @ColumnInfo(name = "ex_toe_tap_right",typeAffinity = ColumnInfo.TEXT)
    private String data_toe_tap_right;

    @ColumnInfo(name = "score_toe_tap")
    private int score_toe_tap;

    @ColumnInfo(name = "ex_gait_left",typeAffinity = ColumnInfo.TEXT)
    private String data_gait_left;

    @ColumnInfo(name = "ex_gait_right",typeAffinity = ColumnInfo.TEXT)
    private String data_gait_right;

    @ColumnInfo(name = "score_gait")
    private int score_gait;

    public User(){

    }

    @Ignore
    public User(int age, int gender, int hand, int duration, float dose, int feel, float amount, String comments){
        this.age = age;
        this.gender = gender;
        this.hand = hand;
        this.duration = duration;
        this.dose = dose;
        this.feel = feel;
        this.amount = amount;
        this.init_comments = comments;
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

    public float getAmount() { return amount; }

    public void setAmount(float amount) { this.amount = amount; }

    public String getInit_comments() {
        return init_comments;
    }

    public void setInit_comments(String init_comments) {
        this.init_comments = init_comments;
    }

    public String getFin_comments() {
        return fin_comments;
    }

    public void setFin_comments(String fin_comments) {
        this.fin_comments = fin_comments;
    }

    public String getData_hands_thighs_left() {
        return data_hands_thighs_left;
    }

    public void setData_hands_thighs_left(String data_hands_thighs_left) {
        this.data_hands_thighs_left = data_hands_thighs_left;
    }

    public String getData_hands_thighs_right() {
        return data_hands_thighs_right;
    }

    public void setData_hands_thighs_right(String data_hands_thighs_right) {
        this.data_hands_thighs_right = data_hands_thighs_right;
    }

    public int getScore_hands_thighs() {
        return score_hands_thighs;
    }

    public void setScore_hands_thighs(int score_hands_thighs) {
        this.score_hands_thighs = score_hands_thighs;
    }

    public String getData_hands_out_left() {
        return data_hands_out_left;
    }

    public void setData_hands_out_left(String data_hands_out_left) {
        this.data_hands_out_left = data_hands_out_left;
    }

    public String getData_hands_out_right() {
        return data_hands_out_right;
    }

    public void setData_hands_out_right(String data_hands_out_right) {
        this.data_hands_out_right = data_hands_out_right;
    }

    public int getScore_hands_out() {
        return score_hands_out;
    }

    public void setScore_hands_out(int score_hands_out) {
        this.score_hands_out = score_hands_out;
    }

    public String getData_fin_nose_left() {
        return data_fin_nose_left;
    }

    public void setData_fin_nose_left(String data_fin_nose_left) {
        this.data_fin_nose_left = data_fin_nose_left;
    }

    public String getData_fin_nose_right() {
        return data_fin_nose_right;
    }

    public void setData_fin_nose_right(String data_fin_nose_right) {
        this.data_fin_nose_right = data_fin_nose_right;
    }

    public int getScore_fin_nose() {
        return score_fin_nose;
    }

    public void setScore_fin_nose(int score_fin_nose) {
        this.score_fin_nose = score_fin_nose;
    }

    public String getData_fin_tap_left() {
        return data_fin_tap_left;
    }

    public void setData_fin_tap_left(String data_fin_tap_left) {
        this.data_fin_tap_left = data_fin_tap_left;
    }

    public String getData_fin_tap_right() {
        return data_fin_tap_right;
    }

    public void setData_fin_tap_right(String data_fin_tap_right) {
        this.data_fin_tap_right = data_fin_tap_right;
    }

    public int getScore_fin_tap() {
        return score_fin_tap;
    }

    public void setScore_fin_tap(int score_fin_tap) {
        this.score_fin_tap = score_fin_tap;
    }

    public String getData_op_cl_left() {
        return data_op_cl_left;
    }

    public void setData_op_cl_left(String data_op_cl_left) {
        this.data_op_cl_left = data_op_cl_left;
    }

    public String getData_op_cl_right() {
        return data_op_cl_right;
    }

    public void setData_op_cl_right(String data_op_cl_right) {
        this.data_op_cl_right = data_op_cl_right;
    }

    public int getScore_op_cl() {
        return score_op_cl;
    }

    public void setScore_op_cl(int score_op_cl) {
        this.score_op_cl = score_op_cl;
    }

    public String getData_h_flip_left() {
        return data_h_flip_left;
    }

    public void setData_h_flip_left(String data_h_flip_left) {
        this.data_h_flip_left = data_h_flip_left;
    }

    public String getData_h_flip_right() {
        return data_h_flip_right;
    }

    public void setData_h_flip_right(String data_h_flip_right) {
        this.data_h_flip_right = data_h_flip_right;
    }

    public int getScore_h_flip() {
        return score_h_flip;
    }

    public void setScore_h_flip(int score_h_flip) {
        this.score_h_flip = score_h_flip;
    }

    public String getData_heel_stmp_left() {
        return data_heel_stmp_left;
    }

    public void setData_heel_stmp_left(String data_heel_stmp_left) {
        this.data_heel_stmp_left = data_heel_stmp_left;
    }

    public String getData_heel_stmp_right() {
        return data_heel_stmp_right;
    }

    public void setData_heel_stmp_right(String data_heel_stmp_right) {
        this.data_heel_stmp_right = data_heel_stmp_right;
    }

    public int getScore_heel_stmp() {
        return score_heel_stmp;
    }

    public void setScore_heel_stmp(int score_heel_stmp) {
        this.score_heel_stmp = score_heel_stmp;
    }

    public String getData_toe_tap_left() {
        return data_toe_tap_left;
    }

    public void setData_toe_tap_left(String data_toe_tap_left) {
        this.data_toe_tap_left = data_toe_tap_left;
    }

    public String getData_toe_tap_right() {
        return data_toe_tap_right;
    }

    public void setData_toe_tap_right(String data_toe_tap_right) {
        this.data_toe_tap_right = data_toe_tap_right;
    }

    public int getScore_toe_tap() {
        return score_toe_tap;
    }

    public void setScore_toe_tap(int score_toe_tap) {
        this.score_toe_tap = score_toe_tap;
    }

    public String getData_gait_left() {
        return data_gait_left;
    }

    public void setData_gait_left(String data_gait_left) {
        this.data_gait_left = data_gait_left;
    }

    public String getData_gait_right() {
        return data_gait_right;
    }

    public void setData_gait_right(String data_gait_right) {
        this.data_gait_right = data_gait_right;
    }

    public int getScore_gait() {
        return score_gait;
    }

    public void setScore_gait(int score_gait) {
        this.score_gait = score_gait;
    }
}
