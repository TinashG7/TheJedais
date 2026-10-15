package com.ims.util;

import com.ims.model.User;

public class SessionManager {
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isShopkeeper() {
        return currentUser != null && "shopkeeper".equalsIgnoreCase(currentUser.getRole());
    }

    public static boolean isAdmin() {
        return currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole());
    }

    public static boolean isOwner() {
        return currentUser != null && "owner".equalsIgnoreCase(currentUser.getRole());
    }
}