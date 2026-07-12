# Mini-Projet 1 : PasswordCracker v1

Mise en œuvre du patron de création **Simple Factory** — cassage de mots de passe à partir de leur empreinte MD5.

## 1. Introduction

`PasswordCracker` est un outil en ligne de commande écrit en Java qui retrouve un mot de passe en clair à partir de son hash MD5, en simulant un audit de robustesse de mots de passe. Ce mini-projet avait pour objectif pédagogique de concevoir une architecture orientée objet modulaire, d'utiliser le polymorphisme, et de mettre en œuvre le patron de création **Simple Factory** pour centraliser l'instanciation des différentes stratégies de cassage.

## 2. Présentation du problème

En sécurité informatique, les mots de passe ne sont jamais stockés en clair : ils sont transformés par une fonction de hachage cryptographique (ici MD5), qui produit une empreinte fixe et théoriquement irréversible. Lors d'un audit, on cherche à évaluer la robustesse des mots de passe utilisés en tentant de retrouver leur valeur en clair à partir de ce hash.

Deux approches sont mises en œuvre :

- **Cassage par dictionnaire (`DICO`)** : on compare le hash cible au hash de chaque mot d'une liste de mots courants (`dictionary.txt`). Rapide, mais limité aux mots déjà présents dans le dictionnaire.
- **Cassage par force brute (`BRUTE`)** : on génère et teste systématiquement toutes les combinaisons possibles de 1 à 4 caractères sur l'alphabet `a-z`. Exhaustif, mais coûteux en temps à mesure que la longueur augmente.

## 3. Architecture

Le projet suit une architecture Maven standard, organisée en quatre responsabilités bien séparées :

```
src/main/java/passwordcracker/
├── Main.java                       # Point d'entrée CLI (parsing des arguments, affichage)
├── cracker/
│   ├── HashCracker.java            # Interface commune (contrat) aux stratégies
│   ├── DictionaryHashCracker.java  # Stratégie : cassage par dictionnaire
│   └── BruteForceHashCracker.java  # Stratégie : cassage par force brute
├── factory/
│   └── HashCrackerFactory.java     # Fabrique simple : centralise la création
└── util/
    └── Md5Util.java                # Utilitaire de hachage MD5 (calcul + comparaison)

src/main/resources/
└── dictionary.txt                  # Liste de mots pour l'attaque par dictionnaire

src/test/java/passwordcracker/util/
└── Md5UtilTest.java                # Tests unitaires (JUnit 5)
```

**Responsabilités des classes :**

| Classe | Rôle |
|---|---|
| `HashCracker` | Interface définissant le contrat `String crack(String hash)` que toute stratégie doit respecter. |
| `DictionaryHashCracker` | Implémente `HashCracker` ; parcourt `dictionary.txt` et compare chaque mot au hash cible via `Md5Util`. |
| `BruteForceHashCracker` | Implémente `HashCracker` ; génère récursivement toutes les combinaisons de l'alphabet `a-z` (longueur 1 à 4) et les compare au hash cible. |
| `Md5Util` | Classe utilitaire statique, sans état, qui centralise le calcul du hash MD5 et la comparaison — évite toute duplication de logique entre les deux stratégies. |
| `HashCrackerFactory` | Fabrique simple : seul point du programme où les classes concrètes (`DictionaryHashCracker`, `BruteForceHashCracker`) sont instanciées avec `new`. |
| `Main` | Parse les arguments (`-m`, `-h`), délègue la création de la stratégie à la fabrique, exécute le cassage et affiche le résultat ainsi que le temps d'exécution. |

## 4. Diagramme UML


  <img width="1111" height="981" alt="uml-diagram" src="https://github.com/user-attachments/assets/a12530ed-1373-4b15-b7f7-7b02e64f90fb" />

 Ce diagramme illustre le patron **Simple Factory** :
- `HashCracker` est l'interface commune (contrat) implémentée par les deux stratégies concrètes.
- `HashCrackerFactory` est le seul point du programme autorisé à instancier `DictionaryHashCracker` ou `BruteForceHashCracker`.
- `Main` ne dépend que de `HashCracker` et de la fabrique, jamais des classes concrètes directement.
- `Md5Util` est une classe utilitaire partagée par les deux stratégies pour éviter la duplication du code de hachage.


## 5. Usage du patron Simple Factory

Le patron **Simple Factory** est mis en œuvre dans `HashCrackerFactory`, qui expose une unique méthode statique `create(String method)` :

```java
public static HashCracker create(String method) {
    switch (method.toUpperCase()) {
        case "BRUTE": return new BruteForceHashCracker();
        case "DICO":  return new DictionaryHashCracker();
        default: throw new IllegalArgumentException("Méthode de cassage inconnue : " + method);
    }
}
```

