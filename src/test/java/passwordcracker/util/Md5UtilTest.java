package passwordcracker.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Md5UtilTest {

    @Test
    void hash_shouldProduceKnownMd5Value() {
        // MD5("test") est une valeur connue et documentée partout,
        // ça sert de référence pour vérifier que l'implémentation est correcte.
        assertEquals("098f6bcd4621d373cade4e832627b4f6", Md5Util.hash("test"));
    }

    @Test
    void matches_shouldReturnTrue_whenCandidateHashesToTarget() {
        String targetHash = Md5Util.hash("password");
        assertTrue(Md5Util.matches("password", targetHash));
    }

    @Test
    void matches_shouldReturnFalse_whenCandidateDoesNotMatch() {
        String targetHash = Md5Util.hash("password");
        assertFalse(Md5Util.matches("wrongword", targetHash));
    }

    @Test
    void matches_shouldBeCaseInsensitiveOnHash() {
        String targetHash = Md5Util.hash("azerty").toUpperCase();
        assertTrue(Md5Util.matches("azerty", targetHash));
    }
 
}
