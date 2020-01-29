package andrewpeltier.smartglovefragments.database;

public class StudyInformation {

    public String exercise_column;
    public String score_column;

    public StudyInformation(){

    }

    public StudyInformation(String column_ex, String column_score){
        this.exercise_column = column_ex;
        this.score_column = column_score;
    }
}
