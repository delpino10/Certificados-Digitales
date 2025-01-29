package JAdelPino;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

/**
 * Esta clase implementa un cliente TLS que se conecta a un servidor seguro,
 * importa el certificado del servidor y se comunica con él.
 */
public class ClienteTLS {

    private static final String KEYSTORE_FILE = "cliente.jks";
    private static final String KEYSTORE_PASSWORD = "cambiarclave";
    private static final String CERT_FILE = "servidor.cer";
    private static final int PUERTO = 8443;

    /**
     * Método principal que importa el certificado del servidor, inicializa
     * el cliente TLS y establece una comunicación con el servidor.
     *
     * @param args los argumentos de línea de comandos
     */
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

    /**
     * Importa el certificado del servidor y lo guarda en el almacén de claves del cliente.
     *
     * @throws Exception si ocurre un error al cargar o guardar el certificado
     */
    private static void importarCertificado() throws Exception {
        // Crear un nuevo KeyStore vacío
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(null, KEYSTORE_PASSWORD.toCharArray());

        // Cargar el certificado del servidor
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        try (FileInputStream fis = new FileInputStream(CERT_FILE)) {
            Certificate cert = cf.generateCertificate(fis);
            ks.setCertificateEntry("servidor", cert);
        }

        // Almacenar el certificado en el KeyStore del cliente
        try (FileOutputStream fos = new FileOutputStream(KEYSTORE_FILE)) {
            ks.store(fos, KEYSTORE_PASSWORD.toCharArray());
        }

        System.out.println("✅ Certificado importado en 'cliente.jks'.");
    }

    /**
     * Inicializa una conexión TLS con el servidor usando el almacén de claves del cliente
     * y el certificado importado del servidor.
     *
     * @return un socket SSL conectado al servidor en el puerto configurado
     * @throws Exception si ocurre un error al configurar el cliente TLS
     */
    private static SSLSocket inicializarClienteTLS() throws Exception {
        // Cargar el almacén de claves del cliente
        KeyStore ks = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(KEYSTORE_FILE)) {
            ks.load(fis, KEYSTORE_PASSWORD.toCharArray());
        }

        // Inicializar el TrustManagerFactory con el almacén de claves
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);

        // Inicializar el contexto SSL con el TrustManager
        SSLContext contextoSSL = SSLContext.getInstance("TLS");
        contextoSSL.init(null, tmf.getTrustManagers(), null);

        // Crear y devolver el socket SSL
        SSLSocketFactory fabricaSocket = contextoSSL.getSocketFactory();
        return (SSLSocket) fabricaSocket.createSocket("localhost", PUERTO);
    }

    /**
     * Establece la comunicación con el servidor enviando un mensaje y recibiendo la respuesta.
     *
     * @param socket el socket SSL para la comunicación
     * @throws IOException si ocurre un error al leer o escribir datos
     */
    private static void comunicarConServidor(SSLSocket socket) throws IOException {
        try (PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("✅ Conexión establecida con el servidor.");
            salida.println("Hola desde el cliente");
            System.out.println("🔄 Respuesta del servidor: " + entrada.readLine());
        } finally {
            // Cerrar el socket después de la comunicación
            socket.close();
        }
    }
}
