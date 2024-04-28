package com.gero.newpass.view.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.security.crypto.EncryptedSharedPreferences;

import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.gero.newpass.R;
import com.gero.newpass.database.DatabaseHelper;
import com.gero.newpass.databinding.FragmentSettingsBinding;
import com.gero.newpass.encryption.EncryptionHelper;
import com.gero.newpass.model.SettingData;
import com.gero.newpass.utilities.PathUtil;
import com.gero.newpass.utilities.VibrationHelper;
import com.gero.newpass.view.activities.MainViewActivity;
import com.gero.newpass.view.adapters.SettingsAdapter;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class SettingsFragment extends Fragment {
    private static final int REQUEST_CODE_SAVE_DOCUMENT = 1;
    private ImageButton buttonBack;
    private FragmentSettingsBinding binding;
    private ListView listView;
    private String url;
    private Intent intent;
    private EncryptedSharedPreferences encryptedSharedPreferences;
    static final int DARK_THEME = 0;
    static final int USE_SCREENLOCK = 1;
    static final int CHANGE_LANGUAGE = 2;
    static final int CHANGE_PASSWORD = 3;
    static final int EXPORT = 4;
    static final int IMPORT = 5;
    static final int GITHUB = 6;
    static final int SHARE = 7;
    static final int CONTACT = 8;
    static final int APP_VERSION = 9;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(binding);
        Activity activity = this.getActivity();

        ArrayList<SettingData> arrayList = new ArrayList<>();
        encryptedSharedPreferences = EncryptionHelper.getEncryptedSharedPreferences(requireContext());

        buttonBack.setOnClickListener(v -> {
            if (activity instanceof MainViewActivity) {
                ((MainViewActivity) activity).onBackPressed();
            }
        });

        createSettingsList(arrayList);

        SettingsAdapter settingsAdapter = new SettingsAdapter(requireContext(), R.layout.list_row, arrayList, getActivity());

        listView.setAdapter(settingsAdapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {

            switch(position) {

                case CHANGE_LANGUAGE:
                    VibrationHelper.vibrate(requireContext(), getResources().getInteger(R.integer.vibration_duration1));

                    DialogFragment languageDialogFragment = new LanguageDialogFragment();
                    languageDialogFragment.setCancelable(false);
                    languageDialogFragment.show(requireActivity().getSupportFragmentManager(), "Language Dialog");

                    break;

                case CHANGE_PASSWORD:
                    VibrationHelper.vibrate(requireContext(), getResources().getInteger(R.integer.vibration_duration1));
                    showDialog();
                    break;

                case EXPORT:
                    //TODO
                    VibrationHelper.vibrate(requireContext(), getResources().getInteger(R.integer.vibration_duration1));
                    //Toast.makeText(requireContext(), R.string.this_feature_will_be_available_soon, Toast.LENGTH_SHORT).show();
                    //DatabaseHelper.exportDB(requireActivity(), requireContext());
                    startFileSelection();
                    break;

                case IMPORT:
                    //TODO
                    VibrationHelper.vibrate(requireContext(), getResources().getInteger(R.integer.vibration_duration1));
                    Toast.makeText(requireContext(), R.string.this_feature_will_be_available_soon, Toast.LENGTH_SHORT).show();
                    break;

                case GITHUB:
                    VibrationHelper.vibrate(requireContext(), getResources().getInteger(R.integer.vibration_duration1));
                    url = "https://github.com/6eero/NewPass";
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    break;

                case SHARE:
                    VibrationHelper.vibrate(requireContext(), getResources().getInteger(R.integer.vibration_duration1));
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_share_text));
                    startActivity(Intent.createChooser(shareIntent, "Share with..."));
                    break;

                case CONTACT:
                    VibrationHelper.vibrate(requireContext(), getResources().getInteger(R.integer.vibration_duration1));
                    url = "https://t.me/geroED";
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    break;

                case APP_VERSION:
                    break;
            }
        });
    }

    private void createSettingsList(ArrayList<SettingData> arrayList) {
        arrayList.add(new SettingData(R.drawable.settings_icon_dark_theme, getString(R.string.settings_dark_theme), false, true, 1));
        arrayList.add(new SettingData(R.drawable.icon_open_lock, getString(R.string.use_screen_lock_to_unlock), false, true, 2));
        arrayList.add(new SettingData(R.drawable.settings_icon_language, getString(R.string.settings_change_language)));
        arrayList.add(new SettingData(R.drawable.settings_icon_lock, getString(R.string.settings_change_password)));
        arrayList.add(new SettingData(R.drawable.icon_export, getString(R.string.settings_export_db)));
        arrayList.add(new SettingData(R.drawable.icon_import, getString(R.string.settings_import_db)));
        arrayList.add(new SettingData(R.drawable.settings_icon_github, getString(R.string.settings_github), true));
        arrayList.add(new SettingData(R.drawable.settings_icon_share, getString(R.string.settings_share_newpass), true));
        arrayList.add(new SettingData(R.drawable.settings_icon_telegram, getString(R.string.settings_contact_me), true));
        arrayList.add(new SettingData(R.drawable.settings_icon_version, getString(R.string.app_version) + getAppVersion()));
    }

    private String getAppVersion() {
        String versionName = "";

        try {
            PackageManager packageManager = requireActivity().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(requireActivity().getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    private void initViews(FragmentSettingsBinding binding) {
        buttonBack = binding.backButton;
        listView = binding.listView;
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_inputs, null);
        builder.setView(dialogView);

        EditText firstInput = dialogView.findViewById(R.id.first_input);
        EditText secondInput = dialogView.findViewById(R.id.second_input);

        builder.setTitle(R.string.settings_change_password)
                .setPositiveButton(R.string.update_alertdialog_yes, (dialog, id) -> {

                    String inputOne = firstInput.getText().toString();
                    String inputTwo = secondInput.getText().toString();

                    if (inputOne.equals(encryptedSharedPreferences.getString("password", "")) && inputTwo.length() >= 4) {
                        //Log.i("2895124", "Correct password");

                        SharedPreferences.Editor editor = encryptedSharedPreferences.edit();
                        editor.putString("password", inputTwo);
                        editor.apply();

                        //Log.w("Database123", "psw in the encryptedsharedpref aka new key: " + encryptedSharedPreferences.getString("password", ""));
                        DatabaseHelper.changeDBPassword(inputTwo, requireContext());
                    } else if (inputTwo.length() < 4) {
                        Toast.makeText(requireContext(), R.string.password_must_be_at_least_4_characters_long, Toast.LENGTH_SHORT).show();

                    } else {
                        //Log.i("2895124", "Incorrect password");
                        Toast.makeText(requireContext(), R.string.wrong_password, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.update_alertdialog_no, (dialog, id) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void startFileSelection() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE, "Password.db");
        startActivityForResult(intent, REQUEST_CODE_SAVE_DOCUMENT);
    }

    // Gestisci il risultato dell'attività di selezione del file
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SAVE_DOCUMENT && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {

                Uri uri = data.getData();

                //String filePath = PathUtil.getPath(requireContext(), uri).substring(0, PathUtil.getPath(requireContext(), uri).lastIndexOf('/'));
                //DatabaseHelper.exportDB(requireContext(), filePath);

                DatabaseHelper.exportDB(requireContext(), null);

            }
        }
    }
}