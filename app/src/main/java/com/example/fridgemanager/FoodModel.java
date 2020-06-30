package com.example.fridgemanager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Food Model class that stores the Food Objects that SQL Database holds
 */

public class FoodModel {

    // Creating fields Food Model will have
    private int id;
    private String category;
    private String location;
    private String foodName;
    private String boughtDate;
    private int expirationTime;    // Based on category, gives days it expires in
    private String finishByDate;    // actually the date food expires
    private boolean isFrozen;


    // Constructor that initializes everything
    public FoodModel(int id, String foodName, String category, String location, boolean isFrozen) {
        this.id = id;
        this.category = category;
        this.foodName = foodName;
        this.location = location;
        this.isFrozen = isFrozen;

        // Putting temp values - will change throughout program
        this.expirationTime = -1;
        this.finishByDate = "";
        this.boughtDate = "";
    }

    // Default Constructor
    public FoodModel() {

    }

    /* Making Getters and Setters */

    // Get Id
    public int getId() {
        return id;
    }

    // Set Id
    public void setId(int id) {
        this.id = id;
    }

    // Get Food Name
    public String getFoodName() {
        return foodName;
    }

    // Set Food Name
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    // Get Location
    public String getLocation() {
        return location;
    }

    // Set Location
    public void setLocation(String location) {
        this.location = location;
    }

    // Get Frozen Status
    public boolean isFrozen() {
        return isFrozen;
    }

    // Set Frozen Status
    public void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }

    // Get Insert Date
    public String getBoughtDate() {
        return boughtDate;
    }

    // Set Insert Date
    public void setBoughtDate(String boughtDate) {
        this.boughtDate = boughtDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(int expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getFinishByDate() {
        return finishByDate;
    }

    public void setFinishByDate(String finishByDate) {
        this.finishByDate = finishByDate;
    }

    @Override
    public String toString() {
        return getFoodName() + " from " + getLocation();
    }

    public String getFullInfo() {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        return getId()+ ", " + getCategory() + ", " + getLocation() + ", " + getFoodName()
                + ", " + getBoughtDate() + ", " + getExpirationTime()
                + ", " + getFinishByDate()
                + ", " + isFrozen();
    }

    // Method to assign date finished time (basically expiration date)
    public void assignExpiryTime() {
        switch (getCategory()) {
            case "Fruits":
            case "Vegetables":
            case "Dairy":
            case "Grains":
                expirationTime = 14;
                break;
            case "Meat &/Or Eggs":
            case "Fish":
                if (isFrozen()) {
                    expirationTime = 30;
                }
                else {
                    expirationTime = 7;
                }
                break;
            case "Other":
                expirationTime = 15;
                break;

        }
    }


}
