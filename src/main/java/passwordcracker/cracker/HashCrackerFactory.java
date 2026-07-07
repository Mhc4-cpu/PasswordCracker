package passwordcracker.factory;

import passwordcracker.cracker.BruteForceHashCracker;
import passwordcracker.cracker.DictionaryHashCracker;
import passwordcracker.cracker.HashCracker;

/**
 * Fabrique simple (Simple Factory) centralisant la création des
 * différentes stratégies de cassage de hash.
 *
 * C'est le SEUL endroit du projet où les classes concrètes
 * ({@link DictionaryHashCracker}, {@link BruteForceHashCracker})
 * doivent être instanciées avec {@code new}. Le reste du programme
 * (Main, tests) ne manipule que le type {@link HashCracker}, sans
 * jamais connaître la classe concrète réellement utilisée.
 *
 * Limite connue (assumée pour ce mini-projet) : l'ajout d'une nouvelle
 * méthode de cassage nécessite de modifier cette classe (ajout d'un
 * nouveau cas), ce qui viole le principe Open/Closed. Cette limite sera
 * corrigée dans le mini-projet suivant.
 */
public class HashCrackerFactory {

    private static final String BRUTE_FORCE_METHOD = "BRUTE";
    private static final String DICTIONARY_METHOD = "DICO";

    // Constructeur privé : cette classe n'a pas vocation à être
    // instanciée, elle n'expose que des méthodes statiques.
    private HashCrackerFactory() {
    }

    /**
     * Crée la stratégie de cassage correspondant à la méthode demandée.
     *
     * @param method "BRUTE" pour une attaque par force brute,
     *               "DICO" pour une attaque par dictionnaire
     *               (insensible à la casse)
     * @return une instance de {@link HashCracker} prête à l'emploi
     * @throws IllegalArgumentException si la méthode n'est pas reconnue
     */
    public static HashCracker create(String method) {
        if (method == null) {
            throw new IllegalArgumentException("La méthode de cassage ne peut pas être null");
        }

        switch (method.toUpperCase()) {
            case BRUTE_FORCE_METHOD:
                return new BruteForceHashCracker();
            case DICTIONARY_METHOD:
                return new DictionaryHashCracker();
            default:
                throw new IllegalArgumentException(
                        "Méthode de cassage inconnue : " + method
                                + " (attendu : " + BRUTE_FORCE_METHOD + " ou " + DICTIONARY_METHOD + ")");
        }
    }
}
