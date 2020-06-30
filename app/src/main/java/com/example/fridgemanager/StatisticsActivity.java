package com.example.fridgemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    ArrayList<Float> yData = new ArrayList<>();
    private ArrayList<String> xData = new ArrayList<>();
    DatabaseHelper databaseHelper;
    List<FoodModel> foodList;
    String label;

    PieChart pieChart;
    RadioGroup radiogroup;
    RadioButton frozen_stats;
    RadioButton categories_stats;
    TextView totalCount;
    FloatingActionButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        pieChart = findViewById(R.id.pieChart);
        radiogroup = findViewById(R.id.radiogroup);
        frozen_stats = findViewById(R.id.frozen_radio);
        categories_stats = findViewById(R.id.cat_radio);
        totalCount = findViewById(R.id.totalCount);
        back = findViewById(R.id.fab_backStats);

        databaseHelper = new DatabaseHelper(getApplicationContext());
        foodList = databaseHelper.getFoodList();

        // TODO Change the default pie graph value
        label = displayFrozenStats(foodList);
        displayPieChart();
        radiogroup.setOnCheckedChangeListener(this);

        // Setting the total count TextView
        totalCount.setText(Integer.toString(foodList.size()));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goBack = new Intent(getApplicationContext(), DashboardNavigation.class);
                startActivity(goBack);
            }
        });



    }

    public String displayFrozenStats(List<FoodModel> foods) {
        String tag = "Frozen Status";
        // This means the xData & yData would only have 2 values
        // 1st data entry is for frozen items
        // 2nd data entry is for non-frozen items
        boolean status;
        float numFrozen = 0f;
        float numNonFrozen = 0f;
        for (int i = 0; i < foods.size(); i++) {
            status = foods.get(i).isFrozen();
            if (status) {
                numFrozen++;
            }
            else {
                numNonFrozen++;
            }
        }
        yData.add(numFrozen);
        yData.add(numNonFrozen);
        xData.add("Frozen");
        xData.add("Not Frozen");

        return tag;
    }


    public String displayCategoryStats(List<FoodModel> foods) {
        String tag = "Categories";
        // xData is Names of Categories
        // yData is Number of things in each category

        // Add all categories to xDataCopy
        ArrayList<String> xDataCopy = new ArrayList<>();
        xDataCopy.add("Fruits");
        xDataCopy.add("Vegetables");
        xDataCopy.add("Dairy");
        xDataCopy.add("Grains");
        xDataCopy.add("Meat &/Or Eggs");
        xDataCopy.add("Fish");
        xDataCopy.add("Other");

        float foodNum[] = new float[7];
        // Second time go through list to add the number of things in each category
        for (int i = 0; i < foods.size(); i++) {
            for (int j = 0; j < xDataCopy.size(); j++) {
                if (xDataCopy.get(j).equals(foods.get(i).getCategory())) {
                    foodNum[j]++;
                }
            }
        }

        // Removing those with 0 as a count (disrupts pie chart)
        for (int i = 0; i < xDataCopy.size(); i++) {
            if (foodNum[i] != 0.0) {
                xData.add(xDataCopy.get(i));
                yData.add(foodNum[i]);
            }
        }


        return tag;
    }


    public void displayPieChart() {

        Description description = new Description();
        description.setText("Food Statistics");
        pieChart.setDescription(description);

        pieChart.setRotationEnabled(true);  // lets you spin it
        pieChart.setHoleRadius(30f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterText("Click to Activate Changes");
        pieChart.setCenterTextSize(10);

        List<PieEntry> value = new ArrayList<>();
        for (int i = 0; i < yData.size(); i++) {
            value.add(new PieEntry(yData.get(i), xData.get(i)));
        }

        PieDataSet pieDataSet = new PieDataSet(value, label);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);

        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        xData.clear();
        yData.clear();

        // Check which radio button was clicked
        switch(checkedId) {
            case R.id.frozen_radio: {
                    label = displayFrozenStats(foodList);
                }
                break;
            case R.id.cat_radio: {
                    label = displayCategoryStats(foodList);
                }
                break;
        }
        pieChart.notifyDataSetChanged();
        displayPieChart();
        pieChart.notifyDataSetChanged();

    }

    // This method will return the 3 most visited stores
    public ArrayList<String> findTop3Stores(List<FoodModel> foods) {
        ArrayList<String> store = new ArrayList<>();
        store.add(foods.get(0).getLocation());
        for (int i = 1; i < foods.size(); i++) {
            for (int j = 0; j < store.size(); j++) {
                if (!store.get(j).equals(foods.get(i).getLocation())) {
                    store.add(foods.get(i).getLocation());
                }
            }
        }

        // Now finding how much of each is in it
        int storeCount[] = new int[store.size()];
        for (int i = 0; i < store.size(); i++) {
            for (int j = 0; j < foods.size(); j++) {
                if (foods.get(j).getLocation().equals(store.get(i))) {
                    storeCount[i]++;
                }
            }
        }

        ArrayList<String> count = new ArrayList<>();
        for (int i = 0; i < storeCount.length; i++) {
            String element = store.get(i) + " Count: " + storeCount[i];
            count.add(element);
        }

        return count;
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
