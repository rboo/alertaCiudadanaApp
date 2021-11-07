package com.core.alertaciudadana.views;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.core.alertaciudadana.R;
import com.core.alertaciudadana.models.user.Usuarios;
import com.core.alertaciudadana.presenters.UserImpl;
import com.core.alertaciudadana.util.MessageResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.List;

public class register extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private UserImpl user;
    EditText et_nombres, et_apellidos, et_email, et_contrasena, et_dni, et_telefono, et_direccion, et_fecha;
    ArrayAdapter<CharSequence> adapter;
    private MaterialSpinner spinner_gender;
    private String genderValue = "Masculino";
    private final static String TAG = register.class.getSimpleName().toString();
    DatePickerDialog datePickerDialog;
    int Year, Month, Day;
    Calendar calendar;
    List<Usuarios> lstUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_register);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        user = new UserImpl(this, mAuth, mDatabase);
        Button btn_register = findViewById(R.id.btn_reg_usuario);
        et_nombres = findViewById(R.id.et_nombres);
        et_apellidos = findViewById(R.id.et_apellidos);
        et_email = findViewById(R.id.et_email);
        et_contrasena = findViewById(R.id.et_contrasena);
        et_dni = findViewById(R.id.et_dni);
        et_telefono = findViewById(R.id.et_telefono);
        et_direccion = findViewById(R.id.et_direccion);
        spinner_gender = findViewById(R.id.genero);
        et_fecha = findViewById(R.id.et_fecha);
        loadGender();

        et_fecha.setInputType(InputType.TYPE_NULL);
        et_fecha.setKeyListener(null);
        btn_register.setOnClickListener(this);
        datePickerLoad();
        lstUsers = user.getUsers();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reg_usuario:
                String dni = et_dni.getText().toString().trim();
                String nombres = et_nombres.getText().toString().trim();
                String apellidos = et_apellidos.getText().toString().trim();
                String email = et_email.getText().toString().trim();
                String constrasena = et_contrasena.getText().toString().trim();
                String telefono = et_telefono.getText().toString().trim();
                String direccion = et_direccion.getText().toString().trim();
                String fecNac = et_fecha.getText().toString().trim();

                for (Usuarios user: lstUsers) {
                    if (dni.compareTo(user.getNumerodocumento()) == 0){
                        Toast.makeText(this, MessageResponse.DNIUSED.getMessageSpanish(),Toast.LENGTH_SHORT).show();
                        return;
                    }else if (telefono.compareTo(user.getTelefono()) == 0){
                        Toast.makeText(this, MessageResponse.TELEFONOUSED.getMessageSpanish(),Toast.LENGTH_SHORT).show();
                        return;
                    }else if (email.compareTo(user.getCorreo()) == 0){
                        Toast.makeText(this, MessageResponse.EMAILUSED.getMessageSpanish(),Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Usuarios dataUsuarios = new Usuarios(
                        apellidos,
                        constrasena,
                        email,
                        direccion,
                        fecNac,
                        "",
                        nombres,
                        dni,
                        genderValue,
                        telefono,
                        "1",
                        "",
                        ""
                );
                user.createAccount(dataUsuarios);
                break;
        }
    }

    public void loadGender() {

        adapter = ArrayAdapter.createFromResource(this, R.array.gender,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner_gender.setAdapter(adapter);
        spinner_gender.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                genderValue = item;
            }
        });
    }

    public void datePickerLoad() {
        calendar = Calendar.getInstance();

        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);

        et_fecha.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                datePickerDialog = DatePickerDialog.newInstance(register.this, Year, Month, Day);

                datePickerDialog.setThemeDark(false);

                datePickerDialog.showYearPickerFirst(false);

                datePickerDialog.setAccentColor(Color.parseColor("#0072BA"));

                datePickerDialog.setTitle("Select Date From DatePickerDialog");

                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
            }
            return false;
        });

        /*et_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datePickerDialog = DatePickerDialog.newInstance(register.this, Year, Month, Day);

                datePickerDialog.setThemeDark(false);

                datePickerDialog.showYearPickerFirst(false);

                datePickerDialog.setAccentColor(Color.parseColor("#0072BA"));

                datePickerDialog.setTitle("Select Date From DatePickerDialog");

                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");

            }
        });*/

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        et_fecha.setText(date);
    }
}