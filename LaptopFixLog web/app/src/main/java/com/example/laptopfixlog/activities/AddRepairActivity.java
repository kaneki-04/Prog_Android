package com.example.laptopfixlog.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.example.laptopfixlog.R;
import com.example.laptopfixlog.model.Repair;
import com.example.laptopfixlog.storage.FileManager;

public class AddRepairActivity extends AppCompatActivity {

    private EditText etClient, etModel, etCost, etDate, etComment;
    private Spinner spinnerType, spinnerStatus;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_repair);
        setTitle(R.string.add_repair);

        etClient = findViewById(R.id.etClient);
        etModel = findViewById(R.id.etModel);
        etCost = findViewById(R.id.etCost);
        etDate = findViewById(R.id.etDate);
        etComment = findViewById(R.id.etComment);
        spinnerType = findViewById(R.id.spinnerType);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnSave = findViewById(R.id.btnSave);

        String today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        etDate.setText(today);

        btnSave.setOnClickListener(v -> saveRepair());
    }

    private void saveRepair() {
        String client = etClient.getText().toString().trim();
        String model = etModel.getText().toString().trim();
        String costStr = etCost.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String comment = etComment.getText().toString().trim();
        String type = spinnerType.getSelectedItem().toString();
        String status = spinnerStatus.getSelectedItem().toString();

        if (client.isEmpty() || model.isEmpty() || costStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Por favor llena los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        double cost = Double.parseDouble(costStr);
        String id = String.valueOf(System.currentTimeMillis());

        Repair newRepair = new Repair(id, client, model, type, cost, date, status, comment);

        ArrayList<Repair> list = FileManager.loadRepairs(this);
        list.add(newRepair);
        FileManager.saveRepairs(this, list);

        Toast.makeText(this, "Reparación guardada con éxito", Toast.LENGTH_SHORT).show();
        finish();
    }
}