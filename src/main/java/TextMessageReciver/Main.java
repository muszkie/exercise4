package TextMessageReciver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import TextMessageReciver.Reciver;

public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		Reciver reciver = new Reciver();
		reciver.recive();
		reciver.closeConnection();
		System.exit(0);
	}
}