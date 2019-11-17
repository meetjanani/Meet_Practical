package com.example.meet_practical.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.meet_practical.UserList_Bean.ResultsItem;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Users_Db_Helper extends SQLiteOpenHelper {


  private static final int Db_Version = 1;
  private static final String Db_Name = "User_Summery";
  private static final String Table_Name = "User_tbl";

  // Columns
  private static final String id = "id";
  private static final String json_str = "json_str";


  public Users_Db_Helper(Context context) {
    super(context, Db_Name, null, Db_Version);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    String Create_Table = "CREATE TABLE " + Table_Name +
            "(" +
            id + " INTEGER PRIMARY KEY," +
            json_str + " TEXT)";
    db.execSQL(Create_Table);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + Table_Name);
    onCreate(db);
  }

  // Display All Users in Descending Order...
  public List<ResultsItem> getAllUser() {
    Gson gson = new Gson();
    List<ResultsItem> order_list = new ArrayList<ResultsItem>();
    // Select All Query
    String selectQuery = "SELECT  *   FROM " + Table_Name + " order by id desc";

    SQLiteDatabase db = this.getWritableDatabase();
    Cursor cursor = db.rawQuery(selectQuery, null);

    // looping through all rows and adding to list
    if (cursor.moveToFirst()) {
      do {
        String json = new String();
        json = cursor.getString(1);

        ResultsItem obj = gson.fromJson(json, ResultsItem.class);

        order_list.add(obj);
      } while (cursor.moveToNext());
    }

    // return contact list
    if (order_list != null && order_list.size() >  0)
    {
      return order_list;
    }
    else {
      return  null;
    }

  }

  // Insert User
  public void Insert_user(String Json_Data) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues cv = new ContentValues();
    cv.put(json_str, Json_Data);

    db.insert(Table_Name, null, cv);
    db.close();
  }

  // Delete User
  public void Delete(String Json_Data) {
    SQLiteDatabase db = this.getWritableDatabase();
    db.delete(Table_Name, json_str + " in  ('" + Json_Data + "')",
            null);
    db.close();
  }

}