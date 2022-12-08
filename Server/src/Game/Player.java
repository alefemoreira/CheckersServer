package Game;

import Pieces.Color;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.Socket;

public class Player {
  private Color color;
  private Socket socket;

  private ObjectOutputStream out;

  private ObjectInputStream in;

  public Player(Color color, Socket socket, ObjectOutputStream out, ObjectInputStream in) throws IOException {
    this.color = color;
    this.socket = socket;
    this.out = out;
    this.in = in;

  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public Socket getSocket() {
    return socket;
  }

  public void setSocket(Socket socket) {
    this.socket = socket;
  }

  public ObjectOutputStream getOut() {
    return out;
  }

  public ObjectInputStream getIn() {
    return in;
  }
}
