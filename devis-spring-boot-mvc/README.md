# Application Spring Boot - Gestion de Devis et Factures

Application web MVC développée avec **Spring Boot 3**, **Java 21**, **Spring Data JPA**, **Thymeleaf**, **Lombok** et une base de données locale **SQLite** (`devis.db`),
dans le contexte du cours de eBusiness pour bacheliers en informatique comme découverte de l'outil gravity.


## Fonctionnalités

- **Gestion des Devis & Factures clients** :
  - Nom du client
  - Date d'émission & date d'échéance
  - Montant total (€)
  - Statut (Brouillon, Envoyé, Accepté, Refusé / En attente, Payée, En retard, Annulée)
- **Tableau de bord interactif** : indicateurs clés (chiffre d'affaires facturé, montant encaissé, montants en attente, devis en cours).
- **Filtrage et recherche** : recherche en temps réel par nom de client ou référence et filtre par statut.
- **Conversion en un clic** : transformation d'un devis accepté en facture client.
- **Vue imprimable** : mise en page soignée pour l'impression ou l'export PDF.

## Configuration SQLite

La configuration dans `src/main/resources/application.properties` :
```properties
spring.datasource.url=jdbc:sqlite:devis.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=update
spring.datasource.hikari.maximum-pool-size=1
```

## Lancement

Pour démarrer l'application :

```bash
mvn spring-boot:run
```

L'application est ensuite accessible sur : [http://localhost:8080](http://localhost:8080)
