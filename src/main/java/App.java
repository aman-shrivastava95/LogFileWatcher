import servers.EmbeddedServer;

public class App {
    public static void main(String[] args) throws Exception {
        final EmbeddedServer server = EmbeddedServer.getInstance();
        server.setPort(8080);
        server.startServer();
    }
}
