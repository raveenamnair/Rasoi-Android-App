package com.example.fridgemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * This class is in charge of displaying the details of a specific food item the user decideds to
 * click on.
 */

public class FoodDetailActivity extends AppCompatActivity {

    int foodID;
    String foodCategory, foodLocation, foodName, expirationTime, dateFinished, tempDate, value;
    boolean frozenStatus;
    FoodModel foodClicked;
    String bundleValue;

    TextView tvFoodName, tvfoodCategory, tvFoodLocation, tvDate, tv_expiresDate, tv_FinishDate,
            tvFrozenStatus;
    Button btn_delete;
    FloatingActionButton back, edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        tvFoodName = findViewById(R.id.tv_foodName);
        tvfoodCategory = findViewById(R.id.tv_foodCategory);
        tvFoodLocation = findViewById(R.id.tv_foodLocation);
        tvDate = findViewById(R.id.tv_foodBoughtDate);
        tv_expiresDate = findViewById(R.id.tv_foodExpiryTime);
        tv_FinishDate = findViewById(R.id.tv_foodFinishDate);
        tvFrozenStatus = findViewById(R.id.tv_frozenStatus);
        btn_delete = findViewById(R.id.btn_remove);
        back = findViewById(R.id.fab_backDetails);
        edit = findViewById(R.id.fab_edit);

        Bundle extras = getIntent().getExtras();
        value = "";
        if (extras != null) {
            value = extras.getString("CLICKED_FOOD_MODEL");
            //Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
            bundleValue = extras.getString("CLICKED_CATEGORY");
        }

        parseFoodModelString(value);

        tvFoodName.setText(foodName);
        tvfoodCategory.setText(foodCategory);
        tvFoodLocation.setText(foodLocation);
        tv_expiresDate.setText(expirationTime + " Days");
        tv_FinishDate.setText(dateFinished);
        if (frozenStatus)
            tvFrozenStatus.setText("Frozen");
        else
            tvFrozenStatus.setText("Not Frozen");

        tvDate.setText(tempDate);
        foodClicked = new FoodModel(foodID, foodName, foodCategory, foodLocation, frozenStatus);

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
                foodClicked = new FoodModel(foodID, foodName, foodCategory, foodLocation, frozenStatus);
                boolean status = databaseHelper.removeFoodItem(foodClicked);
                if (status) {
                    Toast.makeText(getApplicationContext(), "Successfully deleted",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error while removing",
                            Toast.LENGTH_SHORT).show();
                }
                Intent goBackToListView = new Intent(getApplicationContext(), DashboardNavigation.class);
                startActivity(goBackToListView);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goBack  = new Intent(getApplicationContext(), FoodListActivity.class);
                System.out.println(value);
                if (bundleValue == null || !(bundleValue.equals("All Items"))) {
                    goBack.putExtra("CLICKED_CATEGORY", foodClicked.getCategory());
                    startActivity(goBack);
                } else {
                    goBack.putExtra("CLICKED_CATEGORY", "All Items");
                   startActivity(goBack);
                }
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editActivity = new Intent(getApplicationContext(), EditFoodItem.class);
                editActivity.putExtra("Food ID", foodClicked.getId());
                startActivity(editActivity);
            }
        });

    }

    public void parseFoodModelString(String value) {
        String[] foodModelArray = value.split(", ", 15);
        foodID = Integer.parseInt(foodModelArray[0]);
        foodCategory = foodModelArray[1];
        foodLocation = foodModelArray[2];
        foodName = foodModelArray[3];
        tempDate = foodModelArray[4];
        expirationTime = foodModelArray[5];
        dateFinished = foodModelArray[6];
        String status = foodModelArray[7];
        frozenStatus = status.equals("true");
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
