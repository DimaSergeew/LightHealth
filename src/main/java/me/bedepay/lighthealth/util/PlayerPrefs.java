package me.bedepay.lighthealth.util;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerPrefs {

    private final Set<UUID> disabled = ConcurrentHashMap.newKeySet();

    public boolean isEnabled(final UUID uuid) {
        return !this.disabled.contains(uuid);
    }

    public boolean toggle(final UUID uuid) {
        if (this.disabled.remove(uuid)) {
            return true;
        }
        this.disabled.add(uuid);
        return false;
    }

    public void clear() {
        this.disabled.clear();
    }
}
