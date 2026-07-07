package passwordcracker.cracker;

import passwordcracker.util.Md5Util;

/**
 * Stratégie de cassage par force brute : génère toutes les combinaisons
 * possibles de caractères (alphabet a-z), de longueur 1 à 4, et teste
 * chacune d'elles jusqu'à trouver une correspondance.
 *
 * Comme {@link DictionaryHashCracker}, cette classe ne fait que GÉNÉRER
 * des candidats et les COMPARER via {@link Md5Util} ; le calcul de hash
 * lui-même n'est jamais dupliqué ici.
 */
public class BruteForceHashCracker implements HashCracker {

    private static final char[] ALPHABET =
            "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final int MAX_LENGTH = 4;

    private int attempts;

    @Override
    public String crack(String hash) {
        attempts = 0;

        for (int length = 1; length <= MAX_LENGTH; length++) {
            String found = tryAllCombinations(new char[length], 0, hash);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /**
     * Nombre de combinaisons testées lors du dernier appel à
     * {@link #crack(String)}. Utile pour l'affichage d'informations
     * de diagnostic (cf. cahier des charges).
     */
    public int getAttempts() {
        return attempts;
    }

    /**
     * Génère récursivement toutes les combinaisons de longueur fixe
     * {@code buffer.length}, en remplissant {@code buffer} position par
     * position à partir de l'index {@code position}.
     *
     * Dès qu'une combinaison complète correspond au hash cible, la
     * récursion s'arrête immédiatement et remonte le résultat trouvé
     * (pas de génération inutile après une correspondance).
     *
     * @param buffer   tableau de caractères en cours de construction
     * @param position index de la position à remplir dans buffer
     * @param hash     le hash MD5 recherché
     * @return le mot trouvé, ou null si aucune combinaison ne correspond
     */
    private String tryAllCombinations(char[] buffer, int position, String hash) {
        if (position == buffer.length) {
            String candidate = new String(buffer);
            attempts++;
            return Md5Util.matches(candidate, hash) ? candidate : null;
        }

        for (char c : ALPHABET) {
            buffer[position] = c;
            String found = tryAllCombinations(buffer, position + 1, hash);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
}
