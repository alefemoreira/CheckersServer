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

  public String processLine(String message)
    throws IllegalArgumentException, InvalidSession, NonExistentPiece
  {
    if (message.length() == 0) throw new IllegalArgumentException();

    String[] data = message.split(" ");

    switch (data[0]) {
      case "CREATE_SESSION":
        Color color = Color.WHITE;

        if (data[1].equals("BLACK"))
          color = Color.BLACK;

        this.session = Session.create(socket, color);

        // gera um código aleatório;
        Random rd = new Random();
        int n = rd.nextInt();
        String code = Integer.toHexString(n).substring(0, 6);

        this.session.setCode(code);

        Session.printSessions();
        Session.add(code, this.session);
        return "OK " + code;
      case "QUIT_SESSION":
        if (this.session != null) {
          try {
            this.session.close();
          } catch (Exception ignore) {}
        }
        return "BYE";
      case "BYE":
        return "BYE";
      case "CONNECT_SESSION":
        this.session = Session.find(data[1]);
        this.session.addPlayer2(this.socket);
        break;
      case "MOVE":
        int pieceId = Integer.parseInt(data[2]);
        Player player;

        if (this.socket.equals(this.session.getPlayer1().getSocket())) {
          player = this.session.getPlayer1();
        } else if (this.socket.equals(this.session.getPlayer2().getSocket())) {
          player = this.session.getPlayer2();
        } else {
          throw new InvalidSession();
        }

        int x = Integer.parseInt(data[3]);
        int y = Integer.parseInt(data[4]);
        Piece piece = this.session.getTable().getPieceById(pieceId);
        if (piece == null) throw new NonExistentPiece();
        this.session.move(player, piece, x, y);
        break;
      default:
        throw new IllegalArgumentException();
    }

    return "OK";
  }
}