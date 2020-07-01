package com.raveena.fridgemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * This class displays all the items in either a specific category the users chooses,
 * or in a general format, depending on what they clicked on previously in the DashboardNavigation
 */

public class FoodListActivity extends AppCompatActivity {

    ListView lv_foodList;
    ArrayAdapter arrayAdapter;
    DatabaseHelper databaseHelper;
    String categoryPicked;
    TextView header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        header = findViewById(R.id.tv_foodListMessage);
        lv_foodList = findViewById(R.id.lv_foodList);
        databaseHelper = new DatabaseHelper(FoodListActivity.this);


        Bundle extras = getIntent().getExtras();
        String value = null;
        if (extras != null) {
            value = extras.getString("CLICKED_CATEGORY");
        }
        categoryPicked = value;

        if (value.equals("All Items")) {
            showAllFoodItemsOnListView(databaseHelper);
        }
        else {
            showSpecial(value, databaseHelper);
        }

        lv_foodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent goToNextActivity = new Intent(getApplicationContext(), FoodDetailActivity.class);
                FoodModel foodModelClicked = (FoodModel) parent.getItemAtPosition(position);
                String infoSent = foodModelClicked.getFullInfo();
                goToNextActivity.putExtra("CLICKED_FOOD_MODEL", infoSent);
                goToNextActivity.putExtra("CLICKED_CATEGORY", categoryPicked);
                startActivity(goToNextActivity);
            }
        });

        FloatingActionButton back = findViewById(R.id.fab_backList);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goBack = new Intent(getApplicationContext(), DashboardNavigation.class);
                startActivity(goBack);
            }
        });

        FloatingActionButton add = findViewById(R.id.fab_addList);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addItem = new Intent(getApplicationContext(), AddItemActivity.class);
                startActivity(addItem);
            }
        });



    }

    // Method to show list
    private void showAllFoodItemsOnListView(DatabaseHelper databaseHelper2) {
        List<FoodModel> allList = databaseHelper2.getFoodList();
        if (allList.size() == 0) {
            header.setText("No items");
        }
        arrayAdapter = new ArrayAdapter<FoodModel>
                (FoodListActivity.this, android.R.layout.simple_list_item_1,
                        databaseHelper2.getFoodList());
        lv_foodList.setAdapter(arrayAdapter);

    }

    private void showSpecial(String category, DatabaseHelper databaseHelper) {
        List<FoodModel> allList = databaseHelper.getFoodList(); // This gets the whole list
        List<FoodModel> specialList = new ArrayList<>();
        for (int i = 0; i < allList.size(); i++) {
            if (allList.get(i).getCategory().equals(category)) {
                specialList.add(allList.get(i));
            }
        }
        if (specialList.size() == 0) {
            header.setText("No items");
        }
        arrayAdapter = new ArrayAdapter<FoodModel>(FoodListActivity.this,
                android.R.layout.simple_list_item_1, specialList);
        lv_foodList.setAdapter(arrayAdapter);
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
