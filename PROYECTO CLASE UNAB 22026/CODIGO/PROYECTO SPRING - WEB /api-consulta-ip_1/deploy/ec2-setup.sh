#!/usr/bin/env bash
# ==========================================================================
# Script para preparar una instancia EC2 (Amazon Linux 2023) para correr
# api-consulta-ip.jar como servicio systemd.
#
# Uso (dentro de la instancia EC2, por SSH):
#   sudo bash ec2-setup.sh
#
# Antes de correrlo, debes haber subido el jar y este archivo .service a
# la instancia, por ejemplo con scp desde tu computador:
#
#   scp -i tu-llave.pem target/api-consulta-ip.jar ec2-user@TU_IP_EC2:/tmp/
#   scp -i tu-llave.pem deploy/api-consulta-ip.service ec2-user@TU_IP_EC2:/tmp/
#   scp -i tu-llave.pem deploy/ec2-setup.sh ec2-user@TU_IP_EC2:/tmp/
#   ssh -i tu-llave.pem ec2-user@TU_IP_EC2
#   sudo bash /tmp/ec2-setup.sh
# ==========================================================================
set -euo pipefail

APP_DIR="/opt/api-consulta-ip"

echo "==> Instalando Java 17 (Amazon Corretto)..."
if command -v dnf >/dev/null 2>&1; then
    dnf install -y java-17-amazon-corretto
elif command -v yum >/dev/null 2>&1; then
    yum install -y java-17-amazon-corretto
else
    echo "No se encontro dnf ni yum. Instala Java 17 manualmente." >&2
    exit 1
fi

echo "==> Creando directorio de la aplicacion en ${APP_DIR}..."
mkdir -p "${APP_DIR}"

echo "==> Copiando el jar y el archivo de servicio..."
cp /tmp/api-consulta-ip.jar "${APP_DIR}/api-consulta-ip.jar"
cp /tmp/api-consulta-ip.service /etc/systemd/system/api-consulta-ip.service

echo "==> IMPORTANTE: edita /etc/systemd/system/api-consulta-ip.service"
echo "    y ajusta TARGET_IP / TARGET_PORT / TARGET_SCHEME antes de continuar."
read -p "Presiona Enter cuando ya lo hayas editado (o Ctrl+C para hacerlo ahora)... " _

echo "==> Habilitando e iniciando el servicio..."
systemctl daemon-reload
systemctl enable api-consulta-ip
systemctl restart api-consulta-ip

echo "==> Estado del servicio:"
systemctl status api-consulta-ip --no-pager || true

echo ""
echo "Listo. Prueba desde tu computador (ajusta la IP publica de tu EC2):"
echo "  curl http://TU_IP_EC2:8080/api/info"
echo ""
echo "Recuerda abrir el puerto 8080 (o el que uses) en el Security Group de la instancia."
