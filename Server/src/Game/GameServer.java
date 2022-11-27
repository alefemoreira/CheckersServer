package Game;

import Protocol.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
    try {
      DataInputStream in = new DataInputStream(client.getInputStream());
      DataOutputStream out = new DataOutputStream(client.getOutputStream());
      while (true) {
        String message = in.readUTF();
        try {
          String response = protocol.processLine(message);
          out.writeUTF(response);
          if (response.equals("BYE")) break;
        } catch (Exception ignore) {
          // break;
        }
      }
      in.close();
      out.close();
      client.close();
    } catch (Exception ignore) {}
  }
}
