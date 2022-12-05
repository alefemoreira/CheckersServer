package Game;

import Pieces.Color;
import Pieces.Pawn;
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

    // Create table
    this.table = new Table();
    this.criarPecas();
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

  public Table move(Player player, int origemX, int origemY, int destinoX, int destinoY) {
//    System.out.println(origemX);
//    System.out.println(origemY);
//    System.out.println(destinoX);
//    System.out.println(destinoY);

    Square origem = this.table.getSquare(origemX, origemY);
    Square destino = this.table.getSquare(destinoX, destinoY);
    Pawn peca = (Pawn) origem.getPiece();
    peca.mover(destino, this.table);

    return this.table;
  }

  private void criarPecas() {
    this.criarPecasPretas();
    this.criarPecasBrancas();

  }

  public void criarPecasPretas() {
    for(int i = 7; i > 4; i--) { // cria as 12 peças pretas
      for(int j = 0; j < 8; j += 2) {
        if (i % 2 == 1 && j == 0) {
          j++;
        }
        Square casa = this.table.getSquare(j, i);
        new Pawn(casa, Color.BLACK);
      }
    }
  }

  public void criarPecasBrancas() {
    for(int i = 0; i < 3; i++) { // cria as 12 peças brancas
      for(int j = 0; j < 8; j += 2) {
        if (i % 2 == 1 && j == 0) {
          j++;
        }
        Square casa = this.table.getSquare(j, i);
        new Pawn(casa, Color.WHITE);
      }
    }
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
