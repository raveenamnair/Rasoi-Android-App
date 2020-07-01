package com.raveena.fridgemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This is a helper class that deals with all the Database and SQL knowledge
 * This class is in charge of store information to the database, removing items, and updating them
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // Making constants that will be used throughout program
    public static final String TABLE_NAME = "FOOD_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_FOOD_CATEGORY = "FOOD_CATEGORY";
    public static final String COLUMN_FOOD_LOCATION = "FOOD_LOCATION";
    public static final String COLUMN_FOOD_NAME = "FOOD_NAME";
    public static final String COLUMN_DATE_BOUGHT = "DATE_BOUGHT";
    public static final String COLUMN_EXPIRATION = "EXPIRATION_TIME";
    public static final String COLUMN_DATE_FINISHED = "DATE_FINISHED";
    public static final String COLUMN_FROZEN = "FROZEN";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "fridge.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_FOOD_CATEGORY + " TEXT, "
                + COLUMN_FOOD_LOCATION + " TEXT, " + COLUMN_FOOD_NAME + " TEXT, "
                + COLUMN_DATE_BOUGHT + " TEXT, " + COLUMN_EXPIRATION + " INT, "
                + COLUMN_DATE_FINISHED + " TEXT, " + COLUMN_FROZEN + " BOOL)";

        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Currently nothing in here
    }

    public boolean addFoodItem(FoodModel foodModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        // Adding values into database
        cv.put(COLUMN_FOOD_CATEGORY, foodModel.getCategory());
        cv.put(COLUMN_FOOD_LOCATION, foodModel.getLocation());
        cv.put(COLUMN_FOOD_NAME, foodModel.getFoodName());
        cv.put(COLUMN_FROZEN, foodModel.isFrozen());
        //SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        cv.put(COLUMN_DATE_BOUGHT, foodModel.getBoughtDate());
        cv.put(COLUMN_DATE_FINISHED, foodModel.getFinishByDate());
        cv.put(COLUMN_EXPIRATION, foodModel.getExpirationTime());

        long insert = db.insert(TABLE_NAME, null, cv);
        if (insert == -1) {
            return false;
        }
        else {
            return true;
        }
    }

    public boolean removeFoodItem(FoodModel foodModel) {
        SQLiteDatabase db = getWritableDatabase();
//        String queryString = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = '" + foodModel.getId() + "'";
//        Cursor cursor = db.rawQuery(queryString, null);
//        if (cursor.moveToFirst()) {
//            return true;
//        }
//        else {
//            return false;
//        }
        return db.delete(TABLE_NAME, COLUMN_ID + "=" + foodModel.getId(), null) > 0;
    }



    public List<FoodModel> getFoodList() {
        List<FoodModel> returnList = new ArrayList<>();

        // Get data from database
        String queryString = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase(); // You don't need writableDatabase
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            // loop through the cursor (result set) and create new Customer objects
            // Put them into return list
            do {
                int foodId = cursor.getInt(0);
                String category = cursor.getString(1);
                String location = cursor.getString(2);
                String foodName = cursor.getString(3);
                String dateBought = cursor.getString(4);
                int expiryTime = cursor.getInt(5);
                String dateFinishBy = cursor.getString(6);
                boolean isFrozen = cursor.getInt(7) == 1 ? true: false; // ternary operation

                FoodModel newFoodItem = new FoodModel(foodId, foodName, category, location,
                                        isFrozen);
                newFoodItem.setBoughtDate(dateBought);
                newFoodItem.setFinishByDate(dateFinishBy);
                newFoodItem.setExpirationTime(expiryTime);
                returnList.add(newFoodItem);

            } while(cursor.moveToNext());   // Proceed through database one at a time
        }
        else {
            // failure. do not add anything to the list
        }

        cursor.close(); // IMPORTANT
        db.close(); // IMPORTANT

        return returnList;
    }

    public List<FoodModel> getExpiryList() {
        List<FoodModel> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_DATE_FINISHED + " ASC";
        SQLiteDatabase db = this.getReadableDatabase(); // You don't need writableDatabase
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                int foodId = cursor.getInt(0);
                String category = cursor.getString(1);
                String location = cursor.getString(2);
                String foodName = cursor.getString(3);
                String dateBought = cursor.getString(4);
                int expiryTime = cursor.getInt(5);
                String dateFinishBy = cursor.getString(6);
                boolean isFrozen = cursor.getInt(7) == 1 ? true: false; // ternary operation

                FoodModel newFoodItem = new FoodModel(foodId, foodName, category, location,
                        isFrozen);
                newFoodItem.setBoughtDate(dateBought);
                newFoodItem.setFinishByDate(dateFinishBy);
                newFoodItem.setExpirationTime(expiryTime);
                System.out.println(newFoodItem.toString());
                returnList.add(newFoodItem);
            } while (cursor.moveToNext());
        }

        cursor.close(); // IMPORTANT
        db.close(); // IMPORTANT

        return returnList;

    }

    public boolean getFrozenStatus(int ID) {
        String query = "SELECT " +  COLUMN_FROZEN + " FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = " + ID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        boolean result = false;
        if (cursor.moveToFirst()) {
            System.out.println(cursor.getString(0));
            if (cursor.getString(0).equalsIgnoreCase("1")) {
                result = true;
            }
        }
        cursor.close();
        db.close();
        return result;
    }

    public String getDateBought(int ID) {
        String query = "SELECT " +  COLUMN_DATE_BOUGHT + " FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = " + ID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        String result = null;
        if (cursor.moveToFirst()) {
            System.out.println(cursor.getString(0));
            result = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return result;
    }

    public String getColumnFoodCategory(int ID) {
        String query = "SELECT " +  COLUMN_FOOD_CATEGORY + " FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = " + ID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        String result = null;
        if (cursor.moveToFirst()) {
            System.out.println(cursor.getString(0));
            result = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return result;
    }

    public void updateFoodName(int ID, String value) {
        String query = "UPDATE " + TABLE_NAME +  " SET " + COLUMN_FOOD_NAME + " = '" + value +
                "' WHERE " + COLUMN_ID + " = " + ID;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);

    }
    public void updateFoodLocation(int ID, String value) {
        String query = "UPDATE " + TABLE_NAME +  " SET " + COLUMN_FOOD_LOCATION + " = '" + value +
                "' WHERE " + COLUMN_ID + " = " + ID;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }

    public void updateFoodCategory(int ID, String value) {
        String query = "UPDATE " + TABLE_NAME +  " SET " + COLUMN_FOOD_CATEGORY + " = '" + value +
                "' WHERE " + COLUMN_ID + " = " + ID;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }

    public void updateFoodExpirationTime(int ID, int value) {
        String query = "UPDATE " + TABLE_NAME +  " SET " + COLUMN_EXPIRATION + " = " + value +
                " WHERE " + COLUMN_ID + " = " + ID;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }

    public void updateFoodBoughtDate(int ID, String value) {
        String query = "UPDATE " + TABLE_NAME +  " SET " + COLUMN_DATE_BOUGHT + " = '" + value +
                "' WHERE " + COLUMN_ID + " = " + ID;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);

    }

    public void updateFoodFinishByDate(int ID, String value) {
        String query = "UPDATE " + TABLE_NAME +  " SET " + COLUMN_DATE_FINISHED + " = '" + value +
                "' WHERE " + COLUMN_ID + " = " + ID;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public void updateFrozenStatus(int ID, int status) {
        String query = "UPDATE " + TABLE_NAME +  " SET " + COLUMN_FROZEN + " = " + status +
                " WHERE " + COLUMN_ID + " = " + ID;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);

    }
}
