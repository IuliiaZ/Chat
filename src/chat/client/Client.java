package chat.client;

import chat.Connection;
import chat.ConsoleHelper;
import chat.Message;
import chat.MessageType;

import java.io.IOException;
import java.net.Socket;

public class Client {
    protected Connection connection;
    private volatile boolean clientConnected = false;

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
    public void run(){
        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();
        synchronized (this){
            try{
                this.wait();
            }catch (InterruptedException e){
                ConsoleHelper.writeMessage("Connection was interrupted." + e.getMessage());
            }
        }
        if (clientConnected){
            ConsoleHelper.writeMessage("Got a connection. For exit input 'exit'");
        } else {
            ConsoleHelper.writeMessage("Error");
            return;
        }
        String line = null;
        while (clientConnected){
            line = ConsoleHelper.readString();
            if (line.equalsIgnoreCase("exit")){ break; }
            if (shouldSendTextFromConsole()){
                sendTextMessage(line);
            }
        }
    }

    protected String getServerAddress() throws IOException{
        ConsoleHelper.writeMessage("Insert server IP");
        String ip = ConsoleHelper.readString();
        return ip;
        }

    protected int getServerPort() throws IOException{
        ConsoleHelper.writeMessage("Insert server port");
        int port = ConsoleHelper.readInt();
        return port;
    }

    protected String getUserName() throws IOException{
        ConsoleHelper.writeMessage("Insert your name");
        String name = ConsoleHelper.readString();
        return name;
    }

    protected boolean shouldSendTextFromConsole(){
        return true;
    }

    protected SocketThread getSocketThread(){
        SocketThread socketThread = new SocketThread();
        return socketThread;
    }

    protected void sendTextMessage(String text){
        try{
            connection.send(new Message(MessageType.TEXT, text));
        } catch (IOException e){
            clientConnected = false;
        }
    }

    public class SocketThread extends Thread {
        protected void processIncomingMessage(String message){
            ConsoleHelper.writeMessage(message);
        }

        protected void informAboutAddingNewUser(String userName){
            ConsoleHelper.writeMessage(String.format("New user - %s, was connected.", userName));
        }

        protected void informAboutDeletingNewUser(String userName){
            ConsoleHelper.writeMessage(String.format("User - %s, was disconnected.", userName));
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected){
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this){
                Client.this.notify();
            }
        }

        protected void clientHandshake() throws IOException, ClassNotFoundException {
            while (true){
                Message message = connection.receive();
                if (message.getType() == MessageType.NAME_REQUEST){
                    Message message1 = new Message(MessageType.USER_NAME, getUserName());
                    connection.send(message1);
                } else if (message.getType() == MessageType.NAME_ACCEPTED){
                    notifyConnectionStatusChanged(true);
                    return;
                } else {
                    throw  new IOException("Unexpected MessageType");
                }

            }
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException{
            while (true){
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT){
                    processIncomingMessage(message.getData());
                } else if (message.getType() == MessageType.USER_ADDED){
                    informAboutAddingNewUser(message.getData());
                } else if (message.getType() == MessageType.USER_REMOVED){
                    informAboutDeletingNewUser(message.getData());
                } else {
                    throw  new IOException("Unexpected MessageType");
                }
            }
        }

        @Override
        public void run() {
            try {
                connection = new Connection(new Socket(getServerAddress(), getServerPort()));
                clientHandshake();
                clientMainLoop();
            } catch (IOException | ClassNotFoundException e) {
                notifyConnectionStatusChanged(false);
            }
        }
    }
}
