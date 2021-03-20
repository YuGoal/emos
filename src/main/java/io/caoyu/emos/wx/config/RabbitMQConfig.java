package io.caoyu.emos.wx.config;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public ConnectionFactory getFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.99.101"); //Linux主机的IP地址
        factory.setPort(5672); //RabbitMQ端口号
        return factory;
    }
}


