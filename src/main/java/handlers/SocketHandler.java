package handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.FileLogService;
import service.impl.FileLogServiceImpl;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@ServerEndpoint("/log")
public class SocketHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SocketHandler.class) ;
    FileLogService logService =  new FileLogServiceImpl() ;
    List<Session> sessions = new CopyOnWriteArrayList<>() ;

    @OnOpen
    public void onOpen(Session session) throws IOException {
        sessions.add(session) ;
        LOG.info(session.getId() + "Added to active session");
        logService.tailf("src/main/resources/log.txt",4, sessions);
    }

    @OnMessage
    public void onMessage(Session session, String message){
        LOG.info("received message:" +message + "from: " + session.getId() );
    }

    @OnError
    public void onError(Throwable cause){
        LOG.error(cause.getMessage());
    }

    @OnClose
    public void onClose(Session session, CloseReason reason){
        LOG.info(reason.getReasonPhrase());
        sessions.remove(session) ;
    }
}
