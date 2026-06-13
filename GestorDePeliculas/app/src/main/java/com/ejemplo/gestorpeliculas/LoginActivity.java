package com.ejemplo.gestorpeliculas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoogle;
    private TextView tvRegistro;
    private ProgressBar pbCargando;
    private ServicioHTTP servicioHTTP;
    private PreferenciasApp preferencias;
    private GoogleSignInClient googleSignInClient;
    private static final int GOOGLE_SIGN_IN_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar vistas
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        tvRegistro = findViewById(R.id.tvRegistro);
        pbCargando = findViewById(R.id.pbCargando);

        servicioHTTP = new ServicioHTTP(this);
        preferencias = new PreferenciasApp(this);

        // Si ya hay sesión, ir a inicio
        if (preferencias.estaSesionIniciada()) {
            irAInicio();
        }

        // Configurar Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Listeners
        btnLogin.setOnClickListener(v -> iniciarSesion());
        btnGoogle.setOnClickListener(v -> iniciarSesionGoogle());
        tvRegistro.setOnClickListener(v -> mostrarDialogoRegistro());
    }

    private void iniciarSesion() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mostrarCargando(true);

        servicioHTTP.login(email, password, new ServicioHTTP.RespuestaHTTP() {
            @Override
            public void onRespuesta(JSONObject respuesta) {
                mostrarCargando(false);
                try {
                    if (!respuesta.getBoolean("error")) {
                        int idUsuario = respuesta.getInt("id_usuario");
                        String nombre = respuesta.getString("nombre");
                        String emailResp = respuesta.getString("email");
                        String token = respuesta.getString("token");

                        preferencias.guardarSesion(idUsuario, emailResp, nombre, token);
                        Toast.makeText(LoginActivity.this, "Bienvenido " + nombre, Toast.LENGTH_SHORT).show();
                        irAInicio();
                    } else {
                        Toast.makeText(LoginActivity.this, respuesta.getString("error"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Error en la respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                mostrarCargando(false);
                Toast.makeText(LoginActivity.this, "Error de conexión: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void iniciarSesionGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    String googleId = account.getId();
                    String nombre = account.getDisplayName();
                    String email = account.getEmail();

                    mostrarCargando(true);
                    servicioHTTP.loginGoogle(googleId, nombre, email, new ServicioHTTP.RespuestaHTTP() {
                        @Override
                        public void onRespuesta(JSONObject respuesta) {
                            mostrarCargando(false);
                            try {
                                if (!respuesta.getBoolean("error")) {
                                    int idUsuario = respuesta.getInt("id_usuario");
                                    String nombreResp = respuesta.getString("nombre");
                                    String emailResp = respuesta.getString("email");
                                    String token = respuesta.getString("token");

                                    preferencias.guardarSesion(idUsuario, emailResp, nombreResp, token);
                                    Toast.makeText(LoginActivity.this, "Bienvenido " + nombreResp, Toast.LENGTH_SHORT).show();
                                    irAInicio();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(VolleyError error) {
                            mostrarCargando(false);
                            Toast.makeText(LoginActivity.this, "Error en login Google", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Error en Google Sign-In: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void mostrarDialogoRegistro() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialogo_registro, null);

        EditText etEmailReg = view.findViewById(R.id.etEmailRegistro);
        EditText etPasswordReg = view.findViewById(R.id.etPasswordRegistro);
        EditText etNombreReg = view.findViewById(R.id.etNombreRegistro);
        Button btnRegistrarse = view.findViewById(R.id.btnRegistrarse);

        builder.setView(view);
        AlertDialog dialogo = builder.create();

        btnRegistrarse.setOnClickListener(v -> {
            String email = etEmailReg.getText().toString().trim();
            String password = etPasswordReg.getText().toString().trim();
            String nombre = etNombreReg.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || nombre.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(LoginActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            mostrarCargando(true);
            servicioHTTP.registro(email, password, nombre, new ServicioHTTP.RespuestaHTTP() {
                @Override
                public void onRespuesta(JSONObject respuesta) {
                    mostrarCargando(false);
                    try {
                        if (!respuesta.getBoolean("error")) {
                            int idUsuario = respuesta.getInt("id_usuario");
                            String emailResp = respuesta.getString("email");
                            String nombreResp = respuesta.getString("nombre");
                            String token = respuesta.getString("token");

                            preferencias.guardarSesion(idUsuario, emailResp, nombreResp, token);
                            Toast.makeText(LoginActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                            dialogo.dismiss();
                            irAInicio();
                        } else {
                            Toast.makeText(LoginActivity.this, respuesta.getString("error"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    mostrarCargando(false);
                    Toast.makeText(LoginActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialogo.show();
    }

    private void mostrarCargando(boolean mostrar) {
        pbCargando.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!mostrar);
        btnGoogle.setEnabled(!mostrar);
    }

    private void irAInicio() {
        startActivity(new Intent(this, PeliculasActivity.class));
        finish();
    }
}
