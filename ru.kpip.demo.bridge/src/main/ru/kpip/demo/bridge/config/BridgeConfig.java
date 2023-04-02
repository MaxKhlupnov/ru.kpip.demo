package ru.kpip.demo.bridge.config;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kpip.demo.bridge.KpipBridgeService;

import java.util.Hashtable;

@Configuration
public class BridgeConfig {

   // private static Logger LOG = LogManager.getLogger(BridgeConfig.class);
   private static final Logger logger = LoggerFactory.getLogger(BridgeConfig.class);
    @Value("${kpip.bridge.sourceServer}")
    @NotBlank
    private String sourceServer;

    @Value("${kpip.bridge.sourceUser}")
    @NotBlank
    private String sourceUser;

    @Value("${kpip.bridge.sourcePassword}")
    @NotBlank
    private String sourcePassword;

    @Value("${kpip.bridge.targetServer}")
    @NotBlank
    private String targetServer;


   @Value("${kpip.bridge.targetUser}")
   @NotBlank
   private String targetUser;

   @Value("${kpip.bridge.targetPassword}")
   @NotBlank
   private String targetPassword;

 @Value("${kpip.bridge.concurrentConsumers}")
    @NotBlank
    private Integer concurrentConsumers;

    @Value("${kpip.bridge.sourceQueue}")
    private String sourceQueue;

   @Value("${kpip.bridge.targetQueue}")
   private String targetQueue;

    public String getSourceServer() {
     return sourceServer;
    }

    public String getSourceUser() {
     return sourceUser;
    }

    public String getSourcePassword() {
     return sourcePassword;
    }

    public String getTargetServer() {
     return targetServer;
    }
    public String getTargetUser() {
     return targetUser;
    }

    public String getTargetPassword() {
     return targetPassword;
    }

 public Integer getConcurrentConsumers() {
     return concurrentConsumers;
    }

    public String getSourceQueue() {
     return sourceQueue;
    }

    public String getTargetQueue() {
     return targetQueue;
    }
}
