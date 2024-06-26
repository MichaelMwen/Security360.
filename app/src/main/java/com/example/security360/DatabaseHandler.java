package com.example.security360;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Security360.db";
    public static final String TABLE_NAME = "Contacts";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "CONTACT_NO";


    public DatabaseHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT , NAME TEXT , CONTACT_NO INTEGER )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //to add new contact
    public boolean insertData(String name, String num) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, num);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    //to get all contacts
    public Cursor getAllContacts() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_2 + " ASC", null);
    }

    //to delete selected contact
    public boolean deleteContact(String phoneNumber) {

        SQLiteDatabase db = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM " + TABLE_NAME + " WHERE " + COL_3 + " = " + phoneNumber;
        Cursor cursor = db.rawQuery(deleteQuery, null);

        if (cursor.moveToFirst()) {
            db.close();
            cursor.close();
            return true;
        } else {
            db.close();
            cursor.close();
            return false;
        }
    }
    
    //search functionality
    public Cursor searchContacts(String keyword) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_2 + " LIKE ?" + " ORDER BY " + COL_2 + " ASC", new String[]{"%" + keyword + "%"});
        return cursor;
    }

    //to update the contact
    public void updateContact(String oldPhoneNumber, String name, String newPhoneNumber) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_2, name);
        values.put(COL_3, newPhoneNumber);

        db.update(TABLE_NAME, values, COL_3 + " = ?",
                new String[]{oldPhoneNumber});
        db.close();
    }

    //get the contact from the database
    public int getContact(String phoneNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_3 + " = " + phoneNo, null);
        int noOfContacts = cursor.getCount();
        cursor.close();
        return noOfContacts;
    }

    //get the name from the database
    public int getName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_2 + " = '" + name + "'", null);
        int noOfContacts = cursor.getCount();
        cursor.close();
        return noOfContacts;
    }
}
