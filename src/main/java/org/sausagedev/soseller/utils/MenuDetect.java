package org.sausagedev.soseller.utils;

public class MenuDetect {
    private static String menu;

    public static void setMenu(String menu) {
        MenuDetect.menu = menu;
    }

    public static String getMenu() {
        return menu != null ? menu : "main";
    }
}
