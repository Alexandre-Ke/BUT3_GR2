# Utilisation de l'image officielle de Tomcat avec Java 11
FROM tomcat:9.0-jdk11-openjdk-slim AS build

# Mise à jour des paquets et installation de Maven (si nécessaire pour la construction)
RUN apt-get update && apt-get install -y maven

# Répertoire de travail pour la construction du WAR
WORKDIR /app

# Copie des fichiers de définition du projet Maven
COPY pom.xml .
# Résolution des dépendances Maven
RUN mvn -B -f pom.xml dependency:resolve

# Copie des sources du projet
COPY src ./src
# Construction du projet sans exécution des tests
RUN mvn clean package -DskipTests

# Nouvelle étape pour réduire la taille de l'image finale
FROM tomcat:9.0-jdk11-openjdk-slim

# Copie du fichier WAR construit dans l'image précédente vers le dossier de déploiement de Tomcat
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/

# Exposition du port 8080 pour accéder à l'application
EXPOSE 8080

# Commande par défaut pour démarrer Tomcat
CMD ["catalina.sh", "run"]
