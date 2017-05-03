package wdsr.exercise4.receiver;

import java.math.BigDecimal;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wdsr.exercise4.PriceAlert;
import wdsr.exercise4.VolumeAlert;

public class JmsQueueReceiver {
	private static final Logger log = LoggerFactory.getLogger(JmsQueueReceiver.class);
	private final String queueName;
	private final ActiveMQConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	private MessageConsumer messageConsumer;

	public JmsQueueReceiver(final String queueName) {
		this.queueName = queueName;
		connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:62616");
		connectionFactory.setTrustAllPackages(true);
	}

	public void registerCallback(AlertService alertService) {

		try {
			connection = connectionFactory.createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(queueName);
			messageConsumer = session.createConsumer(destination);
			messageConsumer.setMessageListener(new MessageListener() {
				@Override
				public void onMessage(Message message) {
					startConsuming(alertService, message);
				}
			});
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private void startConsuming(AlertService alertService, Message message) {
		try {
			String messageType = message.getJMSType().toString();
			PriceAlert priceAlert = null;
			VolumeAlert volumeAlert = null;
			if (message instanceof ObjectMessage) {
				ObjectMessage objectMessage = (ObjectMessage) message;
				if (messageType.equals("VolumeAlert")) {
					volumeAlert = (VolumeAlert) objectMessage.getObject();
				} else if (messageType.equals("PriceAlert")) {
					priceAlert = (PriceAlert) objectMessage.getObject();
				}
			} else if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				if (messageType.equals("VolumeAlert")) {
					volumeAlert = processVolumeAlertTextMessage(textMessage);
				} else if (messageType.equals("PriceAlert")) {
					priceAlert = processPriceAlertTextMessage(textMessage);
				}
			}
			if (priceAlert != null) {
				alertService.processPriceAlert(priceAlert);
			} else if (volumeAlert != null) {
				alertService.processVolumeAlert(volumeAlert);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	private PriceAlert processPriceAlertTextMessage(TextMessage priceAlert) throws JMSException, NumberFormatException {

		String priceAlertText = priceAlert.getText();
		String[] priceAlertSplit = priceAlertText.split("=|\\r?\\n");
		PriceAlert priceAlertObject = null;
		if (priceAlertSplit.length == 6) {
			priceAlertObject = new PriceAlert(Long.parseLong(priceAlertSplit[1].trim()), priceAlertSplit[3].trim(),
					BigDecimal.valueOf(Long.parseLong(priceAlertSplit[5].trim())));
		}
		return priceAlertObject;
	}

	private VolumeAlert processVolumeAlertTextMessage(TextMessage volumeAlert)
			throws JMSException, NumberFormatException {
		String volumeAlertText = volumeAlert.getText();
		String[] volumeAlertSplit = volumeAlertText.split("=|\\r?\\n");
		VolumeAlert volumeAlertObject = null;
		if (volumeAlertSplit.length == 6) {
			volumeAlertObject = new VolumeAlert(Long.parseLong(volumeAlertSplit[1].trim()), volumeAlertSplit[3].trim(),
					Long.parseLong(volumeAlertSplit[5].trim()));
		}
		return volumeAlertObject;
	}

	public void shutdown() {
		try {
			messageConsumer.setMessageListener(null);
			session.close();
			connection.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
