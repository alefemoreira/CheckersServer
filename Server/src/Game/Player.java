package Game;

import Pieces.Color;

import java.net.Inet4Address;
import java.net.Socket;

public class Player {
  private Color color;
  private Socket socket;

  public Player(Color color, Socket socket) {
    this.color = color;
    this.socket = socket;
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
}
