package me.bedepay.lighthealth.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerPrefsTest {

    @TempDir
    Path tempDir;

    @Test
    void persistsOnboardingSeparatelyFromToggle() {
        final File file = tempDir.resolve("player-toggles.yml").toFile();
        final UUID playerId = UUID.randomUUID();
        final PlayerPrefs prefs = new PlayerPrefs(file, Logger.getAnonymousLogger());

        assertTrue(prefs.isEnabled(playerId));
        assertFalse(prefs.hasSeenOnboarding(playerId));
        assertTrue(prefs.markOnboardingShown(playerId));
        assertFalse(prefs.markOnboardingShown(playerId));
        assertFalse(prefs.toggle(playerId));

        final PlayerPrefs reloaded = new PlayerPrefs(file, Logger.getAnonymousLogger());
        assertTrue(reloaded.hasSeenOnboarding(playerId));
        assertFalse(reloaded.isEnabled(playerId));
        assertTrue(reloaded.toggle(playerId));
        assertTrue(reloaded.hasSeenOnboarding(playerId));
    }
}
