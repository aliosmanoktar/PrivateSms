/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.SmsManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class PrivateDatabase {
    private Context ctx;
    private PrivateDatabaseHelper helper;
    private SQLiteDatabase db;

    public PrivateDatabase(Context ctx) {
        this.ctx = ctx;
        helper = new PrivateDatabaseHelper(ctx);
        db = helper.getWritableDatabase();
    }

    public void AddNumber(Long ThreadID, String Number) {
        ContentValues values = new ContentValues();
        values.put(PrivateNumberEntity.ThreadID, ThreadID);
        values.put(PrivateNumberEntity.Number, Number);
        db.insert(PrivateNumberEntity.TableName, null, values);
    }

    public List<Long> getAllPrivateNumbers() {
        List<Long> ThreadIDs = new ArrayList<>();
        Cursor cr = db.query(PrivateNumberEntity.TableName, null, null, null, null, null, null);
        while (cr.moveToNext()) {
            Long ThreadID = cr.getLong(cr.getColumnIndex(PrivateNumberEntity.ThreadID));
            ThreadIDs.add(ThreadID);
        }
        return ThreadIDs;
    }

    public void RemoveNumber(Long ThreadID) {
        db.delete(PrivateNumberEntity.TableName, PrivateNumberEntity.ThreadID + " = " + ThreadID, null);
    }

    public void RemovePinnedNumber(Long ThreadID) {
        db.delete(PrivateNumberEntity.PinnedTableName, PrivateNumberEntity.ThreadID + " = " + ThreadID, null);
    }

    public void AddPinnedNumber(Long ThreadID) {
        ContentValues values = new ContentValues();
        values.put(PrivateNumberEntity.ThreadID, ThreadID);
        db.insert(PrivateNumberEntity.PinnedTableName, null, values);
    }

    public List<Long> getAllPinnedNumbers() {
        List<Long> ThreadIDs = new ArrayList<>();
        Cursor cr = db.query(PrivateNumberEntity.PinnedTableName, null, null, null, null, null, null);
        while (cr.moveToNext()) {
            Long ThreadID = cr.getLong(cr.getColumnIndex(PrivateNumberEntity.ThreadID));
            ThreadIDs.add(ThreadID);
        }
        return ThreadIDs;
    }

    public boolean CheckNumber(String number) {
        Cursor cr = db.query(PrivateNumberEntity.TableName, null, PrivateNumberEntity.Number + " = '" + number + "'", null, null, null, null);
        return cr.moveToNext();
    }
}