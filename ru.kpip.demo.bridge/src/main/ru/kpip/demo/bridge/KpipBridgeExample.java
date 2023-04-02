
package ru.kpip.demo.bridge;

import javax.jms.*;
import javax.naming.InitialContext;
import java.util.Hashtable;

import org.apache.activemq.artemis.jms.bridge.JMSBridge;
import org.apache.activemq.artemis.jms.bridge.QualityOfServiceMode;
import org.apache.activemq.artemis.jms.bridge.impl.JMSBridgeImpl;
import org.apache.activemq.artemis.jms.bridge.impl.JNDIConnectionFactoryFactory;
import org.apache.activemq.artemis.jms.bridge.impl.JNDIDestinationFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jms.annotation.EnableJms;
import ru.kpip.demo.bridge.config.BridgeConfig;

/**
 * @author Max Khlupnov
 * @date 02/04/2023
 * Пример, который отправляет сообщение из исходного топика и получает егo через очередь приемника.
 * Источник и приемник расположены на 2 разных серверах ActiveMQ Artemis.
 * Очереди источника и приемника соединены мостом JMS, настроенным и запущенным на "целевом" сервере.
 *  @see <a href="https://spring.io/guides/gs/messaging-jms/">Spring JMS Messaging Guide</a>
 */

@SpringBootApplication
public class KpipBridgeExample {

    public static void main(String[] args) {
            SpringApplication.run(KpipBridgeExample.class, args);
        }

}
