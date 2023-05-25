package servers;

import handlers.SocketHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.javax.server.config.JavaxWebSocketServletContainerInitializer;

public class EmbeddedServer {
    private static EmbeddedServer INSTANCE ;

    private  Server server;
    private  ServerConnector connector;
    private EmbeddedServer(){

    }
    public static EmbeddedServer getInstance(){
        if(INSTANCE == null){
            INSTANCE = new EmbeddedServer() ;
            INSTANCE.initServer() ;
        }
        return INSTANCE ;
    }


    private void initServer() {
        server = new Server();
        connector = new ServerConnector(server);
        server.addConnector(connector);

        //configure handlers
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setContextPath("/");

        //configure web socket container
        JavaxWebSocketServletContainerInitializer.configure(contextHandler, ((servletContext, wsServerContainer) -> {
            wsServerContainer.addEndpoint(SocketHandler.class);
            wsServerContainer.setDefaultMaxTextMessageBufferSize(65535);
        }));

        //configure resource handler
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase("src/main/resources");

        HandlerList handlerList = new HandlerList();
        handlerList.setHandlers(new Handler[]{resourceHandler, contextHandler});
        server.setHandler(handlerList);
    }

    public void setPort(int port) {
        connector.setPort(port);
    }

    public void startServer() throws Exception {
        server.start();
    }

    public void stopSever() throws Exception {
        server.stop();
    }
}
