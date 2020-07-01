package com.raveena.fridgemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This class takes care of the user adding an item to their database
 * The information given by the user will be used with the Database Helper class
 * that stores the information in a SQLlite database
 */

public class AddItemActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText et_foodName;
    EditText et_location;
    Switch sw_isFrozen;
    Button btn_addItem;
    Button btn_pickDate;
    Spinner spn_categories;
    FloatingActionButton back;
    String dateAdded;
    Calendar c;

    DatabaseHelper databaseHelper;
    String categoryPicked;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Hooking up all the widgets to be accessed
        et_foodName = findViewById(R.id.et_addFood);
        et_location = findViewById(R.id.et_location);
        sw_isFrozen = findViewById(R.id.sw_frozen);
        btn_addItem = findViewById(R.id.btn_addItem);
        spn_categories = findViewById(R.id.sp_categories);
        back = findViewById(R.id.fab_backAdd);
        btn_pickDate = findViewById(R.id.btn_pickDate);

        databaseHelper = new DatabaseHelper(getApplicationContext());

        // Initialize the drop down bar to have all the category names
        List<String> categoryNames = new ArrayList<>();
        addCategoryNames(categoryNames);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_item, categoryNames);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_categories.setAdapter(arrayAdapter);

        // Creating an onClickListener that stores which category the user clicked on
        spn_categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryPicked = spn_categories.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Button onclickListener that stores the date user bought item
        btn_pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");

            }
        });




        // Setting onclickListener for when the "Add New Item" button is clicked
        btn_addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoodModel foodModel;
                boolean status = false;

                // Setting a try-catch to make sure there are no errors
                try {

                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                    // Made foodModel object
                    foodModel = new FoodModel(-1, et_foodName.getText().toString(), categoryPicked,
                            et_location.getText().toString(), sw_isFrozen.isChecked());

                    // Assign bought date to current time
                    foodModel.setBoughtDate(dateAdded);

                    // Assign expirationTime variable
                    foodModel.assignExpiryTime();
                    // from current time, add expiration-time
                    Calendar newDate = c;
                    newDate.add(Calendar.DATE, foodModel.getExpirationTime());
                    foodModel.setFinishByDate(format1.format(newDate.getTime()));
                    System.out.println("OLD " + foodModel.getBoughtDate() +
                            " NEW " + foodModel.getFinishByDate());

                    if ((et_foodName.getText().toString().length() == 0) ||
                            (et_location.getText().toString().length() == 0) ||
                            (categoryPicked.equals("Pick a category"))) {
                        throw new Exception();
                    }
                    
                    // Adding foodModel object into database
                    status = databaseHelper.addFoodItem(foodModel);

                } catch (Exception e) {
                    // Error checking
                    foodModel = new FoodModel(-1, "error", "error", "error",
                                      false);
                    Toast.makeText(getApplicationContext(), "Error while adding item. Try again!\n" +
                                    "Make sure all fields are filled",
                            Toast.LENGTH_SHORT).show();
                }

                // It the item is added properly, a success message will be shown
                if (status) {
                    Toast.makeText(getApplicationContext(), "Item added successfully " ,
                            Toast.LENGTH_SHORT).show();
                }


            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goBack = new Intent(getApplicationContext(), DashboardNavigation.class);
                startActivity(goBack);
            }
        });

    }

    // method that adds the category names to the List<String>
    public void addCategoryNames(List<String> categories) {
        categories.add("Pick a category");
        categories.add("Fruits");
        categories.add("Vegetables");
        categories.add("Dairy");
        categories.add("Grains");
        categories.add("Meat &/Or Eggs");
        categories.add("Fish");
        categories.add("Other");

    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dateAdded = year + "-" + (month + 1) + "-" + dayOfMonth;
        btn_pickDate.setText("Date: " + dateAdded);
        btn_pickDate.setBackgroundColor(getResources().getColor(R.color.red_color));
    }

    // To display the 3 dots in Action Bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent goToActivity;
        if (item.getItemId() == R.id.settings) {
            goToActivity = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(goToActivity);
            return true;
        } else if (item.getItemId() == R.id.help) {
            goToActivity = new Intent(getApplicationContext(), HelpActivity.class);
            startActivity(goToActivity);
            return true;
        } else if (item.getItemId() == R.id.Contact) {
            goToActivity = new Intent(getApplicationContext(), ContactActivity.class);
            startActivity(goToActivity);
            return true;
        } else if (item.getItemId() == R.id.About) {
            goToActivity = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(goToActivity);
            return true;
        }
        return false;
    }
}
