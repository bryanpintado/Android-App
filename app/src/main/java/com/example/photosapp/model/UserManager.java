package com.example.photosapp.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import photos22.util.AlertUtil;

public class UserManager {
    private static final String storeDir = "data";
    private static final String storeFile = "users.dat";

    private static UserManager instance;
    private final ObservableList<User> users = FXCollections.observableArrayList();

    private UserManager() {
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public ObservableList<User> getUsers() {
        return users;
    }

    public void deleteUser(String username) {
        if (!isUserInList(username)) {
            AlertUtil.showAlert("Error: username (" + username + ") is not in the list");
            return;
        }
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                users.remove(user);
                break;
            }
        }
    }

    public boolean isUserInList(String username) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public void saveUsers() {
        try {
            File dir = new File(storeDir);
            if (!dir.exists()) {
                dir.mkdir();
            }
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(storeDir + File.separator + storeFile));
            oos.writeObject(new ArrayList<>(users));
            oos.close();
        } catch (IOException e) {
            AlertUtil.showAlert("Error saving users: " + e.toString());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadUsers() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storeDir + File.separator + storeFile));
            ArrayList<User> loadedUsers = (ArrayList<User>) ois.readObject();
            users.setAll(loadedUsers);
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            AlertUtil.showAlert("Error: " + e.toString());
        }
    }

    public User getUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    public boolean addUser(User user) {
        if (isUserInList(user.getUsername())) {
            AlertUtil.showAlert("Error: Username already exists!!");
            return false;
        }
        users.add(user);
        return true;
    }

}