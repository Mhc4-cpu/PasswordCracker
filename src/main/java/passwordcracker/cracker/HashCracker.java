package passwordcracker.cracker;

/**
 * Contrat commun à toutes les stratégies de cassage de hash.
 *
 * Chaque implémentation représente une méthode différente pour retrouver
 * un mot de passe en clair à partir de son empreinte MD5 (dictionnaire,
 * force brute, etc.). Le programme principal ne manipule jamais les
 * classes concrètes directement : il ne connaît que ce contrat, ce qui
 * permet d'ajouter de nouvelles stratégies sans modifier le code qui
 * les utilise (polymorphisme).
 */
public interface HashCracker {

    /**
     * Tente de retrouver le mot de passe en clair correspondant au hash
     * MD5 fourni.
     *
     * @param hash le hash MD5 (en hexadécimal) à casser
     * @return le mot de passe trouvé, ou {@code null} si aucune
     *         correspondance n'a été trouvée
     */
    String crack(String hash);
}
