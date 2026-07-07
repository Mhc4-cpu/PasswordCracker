package passwordcracker.cracker;

import passwordcracker.util.Md5Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Stratégie de cassage par dictionnaire : compare le hash cible au hash
 * de chaque mot d'une liste connue, jusqu'à trouver une correspondance.
 *
 * Cette classe se contente de PARCOURIR des candidats et de les COMPARER
 * via {@link Md5Util} ; elle ne réimplémente jamais elle-même le calcul
 * de hash, afin d'éviter toute duplication de code avec
 * {@link BruteForceHashCracker}.
 */
public class DictionaryHashCracker implements HashCracker {

    /** Emplacement par défaut du dictionnaire, chargé depuis le classpath. */
    private static final String DEFAULT_DICTIONARY_RESOURCE = "dictionary.txt";

    private final String dictionaryResource;
    private int attempts;

    /**
     * Crée une stratégie utilisant le dictionnaire par défaut
     * (src/main/resources/dictionary.txt).
     */
    public DictionaryHashCracker() {
        this(DEFAULT_DICTIONARY_RESOURCE);
    }

    /**
     * Crée une stratégie utilisant un dictionnaire personnalisé.
     *
     * @param dictionaryResource chemin du fichier dictionnaire dans le classpath
     */
    public DictionaryHashCracker(String dictionaryResource) {
        this.dictionaryResource = dictionaryResource;
    }

    @Override
    public String crack(String hash) {
        attempts = 0;

        for (String word : loadWords()) {
            attempts++;
            if (Md5Util.matches(word, hash)) {
                return word;
            }
        }
        return null;
    }

    /**
     * Nombre de mots testés lors du dernier appel à {@link #crack(String)}.
     * Utile pour l'affichage d'informations de diagnostic (cf. cahier des charges).
     */
    public int getAttempts() {
        return attempts;
    }

    /**
     * Charge la liste des mots du dictionnaire, un mot par ligne.
     * Les lignes vides sont ignorées.
     */
    private List<String> loadWords() {
        List<String> words = new ArrayList<>();

        try (InputStream in = getClass().getClassLoader().getResourceAsStream(dictionaryResource)) {
            if (in == null) {
                throw new IllegalStateException(
                        "Dictionnaire introuvable dans le classpath : " + dictionaryResource);
            }
            try (BufferedReader reader =
                         new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String word = line.trim();
                    if (!word.isEmpty()) {
                        words.add(word);
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Erreur de lecture du dictionnaire : " + dictionaryResource, e);
        }

        return words;
    }
}
