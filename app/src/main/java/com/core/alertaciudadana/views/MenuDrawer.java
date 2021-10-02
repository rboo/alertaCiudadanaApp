package com.core.alertaciudadana.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.core.alertaciudadana.R;
import com.core.alertaciudadana.databinding.ActivityMenuDrawerBinding;
import com.core.alertaciudadana.presenters.UserImpl;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

public class MenuDrawer extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMenuDrawerBinding binding;
    private UserImpl user;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMenuDrawerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        user = new UserImpl(this, mAuth, mDatabase);
        prefs = getSharedPreferences("session", Context.MODE_PRIVATE);
        prefs.edit();

        setSupportActionBar(binding.appBarMenuDrawer.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu_drawer);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        //NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.i("TAG", "onNavigationItemSelected: prueba de que entra al click");
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        /*Fragment
                                flistadoPrincipal = ListadoIncidentesFragment.newInstance("valor1", "valor2");
                        getSupportFragmentManager().
                                beginTransaction().
                                replace(R.id.nav_host_fragment, flistadoPrincipal, "TAGCUENTA").
                                commit();*/
                        break;
                    case R.id.nav_close_session:
                        user.logout();
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("remind", false);
                        editor.commit();
                        Toast.makeText(MenuDrawer.this, "Cerrando sesion de usuario...!",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MenuDrawer.this, login.class);
                        startActivity(intent);
                        //finish();
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drawer, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu_drawer);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}