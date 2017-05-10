package TextMessageSender;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import TextMessageSender.Producer;


public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		Producer sender = new Producer();
		sender.sendTextMessages();;
		sender.closeConnection();
		System.exit(0);
	}
}