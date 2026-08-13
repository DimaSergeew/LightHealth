package me.bedepay.lighthealth.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessagesNormalizeTest {

    @Test
    void normalizeSupportedCodes() {
        assertEquals("en", Messages.normalize("en"));
        assertEquals("ru", Messages.normalize("RU"));
        assertEquals("es", Messages.normalize("es"));
        assertEquals("zh", Messages.normalize("zh"));
    }

    @Test
    void normalizeAliases() {
        assertEquals("ru", Messages.normalize("russian"));
        assertEquals("zh", Messages.normalize("zh-CN"));
        assertEquals("zh", Messages.normalize("cn"));
        assertEquals("es", Messages.normalize("spanish"));
        assertEquals("en", Messages.normalize("english"));
    }

    @Test
    void normalizeFallback() {
        assertEquals("en", Messages.normalize(null));
        assertEquals("en", Messages.normalize(" "));
        assertEquals("en", Messages.normalize("nope"));
    }

    @Test
    void isSupported() {
        assertTrue(Messages.isSupported("en"));
        assertTrue(Messages.isSupported("ru-RU"));
        assertTrue(Messages.isSupported("es-MX"));
        assertTrue(Messages.isSupported("zh_CN"));
        assertFalse(Messages.isSupported("de"));
        assertFalse(Messages.isSupported(""));
        assertFalse(Messages.isSupported("esoteric"));
        assertFalse(Messages.isSupported("engine"));
        assertEquals("en", Messages.normalize("esoteric"));
        assertEquals("es", Messages.normalize("es-MX"));
    }
}
