package com.fbi.engine;

import com.fbi.engine.config.FlairCachingConfig;
import com.fbi.engine.config.GrpcServerProperties;
import com.fbi.engine.crypto.kdf.KdfType;
import com.fbi.engine.crypto.symmetric.SymmetricCipherType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Properties specific to Fbiengine.
 * <p>
 * Properties are configured in the application.yml file.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
@Getter
@Setter
public class ApplicationProperties {

    private final Database database = new Database();

    private final Authentication authentication = new Authentication();

    private final Configuration configuration = new Configuration();

    private final GrpcServerProperties grpc = new GrpcServerProperties();

    private final FlairCachingConfig flairCache = new FlairCachingConfig();

    @Getter
    @Setter
    public static class Configuration {


        private final Crypto crypto = new Crypto();

        @Getter
        @Setter
        public static class Crypto {

            private SymmetricCipherType symmetricCipherType = SymmetricCipherType.AES_256_GCM;

            private KdfType kdfType = KdfType.SCRYPT;

        }
    }


    @Getter
    @Setter
    public static class Database {

        private final Encryption encryption = new Encryption();

        @Getter
        @Setter
        public static class Encryption {

            private String passphrase;

            private String salt;

            private Integer keyLength;
        }

    }

    @Getter
    @Setter
    public static class Authentication {

        private final FlairBi flairBi = new FlairBi();

        @Getter
        @Setter
        public static class FlairBi {

            private final Pki pki = new Pki();

            private final BasicAuthentication basicAuthentication = new BasicAuthentication();

            @Getter
            @Setter
            public static class Pki {

                boolean enabled;
                private List<String> subjects = new ArrayList<>();

            }

            @Getter
            @Setter
            public static class BasicAuthentication {

                private boolean enabled;

                private List<Credentials> credentials = new ArrayList<>();

                @Getter
                @Setter
                public static class Credentials {

                    private String username;

                    private String password;

                    private List<String> roles;
                }
            }


        }
    }

}
