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

```
   
```


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

| Commande | Sortie |
|---|---|
| `-m DICO -h 098f6bcd4621d373cade4e832627b4f6` | `Password found: test` |
| `-m DICO -h 5f4dcc3b5aa765d61d8327deb882cf99` | `Password found: password` |
| `-m BRUTE -h e2fc714c4727ee9395f324cd2e7f331f` | `Password found: abcd` |
| `-m BRUTE -h 5f4dcc3b5aa765d61d8327deb882cf99` | `Password not found` *(mot absent de l'espace de recherche a-z, 4 caractères max)* |

### Observations de performance

En rejouant l'algorithme sur les mêmes hashes, on observe l'écart attendu entre les deux stratégies :

- **DICO** : de l'ordre de quelques tentatives seulement (le dictionnaire ne contient que 20 mots), donc quasi instantané.
- **BRUTE** : jusqu'à 475 254 combinaisons testées pour parcourir tout l'espace `a-z` de longueur 1 à 4 lorsque le mot n'y figure pas, avec un temps d'exécution qui croît fortement avec la longueur du mot recherché.


### Vidéo de démonstration


## 7. Difficultés rencontrées

- **Choix de l'espace de recherche en force brute** : limiter l'alphabet à `a-z` et la longueur à 4 caractères était nécessaire pour garder un temps d'exécution raisonnable, au prix de ne pas couvrir les mots de passe contenant majuscules, chiffres ou symboles.
- **Éviter la duplication de code** : la logique de hachage et de comparaison étant nécessaire dans les deux stratégies, elle a été extraite dans `Md5Util` pour respecter le principe DRY.
- **Limite du Simple Factory** : ajouter une nouvelle stratégie de cassage impose de modifier `HashCrackerFactory` (nouveau `case`), ce qui viole le principe Open/Closed — limite assumée pour cette version et qui sera corrigée dans le mini-projet suivant (probablement via un Factory Method ou une fabrique enregistrable).

## 8. Conclusion

Ce mini-projet a permis de mettre en pratique le patron **Simple Factory** dans un contexte concret : centraliser la création d'objets `HashCracker` derrière une interface commune, tout en gardant le code appelant (`Main`) totalement découplé des implémentations concrètes. Les deux stratégies (dictionnaire et force brute) illustrent bien le compromis rapidité/exhaustivité propre au cassage de mots de passe. La principale limite identifiée — la violation du principe Open/Closed par la fabrique simple — servira de point de départ au mini-projet suivant.

---

## Auteurs

Projet réalisé en groupe par :

- Habib CISSE
- Abdourahmane FAYE
- Mouhamed BOUSSO
- Moustapha NGUERANE
