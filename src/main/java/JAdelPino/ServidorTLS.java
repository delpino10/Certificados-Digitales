package JAdelPino;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class ServidorTLS {

    private static final String KEYSTORE_FILE = "servidor.jks";
    private static final String KEYSTORE_PASSWORD = "cambiarclave";
    private static final String KEY_ALIAS = "servidor";
    private static final int PUERTO = 8443;

    public static void main(String[] args) {
        try {
            // Inicializar el servidor TLS
            SSLServerSocket servidorSocket = inicializarServidorTLS();
            System.out.println("Servidor TLS en espera de conexiones...");

            while (true) {
                try (SSLSocket socket = (SSLSocket) servidorSocket.accept()) {
                    manejarConexionCliente(socket);
                } catch (IOException e) {
                    System.err.println("Error en la conexión del cliente: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error crítico en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static SSLServerSocket inicializarServidorTLS() throws Exception {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(KEYSTORE_FILE)) {
            ks.load(fis, KEYSTORE_PASSWORD.toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, KEYSTORE_PASSWORD.toCharArray());

        SSLContext contextoSSL = SSLContext.getInstance("TLS");
        contextoSSL.init(kmf.getKeyManagers(), null, null);

        SSLServerSocketFactory fabricaServidorSocket = contextoSSL.getServerSocketFactory();
        return (SSLServerSocket) fabricaServidorSocket.createServerSocket(PUERTO);
    }

    private static void manejarConexionCliente(SSLSocket socket) throws IOException {
        System.out.println("Cliente conectado: " + socket.getInetAddress());

        try (BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)) {

            String linea;
            while ((linea = entrada.readLine()) != null) {
                System.out.println("Recibido: " + linea);
                salida.println("Eco: " + linea);
            }
        }
    }
}

