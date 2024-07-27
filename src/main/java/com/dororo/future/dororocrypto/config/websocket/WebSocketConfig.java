package com.dororo.future.dororocrypto.config.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置
 *
 * @author Dororo
 * @date 2023-11-26 15:01
 */
@Configuration
public class WebSocketConfig {
    /**
     * <p>
     * 在Spring应用程序中，`ServerEndpointExporter` 是一个Spring Bean，它负责自动注册带有 `@ServerEndpoint` 注解的WebSocket端点。这允许WebSocket端点像普通的Spring Bean一样被管理，同时能够使用Spring框架提供的依赖注入和其他特性。
     * ### 工作原理
     * - 当Spring应用程序启动时，`ServerEndpointExporter` 会查找所有带有 `@ServerEndpoint` 注解的类(有点类似于controller)，并将它们注册为WebSocket端点。
     * - 这样，每个被 `@ServerEndpoint` 注解标记的类都将被Spring容器管理，并自动注册为WebSocket端点。
     * - 这允许您在WebSocket端点中使用Spring的依赖注入、事务管理等功能。
     * -
     * ### 注意事项
     * - `ServerEndpointExporter` 主要用于Spring Boot内嵌的Tomcat容器中。如果您在独立的服务器容器（如Tomcat, Jetty）中部署Spring应用程序，通常不需要配置 `ServerEndpointExporter`，因为在这种情况下，服务器容器自身负责WebSocket端点的注册。
     * - 确保 `@ServerEndpoint` 注解的类被Spring管理（例如，通过在类上添加 `@Component` 注解）。
     * 通过使用 `ServerEndpointExporter`，Spring Boot应用程序可以轻松地集成WebSocket功能，同时利用Spring提供的强大特性。
     * </p>
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
