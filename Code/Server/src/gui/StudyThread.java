package gui;

import server.Server;

import java.io.IOException;

public class StudyThread implements Runnable {
    private final Study study;
    private Server server;

    public StudyThread(Study study, Server server) {
        this.study = study;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            study.runThisStudy(server);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
