package org.sausagedev.soseller.utils;

import org.sausagedev.soseller.Configuration.Config;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SellingFields {

    public static List<Integer> getSlots(String menu) {
        for (Map<?, ?> f : Config.settings().sellingFields()) {
            String fieldMenu = String.valueOf(f.get("gui"));
            if (!fieldMenu.equals(menu)) continue;
            return (List<Integer>) f.get("slots");
        }
        return Collections.emptyList();
    }
}
