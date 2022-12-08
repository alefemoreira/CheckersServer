package Game;

import Protocol.Protocol;
import Protocol.Message;

import java.io.*;
import java.net.Socket;

public class GameServer implements Runnable {
  private Socket client;
  private Protocol protocol;



  public GameServer(Socket client) {
    this.client = client;
    this.protocol = new Protocol(client);
  }

  @Override
  public void run() {
    System.out.println("Connected");

    try {

      ObjectInputStream in = new ObjectInputStream(client.getInputStream());
      ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
      System.out.println(client.getInputStream());

      while (true) {
        Message message = (Message) in.readObject();
        System.out.println(message.getAction());
        try {
          Message response = protocol.processLine(message, out, in);
          System.out.println("sala: " + response.getCodeSession() + " " + response.getAction() +  " " + response.getTable());
          out.writeObject(response);
          out.reset();
          if (response.equals("BYE")) break;
        } catch (Exception ignore) {
          // break;
          System.out.println(ignore);
        }
      }
      in.close();
      out.close();
      client.close();
    } catch (Exception ignore) {

    }
  }


}
