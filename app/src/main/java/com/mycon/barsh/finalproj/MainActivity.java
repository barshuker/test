package com.mycon.barsh.finalproj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

// lisa

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Buttons to launch first & second Activity
        Button firstActivityBtn = findViewById(R.id.FirstActivityBtn);
        firstActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(),FirstActivity.class);
                startIntent.putExtra("LaunchSource","Main"); // data that the launched activity receives
                startActivity(startIntent);
            }
        });

        Button secondActivityBtn = (Button)findViewById(R.id.SecondActivityBtn);
        secondActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(),SecondActivity.class);
                startIntent.putExtra("LaunchSource","Main"); // doesn't seem to be used, verify and delete
                startActivity(startIntent);
            }
        });

    }

}
