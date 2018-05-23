package com.ctrip.framework.apollo.tracer;

import com.ctrip.framework.apollo.core.utils.ResourceUtils;
import com.ctrip.framework.apollo.core.utils.StringUtils;
import com.ctrip.framework.apollo.tracer.internals.NullMessageProducerManager;
import com.ctrip.framework.apollo.tracer.spi.MessageProducer;
import com.ctrip.framework.apollo.tracer.spi.MessageProducerManager;
import com.ctrip.framework.apollo.tracer.spi.Transaction;
import com.ctrip.framework.foundation.internals.ServiceBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public abstract class Tracer {
    private static final Logger logger = LoggerFactory.getLogger(Tracer.class);
    private static final MessageProducerManager NULL_MESSAGE_PRODUCER_MANAGER =
            new NullMessageProducerManager();
    private static volatile MessageProducerManager producerManager;
    private static Object lock = new Object();

    static {
        getProducer();
        updateProducerWhenNecessary();
    }

    private static void updateProducerWhenNecessary() {
        Properties prop = new Properties();
        prop = ResourceUtils.readConfigFile("apollo-env.properties", prop);
        Properties env = System.getProperties();
        String catAutoValue = env.getProperty("cat.auto", prop.getProperty("cat.auto", "true"));
        boolean catAuto = true;
        if (!StringUtils.isEmpty(catAutoValue)) {
            catAuto = "true".equals(catAutoValue) ? true : false;
        }

        if (catAuto) {
            updateProducer();
        }
    }

    public static void updateProducer() {
        try {
            synchronized (lock) {
                producerManager = ServiceBootstrap.loadFirst(MessageProducerManager.class);
            }
        } catch (Throwable ex) {
            logger.error(
                    "Failed to initialize message producer manager, use null message producer manager.", ex);
            producerManager = NULL_MESSAGE_PRODUCER_MANAGER;
        }
    }

    public static void logError(String message, Throwable cause) {
        try {
            getProducer().logError(message, cause);
        } catch (Throwable ex) {
            logger.warn("Failed to log error for message: {}, cause: {}", message, cause, ex);
        }
    }

    private static MessageProducer getProducer() {
        if (producerManager == null) {
            synchronized (lock) {
                if (producerManager == null) {
                    producerManager = NULL_MESSAGE_PRODUCER_MANAGER;
                }
            }
        }
        return producerManager.getProducer();
    }

    public static void logError(Throwable cause) {
        try {
            getProducer().logError(cause);
        } catch (Throwable ex) {
            logger.warn("Failed to log error for cause: {}", cause, ex);
        }
    }

    public static void logEvent(String type, String name) {
        try {
            getProducer().logEvent(type, name);
        } catch (Throwable ex) {
            logger.warn("Failed to log event for type: {}, name: {}", type, name, ex);
        }
    }

    public static void logEvent(String type, String name, String status, String nameValuePairs) {
        try {
            getProducer().logEvent(type, name, status, nameValuePairs);
        } catch (Throwable ex) {
            logger.warn("Failed to log event for type: {}, name: {}, status: {}, nameValuePairs: {}",
                    type, name, status, nameValuePairs, ex);
        }
    }

    public static Transaction newTransaction(String type, String name) {
        try {
            return getProducer().newTransaction(type, name);
        } catch (Throwable ex) {
            logger.warn("Failed to create transaction for type: {}, name: {}", type, name, ex);
            return NULL_MESSAGE_PRODUCER_MANAGER.getProducer().newTransaction(type, name);
        }
    }
}
