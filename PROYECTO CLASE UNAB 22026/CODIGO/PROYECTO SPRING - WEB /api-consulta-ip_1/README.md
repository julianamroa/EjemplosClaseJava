# api-consulta-ip

API sencilla en Java (Spring Boot) que recibe peticiones y, por dentro, hace
una llamada HTTP hacia una IP configurable (por ejemplo un servicio interno,
otra API, o un servidor cualquiera), y devuelve el resultado.

Pensada para desplegarse en **AWS EC2** de la forma más económica posible:
una sola instancia (t4g.micro o t3.micro), sin Load Balancer ni Elastic
Beanstalk.

## Estructura del proyecto

```
api-consulta-ip/
├── pom.xml
├── src/main/java/com/silvia/apiconsultaip/
│   ├── ApiConsultaIpApplication.java      -> clase principal
│   ├── controller/ConsultaController.java -> endpoints /api/info y /api/consulta
│   ├── service/ConsultaService.java       -> hace la llamada HTTP real
│   ├── config/TargetProperties.java       -> lee TARGET_IP/PORT/SCHEME
│   └── config/RestClientConfig.java       -> configura el cliente HTTP y timeouts
├── src/main/resources/application.properties
├── src/test/java/.../ApiConsultaIpApplicationTests.java
├── Dockerfile                              -> opcional, si quieres correrlo en contenedor
└── deploy/
    ├── api-consulta-ip.service             -> unidad systemd para EC2
    └── ec2-setup.sh                         -> script que instala Java y deja el servicio corriendo
```

## 0. Si Maven/tu IDE dice "Dependency ... not found" para TODAS las dependencias

Si ves algo como `Dependency 'org.springframework.boot:spring-boot-starter-web:' not found`
(sin numero de version) para varias o todas las dependencias a la vez, casi
siempre significa que Maven no pudo descargar el **POM padre**
(`spring-boot-starter-parent`), del cual salen las versiones de todo lo
demas (incluido Lombok). No es un problema del proyecto en si, sino de que
Maven no esta llegando a Maven Central. Pasos para diagnosticarlo:

1. **Corre el build por terminal, no solo desde el IDE**, para ver el error real:
   ```bash
   mvn -U clean package
   ```
   El `-U` fuerza a Maven a intentar de nuevo aunque haya quedado un intento
   fallido en cache. El mensaje de error de la terminal (por ejemplo un
   timeout, un 403, o "Non-resolvable parent POM") te dice la causa real,
   que el IDE a veces no muestra completa.

2. **Revisa si tienes "Work offline" activado** (muy comun en IntelliJ):
   `Settings/Preferences → Build, Execution, Deployment → Build Tools →
   Maven` → asegurate de que **"Work offline" este desmarcado**.

3. **Revisa tu conexion / red institucional**: si estas en una red de
   oficina, universidad o VPN, puede estar bloqueando
   `repo.maven.apache.org` o `repo1.maven.org`. Prueba abrir
   `https://repo1.maven.org/maven2/` directamente en el navegador. Si no
   carga, es un tema de red/firewall, no del proyecto.

4. **Revisa `~/.m2/settings.xml`** (en tu carpeta de usuario): si alguna
   vez configuraste un proxy o un repositorio espejo (mirror) ahi, puede
   estar mal apuntado y bloqueando el acceso a Maven Central.

5. **Borra la cache local de ese artefacto** por si quedo un intento de
   descarga fallido guardado, y vuelve a compilar:
   ```bash
   rm -rf ~/.m2/repository/org/springframework
   rm -rf ~/.m2/repository/org/projectlombok
   mvn -U clean package
   ```

6. En IntelliJ, si el `pom.xml` cambio (por ejemplo, si agregaste Lombok),
   fuerza el re-import: panel de Maven (lado derecho) → icono de
   "Reload All Maven Projects" (♻), o clic derecho sobre `pom.xml` →
   `Maven → Reload Project`.

Si despues de esto sigue sin funcionar, comparte el error completo que te
da `mvn -U clean package` en la terminal y lo revisamos con ese detalle.

## 0.1 Lombok, especificamente

Este proyecto ya trae la dependencia de Lombok en el `pom.xml` (bajo
`spring-boot-starter-actuator`) y las clases `TargetProperties` y
`ConsultaResponse` usan `@Getter`/`@Setter` de Lombok para generar los
getters y setters automaticamente. Si usas **IntelliJ IDEA**, ademas de
que Maven descargue la dependencia, necesitas:

1. Instalar el plugin **Lombok** (`Settings → Plugins → Marketplace →
   buscar "Lombok" → Install`), y reiniciar el IDE si lo pide.
2. Habilitar el procesamiento de anotaciones: `Settings → Build,
   Execution, Deployment → Compiler → Annotation Processors` → marcar
   **"Enable annotation processing"**.

Sin esos dos pasos en IntelliJ, aunque Maven descargue Lombok
correctamente, el IDE puede seguir marcando en rojo los getters/setters
generados (por ejemplo `getIp()`, `isExito()`) con "cannot find symbol".

## 1. Compilar el proyecto

Necesitas Java 17+ y Maven instalados en tu computador (con acceso normal a
internet, para bajar las dependencias de Spring Boot):

```bash
mvn clean package
```

Esto genera `target/api-consulta-ip.jar`.

