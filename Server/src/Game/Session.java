package Game;

import Pieces.Color;
import Pieces.Piece;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class Session {
  private static final Hashtable<String, Session> sessions = new Hashtable<>();

  private String code;
  private Socket socket;
  private Player player1, player2;
  private Table table;

  private Session (Socket socket, Color player1Color) {
    this.player1 = new Player(player1Color, socket);
    this.table = new Table();
  }

  public static Session create(Socket socket, Color player1Color) {
    return new Session(socket, player1Color);
  }

  public void addPlayer2(Socket socket) {
    Color color = Color.BLACK;
    if (player1.getColor() == Color.BLACK)
      color = Color.WHITE;

    this.player2 = new Player(color, socket);
  }

  public static void printSessions() {
    Iterator<Map.Entry<String, Session>> itr = Session.sessions.entrySet().iterator();

    Map.Entry<String, Session> entry = null;
    while(itr.hasNext()){

      entry = itr.next();
      System.out.println( entry.getKey() + "->" + entry.getValue() );
    }
  }

  public static void add(String code, Session session) {
    if (!Session.sessions.containsKey(code)) {
      Session.sessions.put(code, session);
    }
  }

  public static Session find(String code) {
    return Session.sessions.get(code);
  }

  public void close() throws IOException {
    this.player1.getSocket().close();
    this.player2.getSocket().close();
  }

  public void move(Player player, Piece piece, int x, int y) {

  }

  public Player getPlayer1() {
    return player1;
  }

  public Player getPlayer2() {
    return player2;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Socket getSocket() {
    return socket;
  }

  public Table getTable() {
    return table;
  }
}
