package com.example.laptopfixlog.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

import com.example.laptopfixlog.R;
import com.example.laptopfixlog.model.Repair;
import com.example.laptopfixlog.storage.FileManager;

public class EditRepairActivity extends AppCompatActivity {

    private EditText etClient, etModel, etCost, etDate, etComment;
    private Spinner spinnerType, spinnerStatus;
    private Button btnUpdate, btnDelete;
    private Repair currentRepair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_repair);
        setTitle(R.string.edit_repair);

        etClient = findViewById(R.id.etClientEdit);
        etModel = findViewById(R.id.etModelEdit);
        etCost = findViewById(R.id.etCostEdit);
        etDate = findViewById(R.id.etDateEdit);
        etComment = findViewById(R.id.etCommentEdit);
        spinnerType = findViewById(R.id.spinnerTypeEdit);
        spinnerStatus = findViewById(R.id.spinnerStatusEdit);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        currentRepair = (Repair) getIntent().getSerializableExtra("repair_obj");
        if (currentRepair != null) {
            loadData();
        }

        btnUpdate.setOnClickListener(v -> updateRepair());
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void loadData() {
        etClient.setText(currentRepair.getNombreCliente());
        etModel.setText(currentRepair.getModeloLaptop());
        etCost.setText(String.valueOf(currentRepair.getCosto()));
        etDate.setText(currentRepair.getFecha());
        etComment.setText(currentRepair.getComentario());

        setSpinnerSelection(spinnerType, currentRepair.getTipoReparacion());
        setSpinnerSelection(spinnerStatus, currentRepair.getEstado());
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void updateRepair() {
        String client = etClient.getText().toString().trim();
        String model = etModel.getText().toString().trim();
        String costStr = etCost.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String comment = etComment.getText().toString().trim();

        if (client.isEmpty() || model.isEmpty() || costStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Campos obligatorios vacíos", Toast.LENGTH_SHORT).show();
            return;
        }

        currentRepair.setNombreCliente(client);
        currentRepair.setModeloLaptop(model);
        currentRepair.setTipoReparacion(spinnerType.getSelectedItem().toString());
        currentRepair.setCosto(Double.parseDouble(costStr));
        currentRepair.setFecha(date);
        currentRepair.setEstado(spinnerStatus.getSelectedItem().toString());
        currentRepair.setComentario(comment);

        ArrayList<Repair> list = FileManager.loadRepairs(this);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(currentRepair.getId())) {
                list.set(i, currentRepair);
                break;
            }
        }
        FileManager.saveRepairs(this, list);

        Toast.makeText(this, "Registro actualizado", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage(R.string.delete_confirm)
                .setPositiveButton(R.string.yes, (dialog, which) -> deleteRepair())
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void deleteRepair() {
        ArrayList<Repair> list = FileManager.loadRepairs(this);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(currentRepair.getId())) {
                list.remove(i);
                break;
            }
        }
        FileManager.saveRepairs(this, list);
        Toast.makeText(this, "Registro eliminado", Toast.LENGTH_SHORT).show();
        finish();
    }
}