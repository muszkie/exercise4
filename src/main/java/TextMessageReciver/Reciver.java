package TextMessageReciver;

import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.LinkedList;
import java.util.List;

import javax.jms.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reciver {

	Session session;
	Topic queue;
	TopicSubscriber producer;
	Connection connection = null;
	ActiveMQConnectionFactory conn;
	private final String queueName;
	private List<TextMessage> list = new LinkedList<TextMessage>();

	private static final Logger log = LoggerFactory.getLogger(Reciver.class);

	public Reciver() {
		this.queueName = "MUSZKIE.TOPIC";
		makeConnection();

	}
    

	private void makeConnection() {
		try {
			conn = new ActiveMQConnectionFactory("tcp://localhost:61616");
			conn.setClientID("clientID");
			connection = conn.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			queue = session.createTopic(queueName);
			producer = session.createDurableSubscriber(queue,"testSub");
			producer.setMessageListener(new MessageListener() {

				@Override
				public void onMessage(Message message) {
					try {
						TextMessage mssg = (TextMessage) message;
						list.add(mssg);
						log.info("Recive message: " + mssg.getText());
						System.out.println(mssg.getText());
					} catch (JMSException e) {
						log.error("fail on method onMessage", e);
					}
				}

			});
		} catch (JMSException e) {
			log.error("failed", e);
		}

	}


	public void recive() {
		try {
			connection.start();
		} catch (JMSException e) {
			log.error("failed");
		} finally {
			waitMilliSeconds(100000L);
			log.info("Recived " + list.size() + " messages");
			closeConnection();
			System.exit(0);
		}
	}
	
	private void waitMilliSeconds(Long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			log.error("waiting failed");
		}
	}

	public void closeConnection() {
		try {
			producer.setMessageListener(null);
			connection.close();
			session.close();
		} catch (JMSException e) {
			log.error("closeConnection error", e);
		}

	}

}
