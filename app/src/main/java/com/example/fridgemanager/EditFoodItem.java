package com.example.fridgemanager;

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
import java.util.List;
import java.util.Locale;

public class EditFoodItem extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    int foodId;
    DatabaseHelper db;
    EditText name, store;
    String dateBought, category;
    Button btn_dateBought;
    RadioGroup frozenStatus;
    Spinner categories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food_item);

        db = new DatabaseHelper(getApplicationContext());

        name = findViewById(R.id.et_FoodName);
        categories = findViewById(R.id.spinner_Edit);
        store = findViewById(R.id.et_location);
        btn_dateBought = findViewById(R.id.btn_DateBoughtEdit);
        frozenStatus = findViewById(R.id.frozenStatusEdit);
        Button confirm = findViewById(R.id.btn_confirm);
        FloatingActionButton back = findViewById(R.id.fab_backEdit);


        // Getting the Food ID
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            foodId = extras.getInt("Food ID");
            //Toast.makeText(getApplicationContext(), Integer.toString(foodId), Toast.LENGTH_SHORT).show();
        }

        List<String> categoryNames = new ArrayList<>();
        addCategoryNames(categoryNames);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_item, categoryNames);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categories.setAdapter(arrayAdapter);

        categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = categories.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                category = "";
            }
        });

        btn_dateBought.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");

            }
        });

        frozenStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int value = -1;
                switch (checkedId) {
                    case R.id.ChangeToFrozen: {
                        db.updateFrozenStatus(foodId, 1);
                        value = assignExpiryTime(db.getColumnFoodCategory(foodId), true);

                        break;
                    }

                    case R.id.ChangeToNotFrozen: {
                        db.updateFrozenStatus(foodId, 0);
                        value = assignExpiryTime(db.getColumnFoodCategory(foodId), false);

                        break;
                    }

                    default:
                }
                db.updateFoodExpirationTime(foodId, value);
                db.updateFoodFinishByDate(foodId, modifiedExpirationDate(db.getDateBought(foodId), value));
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                Toast.makeText(getApplicationContext(), "Confirmed", Toast.LENGTH_SHORT).show();

                // Changing name of Item
                if (name.getText().toString().length() != 0) {
                    db.updateFoodName(foodId, name.getText().toString());
                }

                // Changing category (which updates expiration time, and finish by date)
                if (category.length() != 0 && !(category.equals("Categories"))) {
                    // update category string
                    db.updateFoodCategory(foodId, category);
                    // update expiration time based off of category
                    int value = assignExpiryTime(category, db.getFrozenStatus(foodId));
                    db.updateFoodExpirationTime(foodId, value);
                    // update new expiration date
                    String boughtDate = db.getDateBought(foodId);
                    db.updateFoodFinishByDate(foodId, modifiedExpirationDate(boughtDate, value));
                }

                // Changing store which item was bought
                if (store.getText().toString().length() != 0) {
                    db.updateFoodLocation(foodId, store.getText().toString());


                }
                // Or else null pointer will be thrown
                if (dateBought == null) {
                    return;
                }
                else {
                    if (dateBought.length() != 0) {
                        db.updateFoodBoughtDate(foodId, dateBought);
                        int value = assignExpiryTime(db.getColumnFoodCategory(foodId), db.getFrozenStatus(foodId));
                        db.updateFoodFinishByDate(foodId, modifiedExpirationDate(dateBought, value));

                    }
                }

            }

        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoodModel food = null;
                List<FoodModel> list = db.getFoodList();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getId() == foodId) {
                        food = list.get(i);
                    }
                }
                Intent goBack = new Intent(getApplicationContext(), FoodDetailActivity.class);
                assert food != null;
                goBack.putExtra("CLICKED_FOOD_MODEL", food.getFullInfo());
                startActivity(goBack);
            }
        });

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

    // Method to assign date finished time (basically expiration date)
    public int assignExpiryTime(String category, boolean frozen) {
        int expirationTime = 0;
        switch (category) {
            case "Fruits":
            case "Vegetables":
            case "Dairy":
            case "Grains":
                if (frozen) {
                    expirationTime = 60;
                }
                else {
                    expirationTime = 14;
                }
                break;
            case "Meat &/Or Eggs":
            case "Fish":
                if (frozen) {
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
        return expirationTime;
    }

    public String modifiedExpirationDate(String boughtDate, int expirationTime) {
        String[] time = boughtDate.split("-");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, Integer.parseInt(time[0]));
        c.set(Calendar.MONTH, (Integer.parseInt(time[1]) - 1));
        c.set(Calendar.DAY_OF_MONTH, (Integer.parseInt(time[2])));
        c.add(Calendar.DATE, expirationTime);

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format1.format(c.getTime());
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dateBought = year + "-" + (month + 1) + "-" + dayOfMonth;
        btn_dateBought.setText("Date: " + dateBought);
        btn_dateBought.setBackgroundColor(getResources().getColor(R.color.red_color));

    }

    public void addCategoryNames(List<String> categories) {
        categories.add("Categories");
        categories.add("Fruits");
        categories.add("Vegetables");
        categories.add("Dairy");
        categories.add("Grains");
        categories.add("Meat &/Or Eggs");
        categories.add("Fish");
        categories.add("Other");

    }



}
