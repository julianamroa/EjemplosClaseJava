# Imagen para correr la API en un contenedor (opcional: si prefieres Docker
# en EC2 en lugar de correr el jar directamente con systemd).
#
# Build:  docker build -t api-consulta-ip .
# Run:    docker run -d -p 8080:8080 \
#           -e TARGET_IP=203.0.113.10 -e TARGET_PORT=80 -e TARGET_SCHEME=http \
#           --name api-consulta-ip api-consulta-ip

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/api-consulta-ip.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
