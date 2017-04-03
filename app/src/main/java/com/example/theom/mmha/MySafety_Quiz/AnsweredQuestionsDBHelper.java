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
    public static final String COL_2 = "QUESTION_SESSION";
    public static final String COL_3 = "PATIENT_NAME";
    public static final String COL_4 = "INTERVIEWER_NAME";
    public static final String COL_5 = "ANSWERED_QUESTIONS";
    public static final String COL_6 = "NOTES";
    public static final String COL_7 = "DATETIME";

    public AnsweredQuestionsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create table with appropriate column headers
        db.execSQL("create table "+ TABLE_NAME + "("+COL_1+" INTEGER PRIMARY KEY AUTOINCREMENT,"+COL_2+" STRING,"+COL_3+" STRING,"+COL_4
                +" STRING,"+COL_5+" STRING,"+COL_6+" STRING,"+COL_7+" DATETIME DEFAULT CURRENT_TIMESTAMP)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Delete table if it already exists
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    //Function to insert data into the DB
    public boolean insertData(String question_session, String patient_name, String interviewer_name, String answered_questions, String notes){
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            //Take user paramas and insert into DB
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_2, question_session);
            contentValues.put(COL_3, patient_name);
            contentValues.put(COL_4, interviewer_name);
            contentValues.put(COL_5, answered_questions);
            contentValues.put(COL_6, notes);

            //If insert failed, return false, else true
            long result = db.insertOrThrow(TABLE_NAME, null, contentValues);
            if (result == -1) {
                return false;
            }else {
                return true;
            }
        }catch (SQLiteConstraintException e){
            return false;
        }
    }

    //Return all data from the DB
    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME, null);
        return res;
    }

    //Return all data from the DB
    public Cursor getLocationData(String locationTitle){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+ " WHERE "+COL_1+"=\""+locationTitle+"\"", null);
        return res;
    }

    //Return all data from the DB
    public Integer deleteValue(String locationTitle){
        SQLiteDatabase db = this.getWritableDatabase();
        Integer deletedNum = db.delete(TABLE_NAME, "NAME = ?", new String[]{locationTitle});
        return deletedNum;
    }

    //Update user notes
    public Integer updateUserNotes(String locationTitle, String userNotes){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_1, locationTitle);
        cv.put(COL_6, userNotes);

        Log.i("DB_Helper", "Update query: "+locationTitle+ " notes "+userNotes);
        Integer result = db.update(TABLE_NAME, cv, "NAME = ?", new String[]{locationTitle});
        return result;
    }

    //Update user notes
    public Integer updateIsVisited(String locationTitle, Boolean isVisited){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_1, locationTitle);

        Log.i("DB_Helper", "Update query: "+locationTitle+ " isVisted: "+isVisited);
        Integer result = db.update(TABLE_NAME, cv, "NAME = ?", new String[]{locationTitle});
        return result;
    }


    public Integer addLocationPhoto(String locationTitle, byte[] image) throws SQLiteException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new  ContentValues();
        cv.put(COL_7,   image);
        Integer result = db.update(TABLE_NAME, cv, "NAME = ?", new String[]{locationTitle});
        return result;
    }

    public Integer addUserPhoto(String locationTitle, byte[] image, String whichCol) throws SQLiteException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new  ContentValues();
        cv.put(whichCol,   image);
        Integer result = db.update(TABLE_NAME, cv, "NAME = ?", new String[]{locationTitle});
        return result;
    }

    public boolean isLocationFavourite(String locationTitle) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        int count = -1;
        Cursor c = null;
        try {
            String query = "SELECT COUNT(*) FROM "
                    + TABLE_NAME + " WHERE " + COL_1 + " = ?";
            c = db.rawQuery(query, new String[] {locationTitle});
            if (c.moveToFirst()) {
                count = c.getInt(0);
            }
            return count > 0;
        }
        finally {
            if (c != null) {
                c.close();
            }
        }
    }
}
