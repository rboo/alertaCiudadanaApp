package com.core.alertaciudadana.views.ui.incidents;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.core.alertaciudadana.R;
import com.core.alertaciudadana.models.incidente.Incidente;
import com.core.alertaciudadana.presenters.IncidenteImpl;
import com.core.alertaciudadana.util.DateUtil;
import com.core.alertaciudadana.util.NetworkDetector;
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

public class otherIncident extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = otherIncident.class.getSimpleName().toString();
    File photoFile = null;
    String currentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private FirebaseStorage storage;
    private StorageReference mStorageRef;
    private ImageView iv_incidente;
    private EditText et_titulo_incidente, et_descripcion_incidente;
    private IncidenteImpl incidente;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationManager mloLocationManager;
    private final static int REQUEST_LOCATION_PERMISSION = 1;
    private double latitude;
    private double longitude;
    private Button btn_reg_incidente;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_other_incident);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        incidente = new IncidenteImpl(this, mAuth, mDatabase);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mloLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        iv_incidente = findViewById(R.id.iv_foto_incidente);
        et_titulo_incidente = findViewById(R.id.et_titulo_incidente);
        et_descripcion_incidente = findViewById(R.id.et_descripcion_incidente);
        btn_reg_incidente = findViewById(R.id.btn_reg_incidente);

        btn_reg_incidente.setOnClickListener(this);
        iv_incidente.setOnClickListener(this);
        getLocation();
        progressDialog = new ProgressDialog(this);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error al crear la imagen", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                //startActivityForResult(takePictureIntent, CAMERA_RESO);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "incidente_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void uploadFile() {
        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReferenceFromUrl("gs://alertaciudadana-50dd3.appspot.com");
        if (photoFile != null) {
            StorageReference ImageRef = mStorageRef.child(photoFile.getName());

            Uri path = Uri.fromFile(photoFile.getAbsoluteFile());
            UploadTask uploadTask = ImageRef.putFile(path);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressDialog.dismiss();
                    Toast.makeText(otherIncident.this,"Error al subir imagen",Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(taskSnapshot -> {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                //Toast.makeText(otherIncident.this,"se subio correctamente",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                showDialog("Se envio el incidente satisfactoriamente");
                //finish();
            });

        }
    }

    private void dispatchTakePictureIntent2() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void validateData() {
        NetworkDetector cd = new NetworkDetector(getApplicationContext());

        boolean isInternetPresent = cd.isNetworkPresent();
        String titulo = et_titulo_incidente.getText().toString();
        String descripcion = et_descripcion_incidente.getText().toString();
        if (!isInternetPresent) {
            //Snackbar.make(getWindow().getDecorView().getRootView(), "No Esta Conectado a Internet...!", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (titulo.isEmpty()) {
            et_titulo_incidente.setError("Ingrese un título");
            return;
        }

        if (descripcion.length() <= 3) {
            et_descripcion_incidente.setError("Ingrese una breve descripcion");
            return;
        }

        String key = mDatabase.push().getKey();

        /*incidente.registrarIncidente(new Incidente(
                descripcion,
                DateUtil.FechaCorta(),
                DateUtil.HoraActual(),
                photoFile.getName(),
                latitude,
                longitude,
                titulo,
                key,
                getCurrentUser()
        ));*/


        progressDialog.setMessage("Enviando notificacion");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mDatabase.child("incidentes")
                .child(key)
                .setValue(new Incidente(
                        descripcion,
                        DateUtil.FechaCorta(),
                        DateUtil.HoraActual(),
                        photoFile.getName(),
                        latitude,
                        longitude,
                        titulo,
                        key,
                        getCurrentUser()
                ))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "se guardo correctamente");
                        uploadFile();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "hubo un error al guardar el incidente " + e.getMessage());
                        progressDialog.dismiss();
                    }
                });

    }

    private String getCurrentUser() {
        return mAuth.getCurrentUser().getUid();
        //Log.d(TAG,"User -> "+mAuth.getCurrentUser().getEmail()+ "-"+mAuth.getCurrentUser().getUid());
        //user.getUserData(mAuth.getCurrentUser().getUid());
        //System.out.println("correo "+user.getUsuarios().getCorreo());
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = mloLocationManager.getLastKnownLocation(mloLocationManager.getBestProvider(criteria, false));
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d(TAG, "show last know location Latitude: "+latitude+" Longitude: "+longitude);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) { //&& resultCode == RESULT_OK) {
            Log.i("TAG", "onActivityResult: resultCode" + resultCode);
            Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            //Bundle extras = data.getExtras();
            /*Bitmap imageBitmap = (Bitmap) extras.get("data");*/
            iv_incidente.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv_incidente.setImageBitmap(takenImage);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_foto_incidente:
                dispatchTakePictureIntent();
                break;
            case R.id.btn_reg_incidente:
                if (!mloLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    displayPromptForEnablingGPS(this);
                } else {
                    validateData();
                }
                break;
        }
    }

    public void showDialog(String message){
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }
}