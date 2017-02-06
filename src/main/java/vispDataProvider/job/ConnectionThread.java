package vispDataProvider.job;


import vispDataProvider.dataSender.MessageSender;
import entities.Message;

public class ConnectionThread implements Runnable {

    private MessageSender service;
    private Message message;
    private String queue;

    public ConnectionThread(MessageSender service, Message message, String queue) {
        this.service = service;
        this.message = message;
        this.queue = queue;
    }

    @Override
    public void run() {
        service.sendMessage(message, queue);
    }
}
