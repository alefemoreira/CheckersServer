package Game;

import Pieces.Color;
import Pieces.Pawn;
import Pieces.Piece;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class Session {
  private static final Hashtable<String, Session> sessions = new Hashtable<>();

  private String code;
  private Socket socket;
  private Player player1, player2;
  private Table table;

  private boolean blackRound = false;


  public Semaphore wait;
  public Semaphore wait2;

  private Session (Socket socket, Color player1Color,  ObjectOutputStream out, ObjectInputStream in) throws IOException {
    this.player1 = new Player(player1Color, socket, out, in);

    // Create table
    this.table = new Table(false); // Começa com o turno das brancas
    this.criarPecas();

    this.wait = new Semaphore(0, true);
    this.wait2 = new Semaphore(0, true);
  }

  public static Session create(Socket socket, Color player1Color, ObjectOutputStream out, ObjectInputStream in) throws IOException {
    return new Session(socket, player1Color, out, in);
  }

  public void addPlayer2(Socket socket,  ObjectOutputStream out, ObjectInputStream in) throws IOException {
    Color color = Color.BLACK;
    if (player1.getColor() == Color.BLACK)
      color = Color.WHITE;

    this.player2 = new Player(color, socket, out, in);
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
    Piece peca = (Piece) origem.getPiece();
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

  public boolean isBlackRound() {
    return blackRound;
  }

  public void setBlackRound(boolean blackRound) {
    this.blackRound = blackRound;
    System.out.println(this.blackRound);
  }
}
