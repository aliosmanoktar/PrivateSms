/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.SmsManager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class PrivateDatabaseHelper extends SQLiteOpenHelper {

    private static final int DatabaseVersion=1;
    private static final String DataBaseName="Private.db";
    private final String CreatePrivateNumber_SQL="CREATE TABLE "+ PrivateNumberEntity.TableName+" ("+
            PrivateNumberEntity.ID+" INTEGER PRIMARY KEY,"+
            PrivateNumberEntity.NumberColumn+" TEXT)";
    private final String DeleteTable_SQL=" DROP TABLE IF EXISTS "+PrivateNumberEntity.TableName;

    public PrivateDatabaseHelper(Context context) {
        super(context, DataBaseName, null, DatabaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreatePrivateNumber_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DeleteTable_SQL);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db,oldVersion,newVersion);
    }
}