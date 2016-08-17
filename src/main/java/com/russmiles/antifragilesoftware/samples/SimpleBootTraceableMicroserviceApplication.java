package com.russmiles.antifragilesoftware.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.integration.amqp.outbound.AmqpOutboundEndpoint;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableDiscoveryClient
@EnableAspectJAutoProxy(proxyTargetClass = true)
@IntegrationComponentScan
@SpringBootApplication
@EnableBinding(Source.class)
@RestController
public class SimpleBootTraceableMicroserviceApplication {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Bean
    public AlwaysSampler defaultSampler() {
        return new AlwaysSampler();
    }

    @Autowired
    private MyMessagingGateway messagingGateway;

//    @Bean
//    @ServiceActivator(inputChannel = "amqpMessageChannel")
//    public AmqpOutboundEndpoint amqpOutbound(AmqpTemplate amqpTemplate) {
//        AmqpOutboundEndpoint outbound = new AmqpOutboundEndpoint(amqpTemplate);
//        outbound.setRoutingKey("amqpMessageChannel"); // default exchange - route to queue 'amqpMessageChannel'
//        return outbound;
//    }

//    @Bean
//    public MessageChannel amqpMessageChannel() {
//        return new DirectChannel();
//    }

    @RequestMapping("/")
    public String home() {
        log.info("Root URL invoked");

        messagingGateway.send("Sending:" + this.toString() + " instance saying: Hello Microservice World\n");

        return this.toString() + " instance saying: Hello Microservice World\n";
    }

    public static void main(String[] args) {
        SpringApplication.run(SimpleBootTraceableMicroserviceApplication.class, args);
    }
}

@MessagingGateway
interface MyMessagingGateway {

    @Gateway(requestChannel = Source.OUTPUT)
    void send(String data);

}
