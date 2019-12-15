package com.fbi.engine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "appgrpc", ignoreUnknownFields = false)
@Component
@Data
public class GrpcProperties {

    private Tls tls = new Tls();
    private Long port;

    @Data
    public static class Tls {
        private boolean enabled;
        private String key;
        private String certificate;
        private String certChainFile;
        private String privateKeyFile;
        private String trustCertCollectionFile;
        private String cacheTrustCertCollectionFile;
        private String cacheClientCertChainFile;
        private String cacheClientPrivateKeyFile;
    }

}
