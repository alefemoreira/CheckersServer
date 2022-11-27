import Game.GameServer;
import Game.Session;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
  HashMap<String, Session> sessions;

  public static void main(String[] args) {
    ExecutorService pool = Executors.newCachedThreadPool();
    try {
      ServerSocket socket = new ServerSocket(3333);
      while (true) {
          Socket client = socket.accept();
          pool.execute(new GameServer(client));
      }
    } catch (Exception ignore) {}
  }
}