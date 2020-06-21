import java.io.EOFException;
public class ServerTest2 {
	public static void main(String[] args) throws EOFException {
		NewServer s2 = new NewServer(4000,5000);
		s2.startRunning();
	}
}