package Protocol;

import Exceptions.InvalidSession;
import java.util.Random;
import Exceptions.NonExistentPiece;
import Game.Player;
import Game.Session;
import Pieces.Color;
import Pieces.Piece;

import java.net.Socket;

public class Protocol {
  private Socket socket;
  private Session session;

  public Protocol(Socket socket) {
    this.socket = socket;
  }

  public Message processLine(Message message)
    throws IllegalArgumentException, InvalidSession, NonExistentPiece
  {
    if (message.getAction().length() == 0) throw new IllegalArgumentException();

//    String[] data = message.split(" ");

    switch (message.getAction()) {
      case "CREATE_SESSION":
        Color color = Color.WHITE;


//        if (message.getColor().equals("BLACK"))
//          color = Color.BLACK;

        this.session = Session.create(socket, color);

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
        this.session.addPlayer2(this.socket);
        Message msgResponseConnect = new Message();
        msgResponseConnect.setAction("Start");
        msgResponseConnect.setColor("BLACK");


        System.out.println(this.session.getPlayer1());
        System.out.println(this.session.getPlayer2());



        return msgResponseConnect;
      case "MOVE":
//        int pieceId = Integer.parseInt(data[2]);
        Player player;

        if (this.socket.equals(this.session.getPlayer1().getSocket())) {
          player = this.session.getPlayer1();
        } else if (this.socket.equals(this.session.getPlayer2().getSocket())) {
          player = this.session.getPlayer2();
        } else {
          throw new InvalidSession();
        }

//        Piece piece = this.session.getTable().getPieceById(pieceId);
//        if (piece == null) throw new NonExistentPiece();
        this.session.move(player, message.getOrigemX(), message.getOrigemY(), message.getDestinoX(), message.getDestinoY());
        return message;

      default:
      throw new IllegalArgumentException();
    }

  }
}
