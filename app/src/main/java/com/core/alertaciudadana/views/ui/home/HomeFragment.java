package com.core.alertaciudadana.views.ui.home;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.core.alertaciudadana.R;
import com.core.alertaciudadana.databinding.FragmentHomeBinding;
import com.core.alertaciudadana.models.incidente.Incidente;
import com.core.alertaciudadana.models.user.Usuarios;
import com.core.alertaciudadana.presenters.IncidenteImpl;
import com.core.alertaciudadana.presenters.UserImpl;
import com.core.alertaciudadana.util.DateUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class HomeFragment extends Fragment implements View.OnClickListener {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private CardView cv_robo, cv_sospechoso, cv_violencia, cv_otros;
    private Context miContexto = null;
    private IncidenteImpl incidente;
    private UserImpl user;
    private FusedLocationProviderClient fusedLocationClient;
    private final static int REQUEST_LOCATION_PERMISSION = 1;
    private final static int REQUEST_PHONE_PERMISSION = 1;
    private final static String TAG = HomeFragment.class.getSimpleName().toString();
    private double latitude;
    private double longitude;
    String mPhoneNumber;
    private TelephonyManager tMgr;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    Usuarios usuarios;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        //binding = FragmentHomeBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        miContexto = container.getContext();
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        //View root = binding.getRoot();

        cv_robo = v.findViewById(R.id.robo);
        cv_sospechoso = v.findViewById(R.id.sospechoso);
        cv_violencia = v.findViewById(R.id.violencia);
        cv_otros = v.findViewById(R.id.otros);

        cv_robo.setOnClickListener(this);
        cv_sospechoso.setOnClickListener(this);
        cv_violencia.setOnClickListener(this);
        cv_otros.setOnClickListener(this);

        incidente = new IncidenteImpl(miContexto, mAuth, mDatabase);
        user = new UserImpl(miContexto,mAuth,mDatabase);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(miContexto);
        getLocation();
        tMgr = (TelephonyManager) miContexto.getSystemService(Context.TELEPHONY_SERVICE);


        /*homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        */
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.robo:
                //getPhoneNumber();
                ProgressDialog progressDialog = new ProgressDialog(getActivity());

                progressDialog.setMessage("Enviando Notificacion...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                String key = mDatabase.push().getKey();

                incidente.registrarIncidente(new Incidente(
                        "Nuevo incidente generado",
                        DateUtil.FechaCorta(),
                        DateUtil.HoraActual(),
                        "imagen",
                        latitude,
                        longitude,
                        "Asalto o Robo",
                        key,
                        getCurrentUser(),
                        0
                ));
                progressDialog.dismiss();

                break;
            case R.id.sospechoso:
                break;
            case R.id.violencia:
                break;
            case R.id.otros:
                break;

        }
    }

    private void getPhoneNumber() {
        if (ActivityCompat.checkSelfPermission(miContexto,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                            {Manifest.permission.READ_PHONE_STATE},
                    REQUEST_PHONE_PERMISSION);
        } else {
            Log.d(TAG, "getNumber: permissions granted");
            mPhoneNumber = tMgr.getLine1Number();
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(miContexto,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            Log.d(TAG, "getLocation: permissions granted");
            fusedLocationClient.getLastLocation().addOnSuccessListener(
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Log.d(TAG, "Latitud: " + location.getLatitude() + " - Longitud: " + location.getLongitude());
                                // Start the reverse geocode AsyncTask
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                Log.d(TAG, "getLocation is not null");
                                /*Toast.makeText(
                                        miContexto,
                                        "Latitud " + location.getLatitude() + " - Longitud" + location.getLongitude(),
                                        Toast.LENGTH_SHORT)
                                        .show();*/
                            }
                        }
                    });
        }
    }

    private String getCurrentUser(){
        return mAuth.getCurrentUser().getUid();
        //Log.d(TAG,"User -> "+mAuth.getCurrentUser().getEmail()+ "-"+mAuth.getCurrentUser().getUid());
        //user.getUserData(mAuth.getCurrentUser().getUid());
        //System.out.println("correo "+user.getUsuarios().getCorreo());
    }

}