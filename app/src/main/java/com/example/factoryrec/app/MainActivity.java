package com.example.factoryrec.app;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.factoryrec.R;


public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button page_dynamic = (Button)findViewById(R.id.page_display);
        Button page_home = (Button)findViewById(R.id.page_home);
        Button page_info = (Button)findViewById(R.id.page_om);
        Button page_zoning = (Button)findViewById(R.id.page_signal);

        page_dynamic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                Fragment_Display fragment_dynamic = new Fragment_Display();
                transaction.add(R.id.fragment_container, fragment_dynamic);
                transaction.commit();
            }
        });

        page_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                Fragment_Home fragment_home = new Fragment_Home();
                transaction.add(R.id.fragment_container, fragment_home);
                transaction.commit();
            }
        });

        page_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                Fragment_OM Fragment_OM = new Fragment_OM();
                transaction.add(R.id.fragment_container, Fragment_OM);
                transaction.commit();
            }
        });

        page_zoning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                Fragment_Signal Fragment_Signal = new Fragment_Signal();
                transaction.add(R.id.fragment_container, Fragment_Signal);
                transaction.commit();
            }
        });


    }
}
