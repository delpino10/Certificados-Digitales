package JAdelPino;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;

/**
 * Esta clase implementa un servidor TLS que acepta conexiones seguras
 * a través del puerto 8443 y responde con un mensaje de eco.
 */
public class ServidorTLS {

    private static final String KEYSTORE_FILE = "servidor.jks";
    private static final String KEYSTORE_PASSWORD = "cambiarclave";
    private static final String KEY_ALIAS = "servidor";
    private static final int PUERTO = 8443;

    /**
     * Método principal que inicializa el servidor TLS y espera conexiones de clientes.
     *
     * @param args los argumentos de línea de comandos
     */
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

    /**
     * Inicializa el servidor TLS configurando el almacén de claves y el contexto SSL.
     *
     * @return un servidor SSL que escucha en el puerto configurado
     * @throws Exception si ocurre algún error al inicializar el servidor TLS
     */
    private static SSLServerSocket inicializarServidorTLS() throws Exception {
        // Cargar el almacén de claves (keystore)
        KeyStore ks = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(KEYSTORE_FILE)) {
            ks.load(fis, KEYSTORE_PASSWORD.toCharArray());
        }

        // Inicializar el KeyManagerFactory
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, KEYSTORE_PASSWORD.toCharArray());

        // Inicializar el contexto SSL
        SSLContext contextoSSL = SSLContext.getInstance("TLS");
        contextoSSL.init(kmf.getKeyManagers(), null, null);

        // Crear el servidor SSL
        SSLServerSocketFactory fabricaServidorSocket = contextoSSL.getServerSocketFactory();
        return (SSLServerSocket) fabricaServidorSocket.createServerSocket(PUERTO);
    }

    /**
     * Maneja la conexión con un cliente, leyendo datos y enviando un mensaje de eco.
     *
     * @param socket la conexión segura con el cliente
     * @throws IOException si ocurre un error al leer o escribir datos
     */
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
