package vn.edu.fpt.be.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply CORS to all endpoints
                .allowedOrigins("http://178.128.89.58") // Allowed origin(s)
//                .allowedOrigins("http://mss.mom") // Allowed origin(s)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed methods
                .allowedHeaders("*") // Allowed headers
                .allowCredentials(true) // If you need cookies or authorization headers
                .maxAge(3600); // Cache duration for CORS preflight requests}
    }

}
