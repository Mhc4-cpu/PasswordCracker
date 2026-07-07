package passwordcracker.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilitaire de hachage MD5, partagé par toutes les stratégies de
 * cassage afin d'éviter la duplication de code (une seule implémentation
 * du calcul de hash pour tout le projet).
 *
 * Cette classe est volontairement "utilitaire" : elle ne contient que
 * des méthodes statiques et ne conserve aucun état.
 */
public final class Md5Util {

    // Empêche l'instanciation : cette classe n'a pas de raison d'être
    // un objet, elle n'expose que des fonctions pures.
    private Md5Util() {
    }

    /**
     * Calcule le hash MD5 d'une chaîne et le retourne sous forme
     * hexadécimale en minuscules (ex: "5f4dcc3b5aa765d61d8327deb882cf99").
     *
     * @param input la chaîne à hacher (par exemple un mot candidat)
     * @return le hash MD5 en hexadécimal, toujours en minuscules
     */
    public static String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(input.getBytes());
            return bytesToHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            // MD5 fait partie du JDK standard : ce cas ne devrait
            // jamais se produire en pratique.
            throw new IllegalStateException("Algorithme MD5 indisponible", e);
        }
    }

    /**
     * Compare un mot candidat au hash cible, en ignorant la casse.
     *
     * C'est cette méthode que les stratégies (DictionaryHashCracker,
     * BruteForceHashCracker) doivent utiliser plutôt que de recalculer
     * et comparer elles-mêmes, afin d'éviter la duplication de logique
     * de comparaison (et les bugs liés à la casse hexadécimale).
     *
     * @param candidate le mot candidat en clair
     * @param targetHash le hash MD5 recherché
     * @return true si le hash du candidat correspond au hash cible
     */
    public static boolean matches(String candidate, String targetHash) {
        if (candidate == null || targetHash == null) {
            return false;
        }
        return hash(candidate).equalsIgnoreCase(targetHash);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
