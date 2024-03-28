package com.gero.newpass.viewmodel;

import com.gero.newpass.model.database.DatabaseHelper;
import com.gero.newpass.model.database.DatabaseServiceLocator;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddViewModel extends ViewModel {

    private final DatabaseHelper databaseHelper;
    private final MutableLiveData<String> messageLiveData = new MutableLiveData<>();

    public AddViewModel() {
        databaseHelper = DatabaseServiceLocator.getDatabaseHelper();
    }

    public LiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    public void addEntry(String name, String email, String password) {

            if (
                    name.length() >= 4 && name.length() <= 10 &&                     // name    [4, 10]
                            email.length() >= 4 && email.length() <= 30 &&           // email   [4, 30]
                            password.length() >= 4 && password.length() <= 15        // psw     [4, 15]
            ) {

                if (databaseHelper.checkIfAccountAlreadyExist(name, email)) {
                    messageLiveData.setValue("This account already exists!");

                } else  {
                    databaseHelper.addEntry(name, email, password);
                    messageLiveData.setValue("Entry added successfully");
                }

            } else {
                if (name.length() < 4 || name.length() > 10) {
                    messageLiveData.setValue("Name should be 4 to 10 characters long!");

                } else if (email.length() < 4 || email.length() > 30) {
                    messageLiveData.setValue("Email should be 4 to 30 characters long!");

                } else {
                    messageLiveData.setValue("Password should be 4 to 15 characters long!");
                }
            }
    }
}
