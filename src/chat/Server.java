package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ConsoleHelper.writeMessage("Input server port number:");
        try (ServerSocket serverSocket = new ServerSocket(ConsoleHelper.readInt())) {
            while (true){
                Socket clientSocket = serverSocket.accept();
                Handler handler = new Handler(clientSocket);
                handler.start();
            }
        } catch (IOException e) {
            System.out.println("Something was wrong, server socket will be close.");
        }
    }

    private static class Handler extends Thread {
        private Socket socket;
        public Handler(Socket socket){
            this.socket = socket;
        }

        public void run() {
            ConsoleHelper.writeMessage("Got a connection with user " + socket.getRemoteSocketAddress());
            String newUser = null;
            try(Connection connection = new Connection(socket)) {
                newUser = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, newUser));
                sendListOfUsers(connection, newUser);
                serverMainLoop(connection, newUser);
            } catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Error with remote address");

            } finally {
                if (newUser != null) {
                    connectionMap.remove(newUser);
                    sendBroadcastMessage(new Message(MessageType.USER_REMOVED, newUser));
                    ConsoleHelper.writeMessage(String.format("Connection with remote adress %s closed.", socket.getRemoteSocketAddress()));
                }
            }
        }


        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException{
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message answer = connection.receive();
                if (answer.getType() == MessageType.USER_NAME) {
                    if (!answer.getData().isEmpty()){
                        if(!connectionMap.containsKey(answer.getData())) {
                            connectionMap.put(answer.getData(), connection);
                            connection.send(new Message(MessageType.NAME_ACCEPTED));
                            System.out.println("Got a connection");
                            return answer.getData();
                        }
                    }
                }
            }
        }

        private void sendListOfUsers(Connection connection, String userName) throws IOException{
            for (Map.Entry<String, Connection> pair : connectionMap.entrySet()) {
                if (!pair.getKey().equals(userName)) {
                    connection.send(new Message(MessageType.USER_ADDED, pair.getKey()));
                }
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException{
            while (true){
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT){
                    sendBroadcastMessage(new Message(MessageType.TEXT,userName + ": "+message.getData()));
                } else {
                    ConsoleHelper.writeMessage("Error!");
                }
            }
        }
    }

    public static void sendBroadcastMessage(Message message){
        try{
            for (Map.Entry<String, Connection> pair : connectionMap.entrySet()){
                pair.getValue().send(message);
            }
        } catch (IOException ex){
            System.out.println("Message doesn't send.");
        }
    }
}
