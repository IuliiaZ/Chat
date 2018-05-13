package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {

    private static BufferedReader bufferedReader  = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message){
        System.out.println(message);
    }

    public static String readString(){
        String line = null;
        try{
          line = bufferedReader.readLine();
        } catch (IOException e){
            System.out.println("An error occurred while trying to enter text. Try again.");
            line = readString();
        }
        return line;
    }

    public static int readInt(){
        int i = 0;
        try{
           return Integer.parseInt(readString());
        } catch (NumberFormatException e){
            System.out.println("An error occurred while trying to enter number. Try again.");
            i = readInt();
        }
        return i;
    }
}
