package com.fbi.engine.config;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GrpcClientProperties {

    private Tls tls = new Tls();

    @Getter
    @Setter
    public static class Tls {
        private boolean enabled = false;
        private String cacheTrustCertCollectionFile;
        private String cacheClientCertChainFile;
        private String cacheClientPrivateKeyFile;
    }

}
