package com.example.h_mal.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.h_mal.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by h_mal on 04/04/2017.
 */

public class ProductDbHelper extends SQLiteOpenHelper{

    public static final String LOG_TAG = ProductDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "inventory.db";

    private static final int DATABASE_VERSION = 1;


    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_PRODUCTS_TABLE =  "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.COLUMN_PRODUCT_IMAGE + " TEXT, "
                + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProductEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL DEFAULT 0)";

        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
