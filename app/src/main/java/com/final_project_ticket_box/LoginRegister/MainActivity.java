package com.final_project_ticket_box.LoginRegister;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.final_project_ticket_box.HomeFragment;
import com.final_project_ticket_box.MyTicketFragment;
import com.final_project_ticket_box.R;
import com.final_project_ticket_box.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment = new HomeFragment();
    private MyTicketFragment myTicketFragment = new MyTicketFragment();
    private SettingFragment settingFragment = new SettingFragment();

    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Kiểm tra trạng thái đăng nhập
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Nếu chưa đăng nhập, chuyển hướng đến LoginActivity
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
            return; // Ngăn không cho các đoạn mã sau được thực thi
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

//        // Load sessions
//        loadCartSession(this);
//        loadUserSession();
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        Fragment selectedFragment = null;

        int itemId = item.getItemId();

        if (itemId == R.id.home) {
            selectedFragment = homeFragment;
        }

        if (selectedFragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, selectedFragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }

        return false;
    }
}