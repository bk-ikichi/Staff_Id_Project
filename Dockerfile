# --------------------------------------------------
# Build Stage
# --------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# pom.xmlとソースコードをコピーしてビルドを実行
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# --------------------------------------------------
# Run Stage
# --------------------------------------------------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# ビルドステージからjarファイルをコピー
COPY --from=build /app/target/*.jar app.jar

# ポートの開放
EXPOSE 8080

# アプリケーション起動（UTF-8エンコーディングを強制）
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]
