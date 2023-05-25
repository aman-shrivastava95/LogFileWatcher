package service;

import javax.websocket.Session;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface FileLogService {
    public void tailf(String filePath, int linesToShow, List<Session> session) throws IOException;

}
