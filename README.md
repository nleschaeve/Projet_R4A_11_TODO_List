# Projet_R4A_11_TODO_List LESCHAEVE--LEMAIRE Nathaniel

# Diagramme de classes

<img width="621" height="790" alt="Diagramme de classes TODO Kotlin" src="https://github.com/user-attachments/assets/94486059-8798-470c-97d8-d07c16dd1a37" />

## Principes SOLID : 
La conception du diagramme de classes repose sur les principes SOLID afin de garantir une architecture modulaire, maintenable et évolutive.  
Le principe de responsabilité unique (SRP) est respecté en séparant le modèle de données (Task) de la logique métier (TaskStatusService) et de l’orchestration (TaskManager).  
Bien que l'utilisation d'une énumération implique une modification du code lors de l'ajout d'un nouvel état, ce choix reste conforme au principe Open/Close (OCP) dans la mesure où les états de tâche représentent un domaine fonctionnel fermé et stable. L'utilisation de l'énumération constitue un compromis volontaire entre respect théorique du principe OCP et simplicité du modèle métier.  
Le principe d’inversion de dépendance (DIP) est appliqué grâce à l’utilisation de l’interface TaskRepository, permettant au gestionnaire de tâches de dépendre d’une abstraction plutôt que d’une implémentation concrète.  
Cette approche facilite les tests unitaires, l’évolution de la source de données (base locale ou distante) et limite les impacts lors des modifications.

## TaskRepositoryImpl : 
Cette classe est l’implémentation concrète de l’interface TaskRepository. 
Elle encapsule les détails techniques liés à l’accès aux données, permettant ainsi au reste de l’application de dépendre uniquement d’une abstraction.
Cette approche respecte le principe d’inversion de dépendance (DIP) et facilite la maintenabilité ainsi que les tests unitaires.

    TaskManager
    	↓ dépend de
    TaskRepository (interface)
    	↑ implémentée par
    TaskRepositoryImpl (classe concrète)

On ne met pas la logique directement dans TaskManager car c’est une mauvaise pratique qui implique un couplage fort, une impossibilité à tester facilement et une architecture rigide.

# Stockage des données

## Comparatif
### SharedPreferences / DataStore : 
Sert à stocker des objets simples tels que des strings, des ints, ou des booléens.  
C'est très simple et rapide, mas non adapté à des objets complexes.  

### Fichiers locaux (JSON / XML): 
Sert à stocker des objets Kotlin, des listes, des structures personnalisées dans un fichier.  
C'est simple, lisible, et ça n'utilise pas de framework lourds. Mais, on ne peut pas faire de requêtes (tri, filtre, recherche lente), il y a un risque de corruption et ce n'est pas scalable.  

### SQLLite (Manuel) : 
Sert à stocker des tables, des relations et des index dans une base de données relationnelle locale.  
C'est très performant, le contrôle est local et c'est un standard Android. Mais c'est verbeux, et il y a des erreurs SQL fréquentes.  
C'est techniquement solide mais dépassé seul aujourd'hui.

### Room : 
C'est une surcouche moderne de SQLite (officielle Google) qui peut stoquer des objets Kotlin, des relations et des requêtes SQL sécurisées.  
C'est typé et sécurisé, très prorpe et parfait pour le CRUD (Create, Read, Update, Delete).

### Base de données distante : 
Les données sont stockées sur un serveur. On peut y faire de la synchronisation cloud et du multi-utilisateur.  
Une connexion est requise, c'est plus complexe et exagéré pour une to-do list.

## Solution retenue : Room
Room fournit une couche d’abstraction sur SQLite afin de permettre un accès fluide à la base de données, tout en exploitant toute la puissance de SQLite. Il offre en particulier les avantages suivants :  
- Une vérification des requêtes SQL au moment de la compilation  
- Des annotations pratiques qui réduisent le code récurrent qui peut s’avérer répétitif et être sujet aux erreurs  
- Des chemins de migration simplifiés pour les bases de données  

Pour utiliser Room dans une application, il faut ajouter les dépendances ksp.

Composants principaux : 
Room repose sur trois composants principaux :  
- La classe de base de données qui contient la base de données et sert de point d’accès principal pour la connexion sous-jacente aux données persistantes de l’application  
- Les entités de données qui représentent les tables de la base de données de l’application  
- Les objets d’accès aux données (DAO) qui fournissent des méthodes que l’application peut utiliser pour interroger, mettre à jour, insérer et supprimer des données dans la base de données

La classe de base de données fournit à l’application des instances des DAO associés à cette base de données. L’application peut ensuite utiliser ces DAO pour récupérer des données de la base de données en tant qu’instances des objets d’entité associés. L’application peut également utiliser les entités de données définies pour mettre à jour des lignes dans les tables correspondantes ou pour créer des lignes à insérer.
