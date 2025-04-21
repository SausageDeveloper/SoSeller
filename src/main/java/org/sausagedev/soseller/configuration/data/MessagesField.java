package org.sausagedev.soseller.configuration.data;

import java.util.List;
import java.util.Map;

public class MessagesField {
    private final List<String> help;
    private final Map<String, Object> materials;
    private final String configReload;
    private final String oldVersion;
    private final String lastVersion;
    private final String haveNoPerms;
    private final String numberFormatError;
    private final String nullPlayerError;
    private final String balanceError;
    private final String maxBoostError;
    private final String vaultError;
    private final String buyBoost;
    private final String buyAutosell;
    private final String boostModify;
    private final String globalBoostModify;
    private final String itemsModify;
    private final String sold;
    private final String autoSellRemove;
    private final String autoSellGive;
    private final String allowAutosell;
    private final String denyAutosell;
    private final String guiMaxBoost;

    public MessagesField(List<String> help, Map<String, Object> materials, String configReload, String oldVersion, String lastVersion, String haveNoPerms, String numberFormatError, String nullPlayerError, String balanceError, String maxBoostError, String vaultError, String buyBoost, String buyAutosell, String boostModify, String globalBoostModify, String itemsModify, String sold, String autoSellRemove, String autoSellGive, String allowAutosell, String denyAutosell, String guiMaxBoost) {
        this.help = help;
        this.materials = materials;
        this.configReload = configReload;
        this.oldVersion = oldVersion;
        this.lastVersion = lastVersion;
        this.haveNoPerms = haveNoPerms;
        this.numberFormatError = numberFormatError;
        this.nullPlayerError = nullPlayerError;
        this.balanceError = balanceError;
        this.maxBoostError = maxBoostError;
        this.vaultError = vaultError;
        this.buyBoost = buyBoost;
        this.buyAutosell = buyAutosell;
        this.boostModify = boostModify;
        this.globalBoostModify = globalBoostModify;
        this.itemsModify = itemsModify;
        this.sold = sold;
        this.autoSellRemove = autoSellRemove;
        this.autoSellGive = autoSellGive;
        this.allowAutosell = allowAutosell;
        this.denyAutosell = denyAutosell;
        this.guiMaxBoost = guiMaxBoost;
    }

    public List<String> help() {
        return help;
    }

    public Map<String, Object> materials() {
        return materials;
    }

    public String configReload() {
        return configReload;
    }

    public String oldVersion() {
        return oldVersion;
    }

    public String lastVersion() {
        return lastVersion;
    }

    public String haveNoPerms() {
        return haveNoPerms;
    }

    public String numberFormatError() {
        return numberFormatError;
    }

    public String nullPlayerError() {
        return nullPlayerError;
    }

    public String balanceError() {
        return balanceError;
    }

    public String maxBoostError() {
        return maxBoostError;
    }

    public String globalBoostModify() {
        return globalBoostModify;
    }

    public String vaultError() {
        return vaultError;
    }

    public String buyBoost() {
        return buyBoost;
    }

    public String buyAutosell() {
        return buyAutosell;
    }

    public String boostModify() {
        return boostModify;
    }

    public String itemsModify() {
        return itemsModify;
    }

    public String sold() {
        return sold;
    }

    public String autoSellRemove() {
        return autoSellRemove;
    }

    public String autoSellGive() {
        return autoSellGive;
    }

    public String allowAutosell() {
        return allowAutosell;
    }

    public String denyAutosell() {
        return denyAutosell;
    }

    public String guiMaxBoost() {
        return guiMaxBoost;
    }
}
