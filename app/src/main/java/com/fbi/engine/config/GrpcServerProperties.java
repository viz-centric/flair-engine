package com.fbi.engine.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GrpcServerProperties {

    private Tls tls = new Tls();

    @Getter
    @Setter
    public static class Tls {
        private boolean enabled = false;
        private String key;
        private String certificate;
        private String certChainFile;
        private String privateKeyFile;
        private String trustCertCollectionFile;
    }

}