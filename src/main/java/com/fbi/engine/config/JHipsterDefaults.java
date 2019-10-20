//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.fbi.engine.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface JHipsterDefaults {
    public interface Registry {
        String password = null;
    }

    public interface Ribbon {
        String[] displayOnActiveProfiles = null;
    }

    public interface Gateway {
        Map<String, List<String>> authorizedMicroservicesEndpoints = new LinkedHashMap();

        public interface RateLimiting {
            boolean enabled = false;
            long limit = 100000L;
            int durationInSeconds = 3600;
        }
    }

    public interface Social {
        String redirectAfterSignIn = "/#/home";
    }

    public interface Logging {
        public interface SpectatorMetrics {
            boolean enabled = false;
        }

        public interface Logstash {
            boolean enabled = false;
            String host = "localhost";
            int port = 5000;
            int queueSize = 512;
        }
    }

    public interface Metrics {
        public interface Logs {
            boolean enabled = false;
            long reportFrequency = 60L;
        }

        public interface Prometheus {
            boolean enabled = false;
            String endpoint = "/prometheusMetrics";
        }

        public interface Graphite {
            boolean enabled = false;
            String host = "localhost";
            int port = 2003;
            String prefix = "jhipsterApplication";
        }

        public interface Jmx {
            boolean enabled = true;
        }
    }

    public interface Swagger {
        String title = "Application API";
        String description = "API documentation";
        String version = "0.0.1";
        String termsOfServiceUrl = null;
        String contactName = null;
        String contactUrl = null;
        String contactEmail = null;
        String license = null;
        String licenseUrl = null;
        String defaultIncludePattern = "/api/.*";
        String host = null;
        String[] protocols = new String[0];
    }

    public interface Security {
        public interface RememberMe {
            String key = null;
        }

        public interface Authentication {
            public interface Jwt {
                String secret = null;
                long tokenValidityInSeconds = 1800L;
                long tokenValidityInSecondsForRememberMe = 2592000L;
            }
        }

        public interface ClientAuthorization {
            String accessTokenUri = null;
            String tokenServiceId = null;
            String clientId = null;
            String clientSecret = null;
        }
    }

    public interface Mail {
        String from = "";
        String baseUrl = "";
    }

    public interface Cache {
        public interface Infinispan {
            String configFile = "default-configs/default-jgroups-tcp.xml";
            boolean statsEnabled = false;

            public interface Replicated {
                long timeToLiveSeconds = 60L;
                long maxEntries = 100L;
            }

            public interface Distributed {
                long timeToLiveSeconds = 60L;
                long maxEntries = 100L;
                int instanceCount = 1;
            }

            public interface Local {
                long timeToLiveSeconds = 60L;
                long maxEntries = 100L;
            }
        }

        public interface Ehcache {
            int timeToLiveSeconds = 3600;
            long maxEntries = 100L;
        }

        public interface Hazelcast {
            int timeToLiveSeconds = 3600;
            int backupCount = 1;

            public interface ManagementCenter {
                boolean enabled = false;
                int updateInterval = 3;
                String url = "";
            }
        }
    }

    public interface Http {
        public interface Cache {
            int timeToLiveInDays = 1461;
        }
    }

    public interface Async {
        int corePoolSize = 2;
        int maxPoolSize = 50;
        int queueCapacity = 10000;
    }
}
