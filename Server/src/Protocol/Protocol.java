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
        // Segudno jogador conectando a sala
        this.session = Session.find(message.getCodeSession());
        this.session.addPlayer2(this.socket, out, in);
        Message msgResponseConnect = new Message();
        msgResponseConnect.setAction("START");
        msgResponseConnect.setColor("BLACK"); // Cor padrão do player que se conecta em uma sala
        msgResponseConnect.setCodeSession(message.getCodeSession());


//        System.out.println(this.session.getPlayer1());
//        System.out.println(this.session.getPlayer2());

        // Notificando ao player que criou a sala para proseguir para a tela do jogo
        Message msgResponseStartP1 = new Message();
        msgResponseStartP1.setAction("START");
        this.session.getPlayer1().getOut().reset();
        this.session.getPlayer1().getOut().writeObject(msgResponseStartP1);

        return msgResponseConnect;

      case "MOVE":

        System.out.println("Vez do preto:" + this.session.getTable().isBlackRound());
        // Jogador fez a jogada no turno errado
        if (this.session.getTable().isBlackRound() && message.getColor().equals("WHITE") || !this.session.getTable().isBlackRound() && message.getColor().equals("BLACK")) {
          Message msgMoveResponse = new Message();
          msgMoveResponse.setAction("WRONG_TURN");
          return msgMoveResponse;
        }

        // Definindo qual player fez a jogada(player1) e qual player vai receber o tabuleiro(player2)
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

        System.out.println(table);

        // Mudando o turno, só muda o turno da sala se o turno do tabuleiro mudar
//        this.session.setBlackRound(this.session.getTable().isBlackRound());

        return msgMoveResponse;

      default:
      throw new IllegalArgumentException();
    }

  }
}
