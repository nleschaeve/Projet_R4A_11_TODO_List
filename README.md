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

