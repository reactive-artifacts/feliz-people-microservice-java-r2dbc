package com.example.demor2dbc;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import io.netty.handler.timeout.ReadTimeoutHandler;
import reactor.netty.http.server.HttpServer;

@Configuration
public class CustomNettyWebServerFactory {

    @Bean 
    public NettyServerCustomizer nettyServerCustomizer() {
    	return new NettyfelizPeopleCustomizer();
    }
	
	private static class NettyfelizPeopleCustomizer implements NettyServerCustomizer {

        @Override
        public HttpServer apply(HttpServer httpServer) {
            return httpServer.doOnConnection(con->con.addHandler(new ReadTimeoutHandler(10, TimeUnit.SECONDS)));
        }
    }
}