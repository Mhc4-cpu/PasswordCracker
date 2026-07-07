package passwordcracker;

import passwordcracker.cracker.HashCracker;
import passwordcracker.factory.HashCrackerFactory;


public class Main {

    public static void main(String[] args) {
        String method = null;
        String hash = null;

        // --- 1. Parsing des arguments ---
        for (int i = 0; i < args.length - 1; i++) {
            if ("-m".equals(args[i])) {
                method = args[i + 1];
            } else if ("-h".equals(args[i])) {
                hash = args[i + 1];
            }
        }

        if (method == null || hash == null) {
            printUsage();
            System.exit(1);
        }

        // --- 2. Création de la stratégie via la fabrique ---
        HashCracker cracker;
        try {
            cracker = HashCrackerFactory.create(method);
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
            printUsage();
            System.exit(1);
            return; // inatteignable, mais rassure le compilateur
        }

        // --- 3. Exécution de la stratégie (polymorphisme) ---
        long startTime = System.nanoTime();
        String result = cracker.crack(hash);
        long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;

        // --- 4. Affichage du résultat ---
        if (result != null) {
            System.out.println("Password found: " + result);
        } else {
            System.out.println("Password not found");
        }
        System.out.println("Méthode utilisée : " + method.toUpperCase());
        System.out.println("Temps d'exécution : " + elapsedMs + " ms");
    }

    private static void printUsage() {
        System.out.println("Usage : passwordCracker -m <BRUTE|DICO> -h <hashMD5>");
        System.out.println("Exemple : passwordCracker -m DICO -h 098f6bcd4621d373cade4e832627b4f6");
    }
}
