package chat.client;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BotClient extends Client {

    public static void main(String[] args) {
      BotClient botClient = new BotClient();
      botClient.run();
    }

    @Override
    protected String getUserName() throws IOException {
        //int x = (int)Math.random()*100;
        return "date_bot_" + (int)(Math.random()*100);
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected SocketThread getSocketThread() {
        BotSocketThread botSocketThread = new BotSocketThread();
        return botSocketThread;
    }

    public class BotSocketThread extends SocketThread {

        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException{
            sendTextMessage("Hello everyone, I'm a bot. I understand the commands: date, day, month, year, time, hour, minute, second.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            super.processIncomingMessage(message);
            if (message.contains(": ")) {
                String name = message.substring(0, message.indexOf(":"));
                String text = message.substring(message.indexOf(": ") + 2);
                Date date = new Date();
                if (text.equalsIgnoreCase("date")) {
                    SimpleDateFormat format = new SimpleDateFormat("d.MM.YYYY");
                    sendTextMessage(String.format("Information for %s: %s", name, format.format(date)));
                }
                if (text.equalsIgnoreCase("day")) {
                    SimpleDateFormat format = new SimpleDateFormat("d");
                    sendTextMessage(String.format("Information for %s: %s", name, format.format(date)));
                }
                if (text.equalsIgnoreCase("month")) {
                    SimpleDateFormat format = new SimpleDateFormat("MMMM");
                    sendTextMessage(String.format("Information for %s: %s", name, format.format(date)));
                }
                if (text.equalsIgnoreCase("year")) {
                    SimpleDateFormat format = new SimpleDateFormat("YYYY");
                    sendTextMessage(String.format("Information for %s: %s", name, format.format(date)));
                }
                if (text.equalsIgnoreCase("time")) {
                    SimpleDateFormat format = new SimpleDateFormat("H:mm:ss");
                    sendTextMessage(String.format("Information for %s: %s", name, format.format(date)));
                }
                if (text.equalsIgnoreCase("hour")) {
                    SimpleDateFormat format = new SimpleDateFormat("H");
                    sendTextMessage(String.format("Information for %s: %s", name, format.format(date)));
                }
                if (text.equalsIgnoreCase("minute")) {
                    SimpleDateFormat format = new SimpleDateFormat("m");
                    sendTextMessage(String.format("Information for %s: %s", name, format.format(date)));
                }
                if (text.equalsIgnoreCase("second")) {
                    SimpleDateFormat format = new SimpleDateFormat("s");
                    sendTextMessage(String.format("Information for %s: %s", name, format.format(date)));
                }
            }
        }
    }
}
