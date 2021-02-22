package com.raghu.viitorclouddemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.tabs.TabLayout;
import com.raghu.viitorclouddemo.retrofit.EmployeeModel;
import com.raghu.viitorclouddemo.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<EmployeeModel> allDataList = new ArrayList<>();
    private ArrayList<EmployeeModel> chicagoList = new ArrayList<>();
    private ArrayList<EmployeeModel> newYorkList = new ArrayList<>();
    private ArrayList<EmployeeModel> losAngelesList = new ArrayList<>();
    private ViewPager viewPager;
    private TabLayout tablayout;
    private ProgressBar progress;
    ActionMode actionMode;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        tablayout = findViewById(R.id.tablayout);
        progress = findViewById(R.id.progress);

        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "onTabSelected: ");
                if (actionMode != null) {
                    actionMode.finish();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        getData();
    }

    private void getData() {
        Call<ArrayList<EmployeeModel>> call = RetrofitClient.getInstance().getApi().getEmployee();
        call.enqueue(new Callback<ArrayList<EmployeeModel>>() {
            @Override
            public void onResponse(Call<ArrayList<EmployeeModel>> call, Response<ArrayList<EmployeeModel>> response) {
                Log.d(TAG, "onResponse: ");
                progress.setVisibility(View.GONE);
                if (response != null && response.isSuccessful()) {
                    allDataList = response.body();
                    if (allDataList != null) {
                        Log.d(TAG, "onResponse: size: " + allDataList.size());


                        for (int i = 0; i < allDataList.size(); i++) {
                            String city = allDataList.get(i).getCity();
                            if (city.equalsIgnoreCase("NewYork")) {
                                newYorkList.add(allDataList.get(i));
                            } else if (city.equalsIgnoreCase("Los Angeles")) {
                                losAngelesList.add(allDataList.get(i));
                            } else if (city.equalsIgnoreCase("Chicago")) {
                                chicagoList.add(allDataList.get(i));
                            }
                        }

                        Log.d(TAG, "onResponse: chicagoList: " + chicagoList.size());
                        Log.d(TAG, "onResponse: newYorkList: " + newYorkList.size());
                        Log.d(TAG, "onResponse: losAngelesList: " + losAngelesList.size());

                        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                        viewPager.setAdapter(viewPagerAdapter);
                        tablayout.setupWithViewPager(viewPager);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<EmployeeModel>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                progress.setVisibility(View.GONE);
            }
        });
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            UserFragment userFragment = null;
            if (position == 0) {
                userFragment = new UserFragment(allDataList);
            } else if (position == 1) {
                userFragment = new UserFragment(chicagoList);
            } else if (position == 2) {
                userFragment = new UserFragment(newYorkList);
            } else if (position == 3) {
                userFragment = new UserFragment(losAngelesList);
            }
            return userFragment;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            if (position == 0) {
                title = "All";
            } else if (position == 1) {
                title = "Chicago";
            } else if (position == 2) {
                title = "NewYork";
            } else if (position == 3) {
                title = "Los Angeles";
            }
            return title;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}