/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.SmsManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class PrivateDatabase {
    private Context ctx;
    private PrivateDatabaseHelper helper;
    private SQLiteDatabase db;

    public PrivateDatabase(Context ctx) {
        this.ctx = ctx;
        helper=new PrivateDatabaseHelper(ctx);
        db = helper.getWritableDatabase();
    }

    public void AddNumber(String number){
        ContentValues values= new ContentValues();
        values.put(PrivateNumberEntity.NumberColumn,number);
        db.insert(PrivateNumberEntity.TableName,number,values);
    }

    public List<String> getAllPrivateNumbers(){
        List<String> numbers=new ArrayList<>();
        Cursor cr=db.query(PrivateNumberEntity.TableName,null,null,null,null,null,null);
        while (cr.moveToNext()){
            String number = cr.getString(cr.getColumnIndex(PrivateNumberEntity.NumberColumn));
            numbers.add(number);
        }
        return numbers;
    }

    public void RemoveNumber(String Number){
        db.delete(PrivateNumberEntity.TableName,PrivateNumberEntity.NumberColumn+" = '"+Number+"'",null);
    }
}