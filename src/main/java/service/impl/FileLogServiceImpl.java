package service.impl;

import service.FileLogService;


import javax.websocket.Session;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class FileLogServiceImpl implements FileLogService {

    @Override
    public void tailf(String filePath, int linesToShow, List<Session> sessions) throws IOException {
        List<String> lines = new ArrayList<>();
        RandomAccessFile raf = new RandomAccessFile(filePath, "r");
        //['l','i','n','e',' ', '1', '\n' , 'l','i','n','e', ' ','2','\n', 'l'....]
        // 0    1   2   3   4    5    6      7   8   9   10
        int bufferSize = 1024 * 32;//32 kb buffers of file
        long end = raf.length();
        boolean shouldReadMore = true;
        //step1- keep taking the blocks from end
        while (shouldReadMore) {
            byte[] buf = new byte[bufferSize];
            long readLen = bufferSize;
            long startPointer = end - bufferSize;

            if (startPointer < 0) {
                readLen = startPointer + bufferSize;
                startPointer = 0;
            }
            raf.seek(startPointer);
            readLen = raf.read(buf, 0, (int) readLen);
            if (readLen <= 0) {
                break;
            }
            //we have the chunk of file from end,need to parse or new lines
            int unparsedSize = (int) readLen;
            int index = unparsedSize - 1;
            while (index >= 0) {
                if (buf[index] == '\n') {
                    int startOfLine = index + 1;
                    int len = unparsedSize - startOfLine;
                    if (len > 0) {
                        lines.add(new String(buf, startOfLine, len));
                        unparsedSize = index + 1;
                    }
                }
                index--;
            }
            end = end - bufferSize + unparsedSize;
            shouldReadMore = lines.size() < linesToShow && startPointer != 0;
        }
        //we can have lesser lines in the log
        if (linesToShow > lines.size()) {
            raf.seek(0);
            lines.add(raf.readLine() + '\n');
            linesToShow = lines.size();
        }
        for (int i = linesToShow - 1; i >= 0; i--) {
            for (Session session : sessions) {
                broadcast(sessions, lines.get(i));
            }

        }
        raf.seek(raf.length());
        while (true) {
            String curLine = raf.readLine();
            if (curLine != null) {
                broadcast(sessions, curLine);
            }
        }
    }

    private void broadcast(List<Session> sessions, String message) throws IOException {
        for (Session session : sessions) {
            session.getBasicRemote().sendText(message);
        }
    }
}
