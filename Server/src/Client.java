import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
  public static void main(String[] args) throws IOException {
    Socket s = new Socket("localhost", 3333);
    DataInputStream in = new DataInputStream(s.getInputStream());
    DataOutputStream out = new DataOutputStream(s.getOutputStream());
    Scanner stdIn = new Scanner(System.in);
    while (true) {
      String fromUser = stdIn.nextLine();
      out.writeUTF(fromUser);
      System.out.println(in.readUTF());
      if (fromUser.equals("Bye")) break;
    }
    in.close();
    out.close();
    s.close();
  }
}
