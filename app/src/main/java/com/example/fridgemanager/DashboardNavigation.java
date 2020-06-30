package com.example.fridgemanager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DashboardNavigation extends AppCompatActivity implements View.OnClickListener {

    CardView stats_card, all_card, fruits_card, veg_card, dairy_card, grains_card, meat_card,
    fish_card, other_card;
    FloatingActionButton add_fab, backD_fab;
    TextView total_count, exName1, exName2, exName3, exDate1, exDate2, exDate3;
    Button expiryListBtn;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_navigation);

        // Hooking everything up with id #
        stats_card = findViewById(R.id.stats_card);
        all_card = findViewById(R.id.all_card);
        fruits_card = findViewById(R.id.fruits_card);
        veg_card = findViewById(R.id.veg_card);
        dairy_card = findViewById(R.id.dariy_card);
        grains_card = findViewById(R.id.grains_card);
        meat_card = findViewById(R.id.meat_card);
        fish_card = findViewById(R.id.fish_card);
        other_card = findViewById(R.id.other_card);
        add_fab = findViewById(R.id.fab_add);
        // The D is to indicate that it is the back button in Dashboard Activity
        backD_fab = findViewById(R.id.fab_backDetails);
        total_count = findViewById(R.id.HOME_total);
        exName1 = findViewById(R.id.expiry1Name);
        exName2 = findViewById(R.id.expiry2Name);
        exName3 = findViewById(R.id.expiry3Name);
        exDate1 = findViewById(R.id.expiry1Date);
        exDate2 = findViewById(R.id.expiry2Date);
        exDate3 = findViewById(R.id.expiry3Date);
        expiryListBtn = findViewById(R.id.HOME_expiryBtn);
        // Making a reference to Database Helper
        DatabaseHelper db = new DatabaseHelper(DashboardNavigation.this);
        total_count.setText(Integer.toString(db.getFoodList().size()));


        // Adding click listener to the cards
        stats_card.setOnClickListener(this);
        all_card.setOnClickListener(this);
        fruits_card.setOnClickListener(this);
        veg_card.setOnClickListener(this);
        dairy_card.setOnClickListener(this);
        grains_card.setOnClickListener(this);
        meat_card.setOnClickListener(this);
        fish_card.setOnClickListener(this);
        other_card.setOnClickListener(this);
        add_fab.setOnClickListener(this);
        backD_fab.setOnClickListener(this);

        // Expiry List Card
        setExpiryListPreview(db);
        // If button is pressed, goes to the expiry full list
        expiryListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToNextActivity = new Intent(getApplicationContext(), ExpiryList.class);
                startActivity(goToNextActivity);
            }
        });

    }

    @Override
    public void onClick(View v) {
        Intent goToNextActivity;

        if (v.getId() == R.id.stats_card) {
            goToNextActivity = new Intent(getApplicationContext(), StatisticsActivity.class);
            startActivity(goToNextActivity);
        }
        else if (v.getId() == R.id.fab_add) {
            goToNextActivity = new Intent(getApplicationContext(), AddItemActivity.class);
            startActivity(goToNextActivity);
        }
        else if (v.getId() == R.id.fab_backDetails) {
            goToNextActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(goToNextActivity);
        }
        else {
            String infoSent = null;
            // Making a switch statement to see what String to send in Bundle
            switch (v.getId()) {
                case R.id.all_card:
                    infoSent = "All Items";
                    break;
                case R.id.fruits_card:
                    infoSent = "Fruits";
                    break;
                case R.id.veg_card:
                    infoSent = "Vegetables";
                    break;
                case R.id.dariy_card:
                    infoSent = "Dairy";
                    break;
                case R.id.grains_card:
                    infoSent = "Grain";
                    break;
                case R.id.meat_card:
                    infoSent = "Meat &/Or Eggs";
                    break;
                case R.id.fish_card:
                    infoSent = "Fish";
                    break;
                case R.id.other_card:
                    infoSent = "Other";
                    break;

            }
            goToNextActivity = new Intent(getApplicationContext(), FoodListActivity.class);
            // Sending to the next activity which category was chosen
            goToNextActivity.putExtra("CLICKED_CATEGORY", infoSent);
            startActivity(goToNextActivity);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setExpiryListPreview(DatabaseHelper db) {
        List<FoodModel> expiryList = db.getExpiryList();
        // if there is no items
        if (expiryList.size() == 0) {
            // Make Name 2 only visible
            exName1.setVisibility(View.GONE);
            exDate1.setVisibility(View.GONE);
            exDate2.setVisibility(View.GONE);
            exName3.setVisibility(View.GONE);
            exDate3.setVisibility(View.GONE);
            expiryListBtn.setVisibility(View.GONE);
        } else if (expiryList.size() == 1) {
            exName1.setText(expiryList.get(0).getFoodName());
            if (daysLeft(expiryList.get(0).getFinishByDate()) < 0) {
                exDate1.setText("EXPIRED ALREADY");
            } else {
                exDate1.setText(daysLeft(expiryList.get(0).getFinishByDate()) + " Days Left");
            }
            // set others not visible
            exName2.setVisibility(View.GONE);
            exDate2.setVisibility(View.GONE);
            exName3.setVisibility(View.GONE);
            exDate3.setVisibility(View.GONE);
        } else if (expiryList.size() == 2) {
            exName1.setText(expiryList.get(0).getFoodName());
            if (daysLeft(expiryList.get(0).getFinishByDate()) < 0) {
                exDate1.setText("EXPIRED ALREADY");
            } else {
                exDate1.setText(daysLeft(expiryList.get(0).getFinishByDate()) + " Days Left");
            }
            exName2.setText(expiryList.get(1).getFoodName());
            if (daysLeft(expiryList.get(1).getFinishByDate()) < 0) {
                exDate2.setText("EXPIRED ALREADY");
            } else {
                exDate2.setText(daysLeft(expiryList.get(1).getFinishByDate()) + " Days Left");
            }
            // set others not visible
            exName3.setVisibility(View.GONE);
            exDate3.setVisibility(View.GONE);
        } else {
            exName1.setText(expiryList.get(0).getFoodName());
            if (daysLeft(expiryList.get(0).getFinishByDate()) < 0) {
                exDate1.setText("EXPIRED ALREADY");
            } else {
                exDate1.setText(daysLeft(expiryList.get(0).getFinishByDate()) + " Days Left");
            }
            exName2.setText(expiryList.get(1).getFoodName());
            if (daysLeft(expiryList.get(1).getFinishByDate()) < 0) {
                exDate2.setText("EXPIRED ALREADY");
            } else {
                exDate2.setText(daysLeft(expiryList.get(1).getFinishByDate()) + " Days Left");
            }
            exName3.setText(expiryList.get(2).getFoodName());
            if (daysLeft(expiryList.get(2).getFinishByDate()) < 0) {
                exDate3.setText("EXPIRED ALREADY");
            } else {
                exDate3.setText(daysLeft(expiryList.get(2).getFinishByDate()) + " Days Left");
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public long daysLeft(String date) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayString = format1.format(cal.getTime());

        long diff = 0;
        try {
            Date givenDate = format1.parse(date);
            Date today = format1.parse(todayString);
            diff = givenDate.getTime() - today.getTime();
            System.out.println ("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

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
