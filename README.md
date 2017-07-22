# Projet de démarrage

## Description

Ce projet est une application utilisant le framework Spring permettant d'importer sous format JSON es activites du 375e de Montreal et les stations Bixi et pistes cyclables du Quebec et les stockent dans la BD Postgres nommee mtl375.

L'application est s'execute localement sur le port :8080.

Differents REST API permettent de faire des recherches, insertions, mises a jour et suppressions des donnees stockees depuis la BD mtl375.

## Prérequis

- Java 1.8+
- Maven 3.0+
- PostgreSQL 9.6+ avec PostGIS 2.3+

## Configuration Postgresql 

L'application requiert l'utilisateur admin postgres avec le mot de passe postgres avec le mode d'authentification md5.

La configuration de postgres par defaut n'est pas md5, mais peer.

Il faut donc changer la configuration presente dans le fichier pg_hba.conf de postgresql a l'aide
d'un editeur de texte quelconque et attribuer un mot de passe a l'utilisateur postgres.

Étapes
------
1. A partir d'un shell Bash,

	$ sudo nano /etc/postgresql/9.6/main/pg_hba.conf

Ligne du fichier a modifier:
	local	all		postgres			peer
Remplacer par:
	local	all		postgres			trust

2. A partir d'un shell Bash,

	$ sudo service postgresql restart

Apres avoir modifie le fichier de configuration, il ne faut pas oublier de redemarrer le service postgresql.


3. A partir d'un shell Bash,

	$ psql -U postgres
	postgres=# ALTER USER postgres WITH PASSWORD 'postgres';
	postgres=# \q

Le mot de passe 'postgres' a ete attribue a l'utilisateur postgres.

4. A partir d'un shell Bash,

	$ sudo nano /etc/postgresql/9.6/main/pg_hba.conf

Ligne du fichier a modifier:
	local	all		postgres			trust
Remplacer par:
	local	all		postgres			md5

5. A partir d'un shell Bash,

	$ sudo service postgresql restart

Apres avoir modifie le fichier de configuration, il ne faut pas oublier de redemarrer le service postgresql. 

6. A partir d'un shell Bash et du repertoire MTL375 du projet,

	$ PGPASSWORD=postgres psql -U postgres -f src/main/resources/database/create-database.sql

Ceci permet de creer la base de donnees mtl375 et d'attribuer les permissions pour l'utilisateur postgres.

7. A partir d'un shell Bash et du repertoire MTL375 du projet,

	$ PGPASSWORD=postgres psql -U postgres -f src/main/resources/database/create-schema.sql

Ceci cree le schema de la BD ainsi que l'extension Postgis.

## Compilation et exécution

Pour compiler le projet, se placer dans le dossier du projet /MTL375 et effectuer la commande suivante dans un terminal:

	$ mvn compile

Pour executer le projet, se placer dans le dossier du projet /MTL375 et effectuer la comment suivante dans un terminal:

    	$ mvn spring-boot:run

Le projet est alors disponible à l'adresse [http://localhost:8080/](http://localhost:8080/)
