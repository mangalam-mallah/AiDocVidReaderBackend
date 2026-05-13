package com.example.DocBackend.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class AppConfig{

        @Bean
        public ChatClient chatClient(ChatModel chatModel) {
            return ChatClient.builder(chatModel)
                    .defaultSystem("You are a helpful AI assistant that answers questions based on provided document content.")
                    .build();
        }

        @Bean
        public WebMvcConfigurer corsConfigurer() {
            return new WebMvcConfigurer() {
                @Override
                public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
                    registry.addMapping("/api/**")
                            .allowedOrigins("*")
                            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
                }
            };
        }




}