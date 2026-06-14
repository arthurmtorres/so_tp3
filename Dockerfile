# Estágio 1: Build (Compila o código usando o Maven)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# Baixa as dependências primeiro para aproveitar o cache do Docker
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Execução (Imagem super leve só com o Java JRE)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copia o .jar gerado no estágio anterior
COPY --from=build /app/target/tp3-ipc-1.0.0.jar app.jar

# Expõe as portas exigidas (8080 para REST/GraphQL/WS e 50051 para gRPC)
EXPOSE 8080 50051

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
