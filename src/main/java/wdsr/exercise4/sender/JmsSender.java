package wdsr.exercise4.sender;

import org.apache.activemq.ActiveMQConnectionFactory;
import java.math.BigDecimal;
import java.util.Map;

import javax.jms.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wdsr.exercise4.Order;

public class JmsSender {
	private static final Logger log = LoggerFactory.getLogger(JmsSender.class);

	private final String queueName;
	private final String topicName;

	public JmsSender(final String queueName, final String topicName) {
		this.queueName = queueName;
		this.topicName = topicName;
	}

	public void sendOrderToQueue(final int orderId, final String product, final BigDecimal price) {
		Connection connection = null;
		try {
			Order order = new Order(orderId, product, price);
			ActiveMQConnectionFactory conn = new ActiveMQConnectionFactory("tcp://localhost:62616");
			try {
				connection = conn.createConnection();
				connection.start();

				Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				Queue queue = session.createQueue(queueName);
				MessageProducer producer = session.createProducer(queue);
				Message msg = session.createObjectMessage(order);
				msg.setJMSType("Order");
				msg.setStringProperty("WDSR-System", "OrderProcessor");
				producer.send(msg);
				session.close();
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void sendTextToQueue(String text) {
		Connection connection = null;
		try {
			ActiveMQConnectionFactory conn = new ActiveMQConnectionFactory("tcp://localhost:62616");
			try {
				connection = conn.createConnection();

				Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				Queue queue = session.createQueue(queueName);
				MessageProducer producer = session.createProducer(queue);
				Message msg = session.createTextMessage(text);
				producer.send(msg);
				session.close();
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void sendMapToTopic(Map<String, String> map) {
		Connection connection = null;
		try {
			ActiveMQConnectionFactory conn = new ActiveMQConnectionFactory("tcp://localhost:62616");
			connection = conn.createConnection();
			Session session;
			try {
				session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

				Topic queue = session.createTopic(topicName);
				MessageProducer producer = session.createProducer(queue);
				MapMessage msg = session.createMapMessage();
				for (Map.Entry<String, String> entry : map.entrySet()) {
					msg.setString(entry.getKey(), entry.getValue());
				}
				producer.send(msg);
				session.close();
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
