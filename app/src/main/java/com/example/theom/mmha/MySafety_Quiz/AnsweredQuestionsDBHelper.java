package com.example.theom.mmha.MySafety_Quiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by theom on 03/04/2017.
 */

public class AnsweredQuestionsDBHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "answered_questions.db";
    public static final String TABLE_NAME = "user_answers";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "DATE_OF_BIRTH";
    public static final String COL_3 = "GENDER";
    public static final String COL_4 = "RELATIONSHIP";
    public static final String COL_5 = "ETHNICITY";
    public static final String COL_6 = "MAPPA";
    public static final String COL_7 = "NOTES";
    public static final String COL_8 = "DATETIME";
    public static final String COL_9 = "ANSWERS";
    String TAG = "DBHelper";

    public AnsweredQuestionsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create table with appropriate column headers
        db.execSQL("create table "+ TABLE_NAME + "("+COL_1+" INTEGER PRIMARY KEY AUTOINCREMENT,"+COL_2+" STRING,"+COL_3+" STRING,"+COL_4
                +" STRING,"+COL_5+" STRING,"+COL_6+" STRING,"+COL_7+" STRING,"+COL_8+" DATETIME DEFAULT CURRENT_TIMESTAMP,"+COL_9+" STRING)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Delete table if it already exists
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    //Function to insert data into the DB
    public long insertData(String date_of_birth, String gender, String relationship, String ethnicity, String mappa){
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            //Take user paramas and insert into DB
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_2, date_of_birth);
            contentValues.put(COL_3, gender);
            contentValues.put(COL_4, relationship);
            contentValues.put(COL_5, ethnicity);
            contentValues.put(COL_6, mappa);

            //If insert failed, return false, else true
            long result = db.insertOrThrow(TABLE_NAME, null, contentValues);
            if (result == -1) {
                return result;
            }else {
                Log.i(TAG, "Inserted the values: "+date_of_birth + ", "+ relationship + " with an ID "+result);
                return result;
            }
        }catch (SQLiteConstraintException e){
            return 0;
        }
    }

    //Return all data from the DB
    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME, null);
        return res;
    }

   /* public String getLastInsertID(){
        SQLiteDatabase db = this.getWritableDatabase();
        //last_insert_rowid()
    }*/

    //Return all data from the DB
    public Cursor getAssessmentDetails(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+ " WHERE "+COL_1+"=\""+id+"\"", null);
        return res;
    }

    //Delete data from the DB
    public Integer deleteValue(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Integer deletedNum = db.delete(TABLE_NAME, "ID = ?", new String[]{id});
        return deletedNum;
    }

    //Insert Assessment Answers
    public Integer insertAssessmentAnswers(String id, String userAnswers){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_9, userAnswers);

        //userAnswers = userAnswers.substring(1, userAnswers.length()-1);
        Log.i("DB_Helper", "Update query: user answers "+userAnswers + "on id "+id);
        Integer result = db.update(TABLE_NAME, cv, "ID = ?",new String[]{id});


        return result;
    }

}
