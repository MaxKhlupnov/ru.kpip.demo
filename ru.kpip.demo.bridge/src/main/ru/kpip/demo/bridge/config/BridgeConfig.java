package ru.kpip.demo.bridge.config;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class BridgeConfig {

   // private static Logger LOG = LogManager.getLogger(BridgeConfig.class);
   private static final Logger logger = LoggerFactory.getLogger(BridgeConfig.class);

    @Value("${kpip.bridge.sourceServer}")
    private String sourceServer;

    @Value("${kpip.bridge.sourceUser}")
    private String sourceUser;

    @Value("${kpip.bridge.sourcePassword}")
    private String sourcePassword;

    @Value("${kpip.bridge.targetServer}")
    private String targetServer;

    @Value("${kpip.bridge.concurrentConsumers}")
    private Integer concurrentConsumers;
    @Bean
    public  ConnectionFactory sourceConnectionFactory() throws JMSException {
        ActiveMQConnectionFactory artemisConnectionFactory = new ActiveMQConnectionFactory();
        artemisConnectionFactory.setBrokerURL(sourceServer);
        artemisConnectionFactory.setUser(sourceUser);
        artemisConnectionFactory.setPassword(sourcePassword);
        artemisConnectionFactory.setBlockOnDurableSend(true);
        artemisConnectionFactory.setThreadPoolMaxSize(concurrentConsumers); //Пул потоков слушателей соединений

        return (ConnectionFactory) artemisConnectionFactory;
    }
}
