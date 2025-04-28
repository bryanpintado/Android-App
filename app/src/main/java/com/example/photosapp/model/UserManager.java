package com.example.photosapp.model;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;


public class UserManager {
    private static final String storeDir = "data";
    private static final String storeFile = "users.dat";

    private static UserManager instance;
    private final ArrayList<User> users = new ArrayList<>();

    private UserManager() {
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void deleteUser(String username) {
        if (!isUserInList(username)) {
            System.out.println("Error: username (" + username + ") is not in the list");
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

    public void saveUsers(Context context) {
        try {
            File file = new File(context.getFilesDir(), storeFile);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(new ArrayList<>(users));
            oos.close();
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.toString());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadUsers(Context context) {
        try {
            File file = new File(context.getFilesDir(), storeFile);
            if (!file.exists()) {
                return; // No users yet
            }
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            ArrayList<User> loadedUsers = (ArrayList<User>) ois.readObject();
            users.clear();
            users.addAll(loadedUsers);
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading users: " + e.toString());
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
            System.out.println("Error: Username already exists!!");
            return false;
        }
        users.add(user);
        return true;
    }

}