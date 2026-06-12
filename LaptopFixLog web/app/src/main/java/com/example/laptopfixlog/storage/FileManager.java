package com.example.laptopfixlog.storage;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

// Importación corregida a la nueva ruta oficial
import com.example.laptopfixlog.model.Repair;

public class FileManager {
    private static final String FILE_NAME = "reparaciones.txt";

    public static void saveRepairs(Context context, ArrayList<Repair> repairs) {
        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            for (Repair r : repairs) {
                fos.write((r.toTxtLine() + "\n").getBytes());
            }
            fos.close();
        } catch (Exception e) {
            Log.e("FileManager", "Error guardando archivo", e);
        }
    }

    public static ArrayList<Repair> loadRepairs(Context context) {
        ArrayList<Repair> list = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(context.openFileInput(FILE_NAME)));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 8) {
                    Repair r = new Repair(
                            parts[0], parts[1], parts[2], parts[3],
                            Double.parseDouble(parts[4]), parts[5], parts[6], parts[7].trim()
                    );
                    list.add(r);
                }
            }
            br.close();
        } catch (Exception e) {
            Log.e("FileManager", "El archivo no existe aún o está vacío", e);
        }
        return list;
    }
}