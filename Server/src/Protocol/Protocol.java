package Protocol;

import Exceptions.InvalidSession;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import Exceptions.NonExistentPiece;
import Game.*;
import Pieces.Color;
import Pieces.Piece;

import java.net.Socket;

public class Protocol {
  private Socket socket;
  private Session session;


  public Protocol(Socket socket) {
    this.socket = socket;
  }

  public Message processLine(Message message, ObjectOutputStream out, ObjectInputStream in)
          throws IllegalArgumentException, InvalidSession, NonExistentPiece, IOException, InterruptedException {
    if (message.getAction().length() == 0) throw new IllegalArgumentException();

//    String[] data = message.split(" ");

    switch (message.getAction()) {
      case "CREATE_SESSION":
        Color color = Color.WHITE;


//        if (message.getColor().equals("BLACK"))
//          color = Color.BLACK;

        this.session = Session.create(socket, color, out, in);

        // gera um código aleatório;
        Random rd = new Random();
        int n = rd.nextInt();
        String code = Integer.toHexString(n).substring(0, 6);

        this.session.setCode(code);

        Session.printSessions();
        Session.add(code, this.session);

        Message msgResponse = new Message();
        msgResponse.setCodeSession(code);
        msgResponse.setColor("WHITE");

        return msgResponse;
      case "QUIT_SESSION":
        if (this.session != null) {
          try {
            this.session.close();
          } catch (Exception ignore) {}
        }
        Message msgQuit = new Message();
        msgQuit.setAction("QUIT");
        return msgQuit;
      case "CONNECT_SESSION":
        this.session = Session.find(message.getCodeSession());
        this.session.addPlayer2(this.socket, out, in);
        Message msgResponseConnect = new Message();
        msgResponseConnect.setAction("Start");
        msgResponseConnect.setColor("BLACK");
        msgResponseConnect.setCodeSession(message.getCodeSession());


        System.out.println(this.session.getPlayer1());
        System.out.println(this.session.getPlayer2());


        return msgResponseConnect;

      case "WAIT_MOVE":
//        this.session.getWait2().acquire();
        while (this.session.isBlackRound() && message.getColor().equals("WHITE") || !this.session.isBlackRound() && message.getColor().equals("BLACK")){
          System.out.println("waiting...");
        }

        System.out.println("Entregando tabuleiro");
        this.session = Session.find(message.getCodeSession());
        Message msgMoveResponseWait = new Message();
        Table tableWait = this.session.getTable();
        msgMoveResponseWait.setTable(tableWait);
//        this.session.wait.release();

          return msgMoveResponseWait;

      case "MOVE":
//        this.session.wait.acquire();
//        int pieceId = Integer.parseInt(data[2]);

        System.out.println("Vez do preto:" + this.session.isBlackRound());

        if (this.session.isBlackRound() && message.getColor().equals("WHITE") || !this.session.isBlackRound() && message.getColor().equals("BLACK")) {
          Message msgMoveResponse = new Message();
          msgMoveResponse.setAction("WRONG_TURN");
          return msgMoveResponse;
        }

        Player player1;
        Player player2;

        if (this.socket.equals(this.session.getPlayer1().getSocket())) {
          player1 = this.session.getPlayer1();
          player2 = this.session.getPlayer2();
        } else if (this.socket.equals(this.session.getPlayer2().getSocket())) {
          player1 = this.session.getPlayer2();
          player2 = this.session.getPlayer1();
        } else {
          throw new InvalidSession();
        }

        System.out.println( message.getOrigemX() + " " + message.getOrigemY() + " " + message.getDestinoX() + " " + message.getDestinoY());

        // Movendo o tabuleiro do servidor
        Table table = this.session.move(player1, message.getOrigemX(), message.getOrigemY(), message.getDestinoX(), message.getDestinoY());

        // Messangem de retorno para os clientes
        Message msgMoveResponse = new Message();
        msgMoveResponse.setAction("SUCCESS_MOVE");
        msgMoveResponse.setTable(table);
        msgMoveResponse.setSquare(table.getSquare(message.getDestinoX(), message.getDestinoY()));

        // Notificando o outro player sobre a jogada
        ObjectOutputStream out2 = player2.getOut();
        out2.writeObject(msgMoveResponse);
        player2.getOut().reset();

        // Nofificando o player que fez a jogada
        System.out.println(table);
//        this.session.wait.release();

//        this.session.getWait2().release();

        // Mudando o turno
        this.session.setBlackRound(!this.session.isBlackRound());

        return msgMoveResponse;

      default:
      throw new IllegalArgumentException();
    }

  }
}
