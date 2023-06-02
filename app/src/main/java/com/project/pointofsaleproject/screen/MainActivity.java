package com.project.pointofsaleproject.screen;

import androidx.annotation.ColorRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.project.pointofsaleproject.R;
import com.project.pointofsaleproject.SharedPrefManager;
import com.project.pointofsaleproject.fragment.CustomerFragment;
import com.project.pointofsaleproject.fragment.HomeDashboardFragment;
import com.project.pointofsaleproject.fragment.HomeFragment;
import com.project.pointofsaleproject.fragment.HomeSettingFragment;
import com.project.pointofsaleproject.fragment.HomeTransactionFragment;
import com.project.pointofsaleproject.fragment.KategoriFragment;
import com.project.pointofsaleproject.fragment.OrderFragment;
import com.project.pointofsaleproject.fragment.PosFragment;
import com.project.pointofsaleproject.fragment.ProdukFragment;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView btmNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btmNav = findViewById(R.id.navigation);
        btmNav.getMenu().getItem(0).setIcon(R.drawable.ic_home);

        HomeDashboardFragment homeDashboardFragment = new HomeDashboardFragment();
        HomeSettingFragment homeSettingFragment = new HomeSettingFragment();
        HomeTransactionFragment homeTransactionFragment = new HomeTransactionFragment();

        replaceFragment(homeDashboardFragment);

        btmNav.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.menu_setting:
                            replaceFragment(homeSettingFragment);
                            break;
                        case R.id.menu_transaction:
                            replaceFragment(homeTransactionFragment);
                            break;
                        default:
                            replaceFragment(homeDashboardFragment);
                            break;
                    }
                    return true;
                });
    }

    private void replaceFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

}