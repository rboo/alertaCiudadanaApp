package com.core.alertaciudadana.views.ui.incidents;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.core.alertaciudadana.R;
import com.core.alertaciudadana.models.incidente.Incidente;
import com.core.alertaciudadana.models.user.Usuarios;
import com.core.alertaciudadana.presenters.IncidenteImpl;
import com.core.alertaciudadana.presenters.UserImpl;
import com.core.alertaciudadana.util.DateUtil;
import com.core.alertaciudadana.util.LocationTrack;
import com.core.alertaciudadana.views.MenuDrawer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class IncidentFragment extends Fragment implements View.OnClickListener {

    private CardView cv_robo, cv_sospechoso, cv_violencia, cv_otros;
    private Context miContexto = null;
    private IncidenteImpl incidente;
    private UserImpl user;
    private FusedLocationProviderClient fusedLocationClient;
    private final static int REQUEST_LOCATION_PERMISSION = 1;
    private final static int REQUEST_PHONE_PERMISSION = 1;
    private final static String TAG = IncidentFragment.class.getSimpleName().toString();
    private double latitude;
    private double longitude;
    String mPhoneNumber;
    private TelephonyManager tMgr;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    Usuarios usuarios;
    LocationManager mloLocationManager;
    LocationTrack locationTrack;

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
        user = new UserImpl(miContexto, mAuth, mDatabase);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(miContexto);
        getLocation();
        tMgr = (TelephonyManager) miContexto.getSystemService(Context.TELEPHONY_SERVICE);

        mloLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

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
        locationTrack.stopListener();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.robo:
                if (!mloLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    displayPromptForEnablingGPS(getActivity());
                } else {
                    createNotification("Asalto o Robo");
                }
                break;
            case R.id.sospechoso:
                if (!mloLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    displayPromptForEnablingGPS(getActivity());
                } else {
                    createNotification("Accidente de Tránsito");
                }
                break;
            case R.id.violencia:
                if (!mloLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    displayPromptForEnablingGPS(getActivity());
                } else {
                    createNotification("Nuevo Caso de Violencia");
                }
                break;
            case R.id.otros:
                Intent intent = new Intent(miContexto, otherIncident.class);
                startActivity(intent);
                break;

        }
    }

    private void createNotification(String titulo) {
        String key = mDatabase.push().getKey();

        incidente.registrarIncidente(new Incidente(
                "Nuevo incidente generado",
                DateUtil.FechaCorta(),
                DateUtil.HoraActual(),
                "imagen",
                latitude,
                longitude,
                titulo,
                key,
                getCurrentUser()
        ));
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
                                Log.d(TAG, "show last Location Latitude: "+latitude+ " Longitude: "+longitude);
                                //Toast.makeText(miContexto, "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
                            }else{
                                getLastKnowLocation();
                            }
                        }
                    });
        }
    }

    private void getLastKnowLocation() {
        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(miContexto, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(miContexto, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = mloLocationManager.getLastKnownLocation(mloLocationManager.getBestProvider(criteria, false));
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d(TAG, "show last know location Latitude: "+latitude+" Longitude: "+longitude);
        }
    }

    private String getCurrentUser() {
        return mAuth.getCurrentUser().getUid();
        //Log.d(TAG,"User -> "+mAuth.getCurrentUser().getEmail()+ "-"+mAuth.getCurrentUser().getUid());
        //user.getUserData(mAuth.getCurrentUser().getUid());
        //System.out.println("correo "+user.getUsuarios().getCorreo());
    }

    public void displayPromptForEnablingGPS(Activity activity) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = getString(R.string.msg_settings_gps);

        builder.setMessage(message)
                .setPositiveButton("ACEPTAR",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("NO, GRACIAS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }





    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) { //&& resultCode == RESULT_OK) {
            Log.i("TAG", "onActivityResult: resultCode" + resultCode);
            Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            //Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            iv_foto_incidente.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv_foto_incidente.setImageBitmap(takenImage);
        }
    }*/

}