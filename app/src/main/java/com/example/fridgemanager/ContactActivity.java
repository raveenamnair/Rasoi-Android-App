package com.example.fridgemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Contact class stores the name, email, subject and message the user inputs and
 * sends an email to Rasoi
 */

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        final EditText your_name = findViewById(R.id.your_name);
        final EditText your_email = findViewById(R.id.your_email);
        final EditText your_subject = findViewById(R.id.your_subject);
        final EditText your_message = findViewById(R.id.your_message);
        Button submitButton = (Button) findViewById(R.id.submit);



        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = your_name.getText().toString();
                String email = your_email.getText().toString();
                String subject = your_subject.getText().toString();
                String message = your_message.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    your_name.setError("Enter Your Name");
                    your_name.requestFocus();
                    return;
                }

                if (!isEmailValid(email)) {
                    your_name.setError("Enter A Valid Email");
                    your_name.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(subject)) {
                    your_subject.setError("Enter your subject");
                    your_subject.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(message)) {
                    your_message.setError("Enter your message");
                    your_message.requestFocus();
                    return;
                }

                Intent sendEmail = new Intent(Intent.ACTION_SEND);

                // Fill Data
                sendEmail.setType("plain/text");
                sendEmail.putExtra(Intent.EXTRA_EMAIL, new String[] {"raveenamadhu@gmail.com"});
                sendEmail.putExtra(Intent.EXTRA_SUBJECT, subject);
                sendEmail.putExtra(Intent.EXTRA_TEXT,
                        "name: " + name +'\n' + "Email ID: " + email+'\n' + "Message: " + message);

                startActivity(Intent.createChooser(sendEmail, "Send mail..."));


            }
        });

        FloatingActionButton home = findViewById(R.id.fab_Home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goHome = new Intent(getApplicationContext(), DashboardNavigation.class);
                startActivity(goHome);
            }
        });

    }

    public boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
