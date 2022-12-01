import Game.GameServer;
import Game.Session;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
  HashMap<String, Session> sessions;

  public static void main(String[] args) throws IOException {
      ServerSocket socket = new ServerSocket(54323);
      ExecutorService pool = Executors.newCachedThreadPool();

      while (true) {
          Socket client = socket.accept();
          pool.execute(new GameServer(client));
      }
  }
}