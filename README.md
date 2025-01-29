# Generación y Configuración del Certificado en el Servidor y Cliente
## como técnica de programación segura

## Generar e Insertar un certificado en KeyStore del Servidor

### 1. Generar un keystore con un certificado autofirmado:
```bash
keytool -genkeypair -v -keystore servidor.jks -keyalg RSA -keysize 2048 -validity 3650 -alias servidor -dname "CN=localhost, OU=MiEmpresa, O=MiOrganizacion, L=Ciudad, ST=Provincia, C=ES" -storepass cambiarclave -keypass cambiarclave
```
### 2. Exportar el certificado público:
```bash
keytool -export -alias servidor -keystore servidor.jks -file servidor.cer -storepass cambiarclave
```
### 3. Verificar el contenido del certificado exportado:
```bash
keytool -printcert -file servidor.cer
```
### 4. Verificar los contenidos del keystore:
```bash
keytool -list -keystore servidor.jks -storepass cambiarclave
```
<br>


## Importar el Certificado del Servidor en el Keystore del Cliente

Para importar el certificado del servidor en el keystore del cliente, utiliza el siguiente comando:

```bash
keytool -import -alias servidor -keystore cliente.jks -file servidor.cer -storepass cambiarclave
```
