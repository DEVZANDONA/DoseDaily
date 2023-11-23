package com.tcc.DoseDaily.System_UI;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.tcc.DoseDaily.Auth.LoginActivity;
import com.tcc.DoseDaily.R;

public class SideBar {

    public static void setupDrawer(Context context, DrawerLayout drawerLayout, NavigationView navigationView, String userId) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_profile) {
                    startNewActivity(context, ProfileActivity.class, userId);
                    return true;
                } else if (itemId == R.id.nav_meds) {
                    startNewActivity(context, ListMedicinesActivity.class, userId);
                    return true;
                } else if (itemId == R.id.nav_bell) {
                    startNewActivity(context, RemindersActivity.class, userId);
                    return true;
                } else if (itemId == R.id.nav_history) {
                    startNewActivity(context, HistoryActivity.class, userId);
                    return true;
                } else if (itemId == R.id.nav_home) {
                    startNewActivity(context, HomePage.class, userId);
                    return true;
                } else if (itemId == R.id.nav_logout) {
                    FirebaseAuth.getInstance().signOut();
                    Intent loginIntent = new Intent(context, LoginActivity.class);
                    context.startActivity(loginIntent);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private static void startNewActivity(Context context, Class<?> destinationActivity, String userId) {
        Intent intent = new Intent(context, destinationActivity);
        intent.putExtra("userId", userId);
        context.startActivity(intent);
    }

    public static boolean handleBackPressed(Context context, DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else {
            return false;
        }
    }
}
