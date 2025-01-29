package JAdelPino;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

public class ClienteTLS {

    private static final String KEYSTORE_FILE = "cliente.jks";
    private static final String KEYSTORE_PASSWORD = "cambiarclave";
    private static final String CERT_FILE = "servidor.cer";
    private static final int PUERTO = 8443;

    public static void main(String[] args) {
        try {
            // Importar el certificado del servidor
            importarCertificado();

            // Inicializar cliente TLS
            SSLSocket socket = inicializarClienteTLS();
            comunicarConServidor(socket);
        } catch (Exception e) {
            System.err.println("Error crítico en el cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void importarCertificado() throws Exception {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(null, KEYSTORE_PASSWORD.toCharArray());

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        try (FileInputStream fis = new FileInputStream(CERT_FILE)) {
            Certificate cert = cf.generateCertificate(fis);
            ks.setCertificateEntry("servidor", cert);
        }

        try (FileOutputStream fos = new FileOutputStream(KEYSTORE_FILE)) {
            ks.store(fos, KEYSTORE_PASSWORD.toCharArray());
        }

        System.out.println("✅ Certificado importado en 'cliente.jks'.");
    }

    private static SSLSocket inicializarClienteTLS() throws Exception {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(KEYSTORE_FILE)) {
            ks.load(fis, KEYSTORE_PASSWORD.toCharArray());
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);

        SSLContext contextoSSL = SSLContext.getInstance("TLS");
        contextoSSL.init(null, tmf.getTrustManagers(), null);

        SSLSocketFactory fabricaSocket = contextoSSL.getSocketFactory();
        return (SSLSocket) fabricaSocket.createSocket("localhost", PUERTO);
    }

    private static void comunicarConServidor(SSLSocket socket) throws IOException {
        try (PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("✅ Conexión establecida con el servidor.");
            salida.println("Hola desde el cliente");
            System.out.println("🔄 Respuesta del servidor: " + entrada.readLine());
        } finally {
            socket.close();
        }
    }
}

