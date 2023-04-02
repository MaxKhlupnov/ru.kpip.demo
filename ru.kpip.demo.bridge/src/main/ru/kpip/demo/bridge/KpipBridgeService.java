package ru.kpip.demo.bridge;

import jakarta.annotation.PreDestroy;
import org.apache.activemq.artemis.jms.bridge.JMSBridge;
import org.apache.activemq.artemis.jms.bridge.QualityOfServiceMode;
import org.apache.activemq.artemis.jms.bridge.impl.JMSBridgeImpl;
import org.apache.activemq.artemis.jms.bridge.impl.JNDIConnectionFactoryFactory;
import org.apache.activemq.artemis.jms.bridge.impl.JNDIDestinationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kpip.demo.bridge.config.BridgeConfig;

import javax.naming.InitialContext;
import java.util.Hashtable;

@Service
public class KpipBridgeService {

    private static final Logger logger = LoggerFactory.getLogger(KpipBridgeService.class);
    private JMSBridge bridge;
    private BridgeConfig bridgeConfig;
    private static final String SOURCE_LOOKUP = "source";

    private static final String TARGET_LOOKUP = "target";
    private static final String CONNECTION_FACTORY_LOOKUP = "ConnectionFactory";
    @Autowired
    public void KpipBridgeFactory(BridgeConfig bridgeConfig) throws Exception {

        this.bridgeConfig = bridgeConfig;

        Hashtable<String, String> sourceJndiParams = createJndiParams(bridgeConfig.getSourceServer());
        Hashtable<String, String> targetJndiParams = createJndiParams(bridgeConfig.getTargetServer());

        // Step 1. Create and start a JMS Bridge
        // Note, the Bridge needs a transaction manager, in this instance we will use the JBoss TM
        JMSBridge jmsBridge = new JMSBridgeImpl(
                new JNDIConnectionFactoryFactory(sourceJndiParams,CONNECTION_FACTORY_LOOKUP),
                new JNDIConnectionFactoryFactory(targetJndiParams,CONNECTION_FACTORY_LOOKUP),
                new JNDIDestinationFactory(sourceJndiParams, String.format("source/%s",this.bridgeConfig.getSourceQueue())),
                new JNDIDestinationFactory(targetJndiParams,  String.format("target/%s",this.bridgeConfig.getTargetQueue())),
                this.bridgeConfig.getSourceUser(), this.bridgeConfig.getSourcePassword(),
                this.bridgeConfig.getTargetUser(), this.bridgeConfig.getTargetPassword(),
                null, 5000, 10, QualityOfServiceMode.DUPLICATES_OK,
                1, -1, null, null, true);
        logger.info("Starting bridge");

        jmsBridge.start();

        bridge = jmsBridge;
        logger.info("Bridge started");
    }
    @PreDestroy
    public void onDestroy() throws Exception {
        // Step 2. Stop JMS Bridge when complete
        bridge.stop();
        System.out.println("Bridge stopped");
    }

    /**
     * @see <a href="https://activemq.apache.org/components/artemis/documentation/latest/using-jms.html#destination-jndi>JNDI Mapping</a>
     * @param server
     * @return
     */
    private Hashtable<String, String> createJndiParams(String server) {
        Hashtable<String, String> jndiProps = new Hashtable<>();
        jndiProps.put("connectionFactory.ConnectionFactory", server);
        jndiProps.put("java.naming.factory.initial", "org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory");
        jndiProps.put(String.format("queue.target/%s",this.bridgeConfig.getTargetQueue()),
                            this.bridgeConfig.getTargetQueue());

        jndiProps.put(String.format("queue.source/%s",this.bridgeConfig.getSourceQueue()),
                            this.bridgeConfig.getSourceQueue());
        return jndiProps;
    }
}
