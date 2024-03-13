package at.tugraz.oop2;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PreDestroy;
import java.awt.image.BufferedImage;
import java.util.Collections;

@RestController
@SpringBootApplication
public class MapApplication {
    public static ManagedChannel channel;
    public static GeographyGrpc.GeographyBlockingStub blockingStub;
    public static final Integer TAKE_DEFAULT = 50;
    public static final Integer SKIP_DEFAULT = 0;

    private static final String JMAP_MIDDLEWARE_PORT = "JMAP_MIDDLEWARE_PORT";
    private static final Integer JMAP_MIDDLEWARE_PORT_DEFAULT = 8010;
    private static final String JMAP_BACKEND_TARGET = "JMAP_BACKEND_TARGET";
    private static final String JMAP_BACKEND_TARGET_DEFAULT = "localhost:8020";

    public static void main(String[] args) {

        String middlewarePortEnv = System.getenv().getOrDefault(JMAP_MIDDLEWARE_PORT, "" + JMAP_MIDDLEWARE_PORT_DEFAULT);

        Integer middlewarePort = JMAP_MIDDLEWARE_PORT_DEFAULT;
        try {
            middlewarePort = Integer.parseUnsignedInt(middlewarePortEnv);

        } catch (NumberFormatException e) {
            //do nothing
        }

        String backendTarget = System.getenv().getOrDefault(JMAP_BACKEND_TARGET, JMAP_BACKEND_TARGET_DEFAULT);


        var app = new SpringApplication(MapApplication.class);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", middlewarePort));
        app.run();

        channel = ManagedChannelBuilder.forTarget(backendTarget)
                .usePlaintext()
                .build();
        blockingStub = GeographyGrpc.newBlockingStub(channel);

        MapLogger.middlewareStartup(middlewarePort, backendTarget);

    }

    @Bean
    public HttpMessageConverter<BufferedImage> bufferedImageHttpMessageConverter() {
        return new BufferedImageHttpMessageConverter();
    }

    @PreDestroy
    public void onExit() {
        if (channel != null) {
            channel.shutdown();
        }
    }
}