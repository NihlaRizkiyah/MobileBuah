package com.project.pointofsaleproject.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.project.pointofsaleproject.R;

public class HomeFragment extends Fragment {
    BottomNavigationView btmNav;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        btmNav = root.findViewById(R.id.navigation);
        btmNav.getMenu().getItem(0).setIcon(R.drawable.ic_home);

        HomeDashboardFragment homeDashboardFragment = new HomeDashboardFragment();
        HomeSettingFragment homeSettingFragment = new HomeSettingFragment();

        replaceFragment(homeDashboardFragment);

        btmNav.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.menu_setting:
                            replaceFragment(homeSettingFragment);
                            break;
                        default:
                            replaceFragment(homeDashboardFragment);
                            break;
                    }
                    return true;
                });

        return root;
    }

    private void replaceFragment(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_sub, fragment)
                .commit();
    }

}