> Nota: este build no se pudo ejecutar de extremo a extremo dentro del
> entorno donde armé el proyecto porque no tiene acceso a Maven Central.
> Revisé el código a mano (compilación de sintaxis con `javac`, sin las
> dependencias) y no hay errores de sintaxis; al correr `mvn clean package`
> en tu maquina o en la instancia EC2, con internet normal, deberia
> compilar sin problema. Si algo falla, compárteme el error y lo ajustamos.

## 2. Probarlo localmente (opcional, antes de subirlo a AWS)

```bash
export TARGET_IP=203.0.113.10      # la IP que quieres consultar
export TARGET_PORT=80
export TARGET_SCHEME=http
java -jar target/api-consulta-ip.jar
```

Luego:

```bash
curl "http://localhost:8080/api/info"
curl "http://localhost:8080/api/consulta?path=/estado"
curl -X POST "http://localhost:8080/api/consulta?path=/procesar" \
     -H "Content-Type: application/json" \
     -d '{"algo":"valor"}'
```

`path` es la ruta dentro del servicio remoto (en la IP configurada) que
quieres llamar. Todos los demás query params se reenvían tal cual.

## 3. Desplegar en AWS EC2 (la opción más económica)

### 3.1 Crear la instancia

1. Consola AWS → EC2 → "Launch instance".
2. Tipo de instancia: **t4g.micro** (Graviton/ARM, la más barata) o
   **t3.micro** si prefieres x86. Ambas caben en la capa gratuita de 750
   horas/mes durante los primeros 12 meses de la cuenta.
3. AMI: Amazon Linux 2023 (arm64 si elegiste t4g, x86_64 si elegiste t3).
4. Crea o reutiliza un par de llaves (.pem) para conectarte por SSH.
5. Security Group: abre el puerto **22** (SSH, solo desde tu IP) y el
   puerto **8080** (o el que uses) desde donde vaya a llegar el tráfico a
   la API.
6. **No** agregues Load Balancer ni Auto Scaling Group — eso es lo que
   sube el costo. Una sola instancia es suficiente para algo sencillo.
7. Lanza la instancia y anota su IP pública.

### 3.2 Subir el jar y desplegar

Desde tu computador, en la carpeta del proyecto:

```bash
scp -i tu-llave.pem target/api-consulta-ip.jar        ec2-user@TU_IP_EC2:/tmp/
scp -i tu-llave.pem deploy/api-consulta-ip.service     ec2-user@TU_IP_EC2:/tmp/
scp -i tu-llave.pem deploy/ec2-setup.sh                ec2-user@TU_IP_EC2:/tmp/

ssh -i tu-llave.pem ec2-user@TU_IP_EC2
sudo bash /tmp/ec2-setup.sh
```

El script instala Java 17, copia el jar a `/opt/api-consulta-ip/`, te
pausa para que edites `TARGET_IP` / `TARGET_PORT` / `TARGET_SCHEME` en
`/etc/systemd/system/api-consulta-ip.service`, y arranca el servicio con
`systemd` (se reinicia solo si la instancia reinicia o si la app se cae).

### 3.3 Probar desde afuera

```bash
curl http://TU_IP_EC2:8080/api/info
curl "http://TU_IP_EC2:8080/api/consulta?path=/estado"
```

### 3.4 Actualizar la app despues de un cambio

```bash
mvn clean package
scp -i tu-llave.pem target/api-consulta-ip.jar ec2-user@TU_IP_EC2:/tmp/
ssh -i tu-llave.pem ec2-user@TU_IP_EC2 \
    "sudo cp /tmp/api-consulta-ip.jar /opt/api-consulta-ip/api-consulta-ip.jar && sudo systemctl restart api-consulta-ip"
```

## 4. Alternativa con Docker (opcional)

Si en la instancia EC2 prefieres correrlo en contenedor en lugar de
systemd:

```bash
docker build -t api-consulta-ip .
docker run -d -p 8080:8080 \
  -e TARGET_IP=203.0.113.10 -e TARGET_PORT=80 -e TARGET_SCHEME=http \
  --name api-consulta-ip api-consulta-ip
```

## 5. Costos estimados (region us-east-1, referencia)

- **Con capa gratuita de 12 meses (cuenta nueva):** $0/mes (750 horas de
  t2.micro/t3.micro + 30 GB de EBS gratis).
- **Sin capa gratuita**, instancia unica 24/7 todo el mes:
  - t4g.micro: ~$6 USD/mes
  - t3.micro: ~$7.5 USD/mes
  - EBS (8-10 GB gp3): ~$0.70-1 USD/mes
  - Transferencia de datos saliente: normalmente gratis hasta ~100 GB/mes
  - **Total aproximado: $7-9 USD/mes**
- Evita Elastic Beanstalk con balanceador o un ALB aparte: eso agrega
  ~$16 USD/mes extra que no necesitas para un API sencillo de una sola
  instancia.
- Si mas adelante quieres bajar el costo a casi $0 incluso sin capa
  gratuita, la alternativa es migrar a **AWS Lambda + Function URL**
  (capa gratuita permanente de 1M peticiones/mes), pero requiere adaptar
  el arranque de Spring Boot para Lambda.

## 6. Seguridad basica a tener en cuenta

- Restringe el Security Group: el puerto 22 solo desde tu IP, y el 8080
  solo desde donde realmente necesites que llegue trafico.
- Si la API va a recibir trafico publico real (no solo pruebas), considera
  poner Nginx o un Application Load Balancer con HTTPS delante mas
  adelante; para pruebas o uso interno, HTTP directo al puerto 8080 basta.
- No dejes credenciales ni datos sensibles en `application.properties`;
  usa variables de entorno (como ya está armado con `TARGET_IP`, etc).
