package com.core.alertaciudadana.presenters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.core.alertaciudadana.interfaces.UserInteractor;
import com.core.alertaciudadana.models.user.Usuarios;
import com.core.alertaciudadana.util.MessageResponse;
import com.core.alertaciudadana.views.MenuDrawer;
import com.core.alertaciudadana.views.login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserImpl implements UserInteractor {

    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private final static String TAG = UserImpl.class.getSimpleName().toString();
    private Usuarios usuarios = new Usuarios();

    public UserImpl(Context context, FirebaseAuth mAuth, DatabaseReference mDatabase) {
        this.context = context;
        this.mAuth = mAuth;
        this.mDatabase = mDatabase;
    }

    public Usuarios getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Usuarios usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    public void login(String user, String pass, boolean checked) {
        ProgressDialog progressDialog = new ProgressDialog(context);

        progressDialog.setMessage("Validando Cuenta...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(user, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String uuid;
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            progressDialog.dismiss();
                            Log.w(TAG, "signInWithEmail", task.getException());
                            Toast.makeText(context, "Inicio de sesion fallido...!!", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.dismiss();
                            SharedPreferences prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE);
                            //prefs.edit();
                            SharedPreferences.Editor editor = prefs.edit();
                            uuid = task.getResult().getUser().getUid();
                            Log.d(TAG, "task uid -> " + uuid);
                            editor.putString("user", uuid);
                            editor.putBoolean("remind", checked);
                            editor.commit();
                            Toast.makeText(context, "Bienvenido", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, MenuDrawer.class);
                            intent.putExtra("usuario", uuid);
                            context.startActivity(intent);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.i(TAG, "signInWithEmail", e.getCause());
                    }
                });
    }


    @Override
    public void logout() {
        mAuth.signOut();
    }

    @Override
    public void createAccount(Usuarios usuarios) {

        ProgressDialog progressDialog = new ProgressDialog(context);

        progressDialog.setMessage("Registrando usuario...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(usuarios.getCorreo(), usuarios.getClave())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            String uid = task.getResult().getUser().getUid();

                            //String tokenGcm = FirebaseInstallations.getInstance().getId().getResult();
                            Log.d(TAG, "token GCM: " + uid);

                            Usuarios newUsuarios = new Usuarios(
                                    usuarios.getApellidos(),
                                    usuarios.getClave(),
                                    usuarios.getCorreo(),
                                    usuarios.getDireccion(),
                                    usuarios.getFechanac(),
                                    usuarios.getImagen(),
                                    usuarios.getNombres(),
                                    usuarios.getNumerodocumento(),
                                    usuarios.getSexo(),
                                    usuarios.getTelefono(),
                                    usuarios.getTipoacceso(),
                                    uid,
                                    "2" //2 -> movil
                            );

                            mDatabase.child("usuarios")
                                    .child(uid)
                                    .setValue(newUsuarios);

                            Toast.makeText(context, "Usuario registrado correctamente",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, MenuDrawer.class);
                            intent.putExtra("usuario",uid);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);

                        }else{
                            Toast.makeText(context, "No se pudo registrar usuario ",
                                    Toast.LENGTH_SHORT).show();
                        }
                        //if (!task.isSuccessful()) {
                            /*Toast.makeText(context, "No se pudo registrar usuario, intente en otro momento porfavor...!",
                                    Toast.LENGTH_SHORT).show();*/
                        //} else {
                            /*Toast.makeText(context, "Usuario registrado correctamente",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, MenuDrawer.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);*/
                        //}
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e.getMessage().compareTo(MessageResponse.EMAILUSED.getMessageEnglish()) == 0){
                            Toast.makeText(context, MessageResponse.EMAILUSED.getMessageSpanish(),Toast.LENGTH_SHORT).show();
                        }else{
                            Log.e(TAG, "Error " + e.getMessage());
                            Toast.makeText(context, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    public void loginAnonymous() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInAnonymously:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void getUserData(String uid) {
        /*mDatabase.getDatabase().getReference().child("usuarios")
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot i: dataSnapshot.getChildren()) {
                    Log.i(TAG, "onDataChange: " + i);
                    Log.i(TAG, "key: "+i.getKey());
                    usuarios = i.getValue(Usuarios.class);
                    if (usuarios.getTokengcm().equals(uid)){
                        return;
                    }
                }
                System.out.println("data usuario"+dataSnapshot.getValue(Usuarios.class));
                //usuarios = dataSnapshot.getValue(Usuarios.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });*/
        mDatabase.getDatabase().getReference().child("usuarios").child(uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        usuarios = dataSnapshot.getValue(Usuarios.class);
                        System.out.println("data usuario" + usuarios.getCorreo());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    @Override
    public List<Usuarios> getUsers() {
        List<Usuarios> lstUsers = new ArrayList<>();
        mDatabase.child("usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    //System.out.println(postSnapshot.getValue(Usuarios.class));
                    lstUsers.add(postSnapshot.getValue(Usuarios.class));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

        return lstUsers;
    }

    public boolean isLogged(String uid) {
        final boolean[] flag = {false};
        mDatabase.getDatabase().getReference().child("usuarios").child(uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        /*usuarios = dataSnapshot.getValue(Usuarios.class);
                        System.out.println("data usuario"+usuarios.getCorreo());*/
                        /*if(!dataSnapshot.exists()){
                            logout();
                            SharedPreferences prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("user", null);
                            editor.commit();
                            Intent intent = new Intent(context, login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }*/
                        flag[0] = true;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        flag[0] = false;
                    }
                });
        return flag[0];
    }


}
