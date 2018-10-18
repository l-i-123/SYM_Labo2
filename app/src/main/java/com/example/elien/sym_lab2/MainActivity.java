package com.example.elien.sym_lab2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button mClickButton1 = findViewById(R.id.button1);
        Button mClickButton2 = findViewById(R.id.button2);
        Button mClickButton3 = findViewById(R.id.button3);
        Button mClickButton4 = findViewById(R.id.button4);
        Button mClickButton5 = findViewById(R.id.button5);

       mClickButton1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this, Activity1.class);
               //intent.putExtra("mail", mail);
               startActivity(intent);
           }
       });

        mClickButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Activity2.class);
                //intent.putExtra("mail", mail);
                startActivity(intent);
            }
        });

        mClickButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Activity3.class);
                //intent.putExtra("mail", mail);
                startActivity(intent);
            }
        });

        mClickButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Activity4.class);
                //intent.putExtra("mail", mail);
                startActivity(intent);
            }
        });

        mClickButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Activity5.class);
                //intent.putExtra("mail", mail);
                startActivity(intent);
            }
        });
    }
}
