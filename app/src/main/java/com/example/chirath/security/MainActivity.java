package com.example.chirath.security;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.setNumbers);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView t = (TextView) findViewById(R.id.textView);
                t.setText("Enter numbers");
                Intent intent = new Intent(Intent.ACTION_CALL);
                EditText number1 = (EditText) findViewById(R.id.phone1);
                if(number1.getText().length() == 10) {
                    intent.setData(Uri.parse("tel:" + number1.getText()));
                    if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(intent);
                    t.setText("Sucessfull");
                    t.setTextColor(Color.GREEN);
                    t.setTextSize(20);
                }
                else {
                    TextView a = (TextView) findViewById(R.id.textView);
                    a.setText("Check the phone number!!!");
                    a.setTextColor(Color.RED);
                    a.setTextSize(20);
                }
            }
        }
        );
    }

}
