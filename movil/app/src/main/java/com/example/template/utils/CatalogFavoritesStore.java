package com.example.template.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Persists catalog favorites on this device, keeping a separate set for each store.
 * No account or server data is used for this storage.
 */
public final class CatalogFavoritesStore {
    private static final String PREFERENCES_NAME = "catalog_favorites";
    private static final String FAVORITES_KEY_PREFIX = "product_ids:";

    private final SharedPreferences preferences;
    private String currentStoreKey;

    public CatalogFavoritesStore(Context context) {
        Context appContext = context.getApplicationContext();
        preferences = appContext.getSharedPreferences(
                PREFERENCES_NAME,
                Context.MODE_PRIVATE
        );
    }

    /** Selects the storage partition for a store. Its database ID is preferred. */
    public void selectStore(String storeId, String storeDomain) {
        if (hasText(storeId)) {
            currentStoreKey = "store_id:" + storeId.trim();
        } else if (hasText(storeDomain)) {
            currentStoreKey = "domain:" + storeDomain.trim().toLowerCase(Locale.ROOT);
        } else {
            currentStoreKey = null;
        }
    }

    public boolean isFavorite(String productId) {
        return hasText(productId) && loadFavoriteIds().contains(productId);
    }

    /** Adds a favorite and returns true, or removes it and returns false. */
    public boolean toggleFavorite(String productId) {
        if (!hasText(productId) || currentStoreKey == null) {
            return false;
        }

        Set<String> favoriteIds = loadFavoriteIds();
        boolean added = favoriteIds.add(productId);
        if (!added) {
            favoriteIds.remove(productId);
        }

        preferences.edit()
                .putStringSet(getPreferencesKey(), favoriteIds)
                .apply();
        return added;
    }

    private Set<String> loadFavoriteIds() {
        if (currentStoreKey == null) {
            return new HashSet<>();
        }

        Set<String> savedIds = preferences.getStringSet(
                getPreferencesKey(),
                Collections.emptySet()
        );
        return savedIds == null ? new HashSet<>() : new HashSet<>(savedIds);
    }

    private String getPreferencesKey() {
        return FAVORITES_KEY_PREFIX + currentStoreKey;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
