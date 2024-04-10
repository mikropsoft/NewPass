package com.gero.newpass.view.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.gero.newpass.R;
import com.gero.newpass.databinding.FragmentMainViewBinding;

import com.gero.newpass.utilities.VibrationHelper;
import com.gero.newpass.view.activities.MainViewActivity;
import com.gero.newpass.view.adapters.CustomAdapter;
import com.gero.newpass.viewmodel.MainViewModel;

import java.util.Objects;


public class MainViewFragment extends Fragment {

    private FragmentMainViewBinding binding;
    private TextView noData, count;
    private ImageView empty_imageview;
    private RecyclerView recyclerView;
    private ImageButton buttonGenerate, buttonAdd, buttonSettings;
    private MainViewModel mainViewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMainViewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        initViews();

        populateUI();


        //Navigating to generate/ add password and settings fragments using the method inherited from the base activity
        Activity activity = this.getActivity();
        if (activity instanceof MainViewActivity) {


            buttonSettings.setOnClickListener(v -> {
                VibrationHelper.vibrate(requireContext(), getResources().getInteger(R.integer.vibration_duration1));
                ((MainViewActivity) activity).openFragment(new SettingsFragment());
            });


            buttonAdd.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        VibrationHelper.vibrate(requireContext(), getResources().getInteger(R.integer.vibration_duration0));
                        return true;
                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        VibrationHelper.vibrate(requireContext(), getResources().getInteger(R.integer.vibration_duration1));
                        ((MainViewActivity) activity).openFragment(new AddPasswordFragment());
                        return true;
                }
                return false;
            });


            buttonGenerate.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        VibrationHelper.vibrate(requireContext(), getResources().getInteger(R.integer.vibration_duration0));
                        return true;
                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        VibrationHelper.vibrate(requireContext(), getResources().getInteger(R.integer.vibration_duration1));
                        ((MainViewActivity) activity).openFragment(new GeneratePasswordFragment());
                        return true;
                }
                return false;
            });
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nullify the binding object to avoid memory leaks
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            String result = bundle.getString("resultKey");
            // Updating UI after update
            if (Objects.equals(result, "1")) {
                populateUI();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void populateUI() {
        mainViewModel.storeDataInArrays();

        mainViewModel.getUserDataList().observe(getViewLifecycleOwner(), userDataList -> {
            CustomAdapter customAdapter = new CustomAdapter(this.getActivity(), this.getContext(), userDataList);
            recyclerView.setAdapter(customAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

            count.setText("[" + customAdapter.getItemCount() + "]");

            if (customAdapter.getItemCount() == 0) {
                empty_imageview.setVisibility(View.VISIBLE);
                noData.setVisibility(View.VISIBLE);
            } else {
                empty_imageview.setVisibility(View.GONE);
                noData.setVisibility(View.GONE);
            }
        });
    }

    private void initViews() {
        recyclerView = binding.recyclerView;
        buttonGenerate = binding.buttonGenerate;
        buttonAdd = binding.buttonAdd;
        buttonSettings = binding.buttonSettings;
        count = binding.textViewCount;
        empty_imageview = binding.emptyImageview;
        noData = binding.noData;
    }

}