`Main` ne connaît jamais les classes concrètes : il manipule uniquement le type `HashCracker`, obtenu via la fabrique :

```java
HashCracker cracker = HashCrackerFactory.create(method);
String result = cracker.crack(hash);
```

Ce découplage permet au code appelant de rester inchangé quelle que soit la stratégie réellement utilisée, grâce au polymorphisme.

## 6. Résultats obtenus

### Compilation et exécution

```bash
mvn clean package
java -jar target/passwordCracker.jar -m DICO -h 5f4dcc3b5aa765d61d8327deb882cf99
```

### Exemples d'exécution

**Capture 1 — Compilation du projet avec Maven**

On lance `mvn clean package` depuis la racine du projet. Maven télécharge les dépendances nécessaires (commons-codec pour le calcul MD5, etc.), compile les sources, exécute les tests, puis génère l'archive exécutable `target/passwordCracker.jar`. Le message `BUILD SUCCESS` confirme que la compilation s'est déroulée sans erreur.

**Capture 2 — Cassage par dictionnaire (mot trouvé)**

<img width="957" height="222" alt="Capture d’écran 2026-07-12 004844" src="https://github.com/user-attachments/assets/75f21f5a-dd37-4a41-b50c-6bfc3166cf8d" />

On lance le programme en mode DICO avec le hash du mot `password`. Le programme charge le dictionnaire, calcule le hash de chaque mot qu'il contient, et le compare au hash recherché. Dès qu'une correspondance est trouvée — ici `password` — il l'affiche. Le temps d'exécution est très court, 88 ms, car le dictionnaire ne contient qu'une vingtaine de mots à tester.

**Capture 3 — Cassage par force brute (mot trouvé)**

<img width="948" height="207" alt="Capture d’écran 2026-07-12 004902" src="https://github.com/user-attachments/assets/63f47f37-343f-4658-a884-98d0a30b67da" />

Cette fois, on teste le mode BRUTE avec le hash du mot `abcd`. Le programme génère systématiquement toutes les combinaisons possibles de lettres, de 1 à 4 caractères, dans l'ordre alphabétique, jusqu'à trouver une correspondance. Il retrouve `abcd` en 364 ms — plus long que le dictionnaire, car il doit tester beaucoup plus de combinaisons avant d'y arriver.

**Capture 4 — Force brute (mot non trouvé)**

<img width="957" height="176" alt="Capture d’écran 2026-07-12 004924" src="https://github.com/user-attachments/assets/ca330c4f-57e8-4746-9a74-11df2789d15c" />

Enfin, on relance une attaque BRUTE, mais cette fois avec le hash de `password`, qui contient 8 caractères. L'espace de recherche étant limité à 4 caractères maximum, le programme est obligé de tester l'intégralité des 475 254 combinaisons possibles avant de conclure qu'aucune correspondance n'existe dans cet espace. Résultat : `Password not found`, en 1257 ms. Cela illustre bien la limite principale de la force brute : plus le mot de passe est long, plus le temps de recherche explose de façon exponentielle.

### Vidéo de démonstration
https://drive.google.com/drive/u/1/folders/1Q0_OqnR03tjOx4KWURM5SrimnLTIOB6I


## 7. Difficultés rencontrées

- **Choix de l'espace de recherche en force brute** : limiter l'alphabet à `a-z` et la longueur à 4 caractères était nécessaire pour garder un temps d'exécution raisonnable, au prix de ne pas couvrir les mots de passe contenant majuscules, chiffres ou symboles.
- **Éviter la duplication de code** : la logique de hachage et de comparaison étant nécessaire dans les deux stratégies, elle a été extraite dans `Md5Util` pour respecter le principe DRY.
- **Limite du Simple Factory** : ajouter une nouvelle stratégie de cassage impose de modifier `HashCrackerFactory` (nouveau `case`), ce qui viole le principe Open/Closed — limite assumée pour cette version et qui sera corrigée dans le mini-projet suivant (probablement via un Factory Method ou une fabrique enregistrable).

## 8. Conclusion

Ce mini-projet a permis de mettre en pratique le patron **Simple Factory** dans un contexte concret : centraliser la création d'objets `HashCracker` derrière une interface commune, tout en gardant le code appelant (`Main`) totalement découplé des implémentations concrètes. Les deux stratégies (dictionnaire et force brute) illustrent bien le compromis rapidité/exhaustivité propre au cassage de mots de passe. La principale limite identifiée — la violation du principe Open/Closed par la fabrique simple — servira de point de départ au mini-projet suivant.

---

## Auteurs

Projet réalisé en groupe par :

- Mouhamadoul Habib CISSE
- Abdourahmane FAYE
- Mouhamed BOUSSO
- Moustapha NGUERANE
