package org.sausagedev.soseller.Configuration.data;

import java.util.List;
import java.util.Map;

public record MessagesField(
        List<String> help,
        Map<String, Object> materials,
        String configReload,
        String oldVersion,
        String lastVersion,
        String haveNoPerms,
        String numberFormatError,
        String nullPlayerError,
        String balanceEror,
        String maxBoostError,
        String vaultError,
        String buyBoost,
        String buyAutosell,
        String boostModify,
        String globalBoostModify,
        String itemsModify,
        String sold,
        String autoSellRemove,
        String autoSellGive,
        String allowAutosell,
        String denyAutosell
) {
}
