package com.example.laptopfixlog.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

import com.example.laptopfixlog.R;
import com.example.laptopfixlog.adapter.RepairAdapter;
import com.example.laptopfixlog.model.Repair;
import com.example.laptopfixlog.storage.FileManager;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RepairAdapter adapter;
    private ArrayList<Repair> repairList;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        tvEmpty = findViewById(R.id.tvEmpty);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddRepairActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        repairList = FileManager.loadRepairs(this);

        if (repairList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        adapter = new RepairAdapter(repairList, (repair, position) -> {
            Intent intent = new Intent(MainActivity.this, EditRepairActivity.class);
            intent.putExtra("repair_obj", repair);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }
}