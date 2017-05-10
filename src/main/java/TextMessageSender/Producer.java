package TextMessageSender;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Producer {

	Session session;
	Topic queue;
	MessageProducer producer;
	Connection connection = null;
	ActiveMQConnectionFactory conn;
	private final String queueName;
	
	private static final Logger log = LoggerFactory.getLogger(Producer.class);

	public Producer() {
		this.queueName = "MUSZKIE.TOPIC";
		makeConnection();

	}

	private void makeConnection() {

		try {
			conn = new ActiveMQConnectionFactory("tcp://localhost:61616");
			connection = conn.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			queue = session.createTopic(queueName);
			producer = session.createProducer(queue);

		} catch (JMSException e) {
			log.error("makeConnection error",e);
		}

	}

	public void sendTextMessages() {
		try {
			int mode = DeliveryMode.PERSISTENT;
			String stringMode = "presistent";

			sendMessages(mode, stringMode);
			
			mode = DeliveryMode.NON_PERSISTENT;
			stringMode = "non_presistent";
			
			sendMessages(mode, stringMode);
			

		} catch (JMSException e) {
			log.error("sendTextMessages error",e);
		}

	}

	private void sendMessages(int mode, String stringMode) throws JMSException {
		long startTime = System.currentTimeMillis();
		for (int i=0; i<10000; ++i) {
			TextMessage mssg = session.createTextMessage("test_" + i+1);
			mssg.setJMSDeliveryMode(mode);
			producer.send(mssg);
		}
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		String info = String.format("10000 %s messages sent in %d milliseconds ", stringMode, elapsedTime);
		log.info(info);
	}


	public void closeConnection() {
		try {
			connection.close();
			session.close();
		} catch (JMSException e) {
			log.error("closeConnection error",e);
		}

	}

}
