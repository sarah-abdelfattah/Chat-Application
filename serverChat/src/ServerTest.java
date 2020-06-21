import java.io.EOFException;
import java.util.Arrays;
import javax.swing.JFrame;
public class ServerTest {
	public static void main(String[] args) throws EOFException {
		NewServer s = new NewServer(6000,5000);
		s.startRunning();
		
	}
}