
package ru.kpip.demo.bridge;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;
import java.util.Hashtable;

import org.apache.activemq.artemis.jms.bridge.JMSBridge;
import org.apache.activemq.artemis.jms.bridge.QualityOfServiceMode;
import org.apache.activemq.artemis.jms.bridge.impl.JMSBridgeImpl;
import org.apache.activemq.artemis.jms.bridge.impl.JNDIConnectionFactoryFactory;
import org.apache.activemq.artemis.jms.bridge.impl.JNDIDestinationFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Max Khlupnov
 * @date 02/04/2023
 * Пример, который отправляет сообщение из исходного топика и получает егo через очередь приемника.
 * Источник и приемник расположены на 2 разных серверах ActiveMQ Artemis.
 * Очереди источника и приемника соединены мостом JMS, настроенным и запущенным на "целевом" сервере.
 */

@SpringBootApplication
public class KpipBridgeExample {

    public static void main(String[] args) {
            SpringApplication.run(KpipBridgeExample.class, args);
        }


    public static void main_old(final String[] args) throws Exception {
        String sourceServer = "tcp://localhost:61616";
        String targetServer = "tcp://localhost:61617";

        System.out.println("client will publish messages to " + sourceServer +
                " and receives message from " +
                targetServer);

        // Step 1. Create JNDI contexts for source and target servers
        InitialContext sourceContext = KpipBridgeExample.createContext(sourceServer);

        InitialContext targetContext = KpipBridgeExample.createContext(targetServer);

        Hashtable<String, String> sourceJndiParams = createJndiParams(sourceServer);
        Hashtable<String, String> targetJndiParams = createJndiParams(targetServer);
        // Step 2. Create and start a JMS Bridge
        // Note, the Bridge needs a transaction manager, in this instance we will use the JBoss TM

        JMSBridge jmsBridge = new JMSBridgeImpl(new JNDIConnectionFactoryFactory(sourceJndiParams,
                "ConnectionFactory"), new JNDIConnectionFactoryFactory(targetJndiParams,
                "ConnectionFactory"), new JNDIDestinationFactory(sourceJndiParams, "source/topic"),
                new JNDIDestinationFactory(targetJndiParams, "target/queue"),
                "artemis", "artemis", "artemis", "artemis",
                null, 5000, 10, QualityOfServiceMode.DUPLICATES_OK,
                1, -1, null, null, true);

        Connection sourceConnection = null;
        Connection targetConnection = null;
        try {
            jmsBridge.start();
            // Step 3. Lookup the *source* JMS resources
            ConnectionFactory sourceConnectionFactory = (ConnectionFactory) sourceContext.lookup("ConnectionFactory");

            Topic sourceTopic = (Topic) sourceContext.lookup("source/topic");

            // Step 4. Create a connection, a session and a message producer for the *source* topic
            sourceConnection = sourceConnectionFactory.createConnection("artemis","artemis");
            Session sourceSession = sourceConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer sourceProducer = sourceSession.createProducer(sourceTopic);

            // Step 5. Create and send a text message to the *source* queue
            TextMessage message = sourceSession.createTextMessage("this is a text message sent at " + System.currentTimeMillis());
            sourceProducer.send(message);
            System.out.format("Sent message to %s: %s%n", ((Topic) message.getJMSDestination()).getTopicName(), message.getText());
            System.out.format("Message ID : %s%n", message.getJMSMessageID());

            // Step 6. Close the *source* connection
            sourceConnection.close();

            // Step 7. Lookup the *target* JMS resources
            ConnectionFactory targetConnectionFactory = (ConnectionFactory) targetContext.lookup("ConnectionFactory");
            Queue targetQueue = (Queue) targetContext.lookup("target/queue");

            // Step 8. Create a connection, a session and a message consumer for the *target* queue
            targetConnection = targetConnectionFactory.createConnection("artemis","artemis");
            Session targetSession = targetConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer targetConsumer = targetSession.createConsumer(targetQueue);

            // Step 9. Start the connection to receive messages from the *target* queue
            targetConnection.start();

            // Step 10. Receive a message from the *target* queue
            TextMessage messageReceived = (TextMessage) targetConsumer.receive(500000);
            System.out.format("%nReceived from %s: %s%n", ((Queue) messageReceived.getJMSDestination()).getQueueName(), messageReceived.getText());

            // Step 11. Display the received message's ID and this "bridged" message ID
            System.out.format("Message ID         : %s%n", messageReceived.getJMSMessageID());
            System.out.format("Bridged Message ID : %s%n", messageReceived.getStringProperty("AMQ_BRIDGE_MSG_ID_LIST"));
        }
        catch (Exception ex){
            System.out.format("Error      : %s", ex.getMessage());
            throw ex;
        } finally {
            // Step 12. Be sure to close the resources!
            jmsBridge.stop();
            if (sourceContext != null) {
                sourceContext.close();
            }
            if (targetContext != null) {
                targetContext.close();
            }
            if (sourceConnection != null) {
                sourceConnection.close();
            }
            if (targetConnection != null) {
                targetConnection.close();
            }
        }
    }

    private static InitialContext createContext(final String server) throws Exception {
        Hashtable<String, String> jndiProps = createJndiParams(server);
        return new InitialContext(jndiProps);
    }

    private static Hashtable<String, String> createJndiParams(String server) {
        Hashtable<String, String> jndiProps = new Hashtable<>();
        jndiProps.put("connectionFactory.ConnectionFactory", server);
        jndiProps.put("java.naming.factory.initial", "org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory");
        jndiProps.put("queue.target/queue", "target");
        jndiProps.put("topic.source/topic", "source");
        return jndiProps;
    }

}
