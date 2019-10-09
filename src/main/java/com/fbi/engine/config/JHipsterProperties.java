package com.fbi.engine.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(
        prefix = "jhipster",
        ignoreUnknownFields = false
)
public class JHipsterProperties {
    private final JHipsterProperties.Async async = new JHipsterProperties.Async();
    private final JHipsterProperties.Http http = new JHipsterProperties.Http();
    private final JHipsterProperties.Cache cache = new JHipsterProperties.Cache();
    private final JHipsterProperties.Mail mail = new JHipsterProperties.Mail();
    private final JHipsterProperties.Security security = new JHipsterProperties.Security();
    private final JHipsterProperties.Swagger swagger = new JHipsterProperties.Swagger();
    private final JHipsterProperties.Metrics metrics = new JHipsterProperties.Metrics();
    private final JHipsterProperties.Logging logging = new JHipsterProperties.Logging();
    private final CorsConfiguration cors = new CorsConfiguration();
    private final JHipsterProperties.Social social = new JHipsterProperties.Social();
    private final JHipsterProperties.Gateway gateway = new JHipsterProperties.Gateway();
    private final JHipsterProperties.Ribbon ribbon = new JHipsterProperties.Ribbon();
    private final JHipsterProperties.Registry registry = new JHipsterProperties.Registry();

    public JHipsterProperties() {
    }

    public JHipsterProperties.Async getAsync() {
        return this.async;
    }

    public JHipsterProperties.Http getHttp() {
        return this.http;
    }

    public JHipsterProperties.Cache getCache() {
        return this.cache;
    }

    public JHipsterProperties.Mail getMail() {
        return this.mail;
    }

    public JHipsterProperties.Registry getRegistry() {
        return this.registry;
    }

    public JHipsterProperties.Security getSecurity() {
        return this.security;
    }

    public JHipsterProperties.Swagger getSwagger() {
        return this.swagger;
    }

    public JHipsterProperties.Metrics getMetrics() {
        return this.metrics;
    }

    public JHipsterProperties.Logging getLogging() {
        return this.logging;
    }

    public CorsConfiguration getCors() {
        return this.cors;
    }

    public JHipsterProperties.Social getSocial() {
        return this.social;
    }

    public JHipsterProperties.Gateway getGateway() {
        return this.gateway;
    }

    public JHipsterProperties.Ribbon getRibbon() {
        return this.ribbon;
    }

    public static class Registry {
        private String password;

        public Registry() {
            this.password = JHipsterDefaults.Registry.password;
        }

        public String getPassword() {
            return this.password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class Ribbon {
        private String[] displayOnActiveProfiles;

        public Ribbon() {
            this.displayOnActiveProfiles = JHipsterDefaults.Ribbon.displayOnActiveProfiles;
        }

        public String[] getDisplayOnActiveProfiles() {
            return this.displayOnActiveProfiles;
        }

        public void setDisplayOnActiveProfiles(String[] displayOnActiveProfiles) {
            this.displayOnActiveProfiles = displayOnActiveProfiles;
        }
    }

    public static class Gateway {
        private final JHipsterProperties.Gateway.RateLimiting rateLimiting = new JHipsterProperties.Gateway.RateLimiting();
        private Map<String, List<String>> authorizedMicroservicesEndpoints;

        public Gateway() {
            this.authorizedMicroservicesEndpoints = JHipsterDefaults.Gateway.authorizedMicroservicesEndpoints;
        }

        public JHipsterProperties.Gateway.RateLimiting getRateLimiting() {
            return this.rateLimiting;
        }

        public Map<String, List<String>> getAuthorizedMicroservicesEndpoints() {
            return this.authorizedMicroservicesEndpoints;
        }

        public void setAuthorizedMicroservicesEndpoints(Map<String, List<String>> authorizedMicroservicesEndpoints) {
            this.authorizedMicroservicesEndpoints = authorizedMicroservicesEndpoints;
        }

        public static class RateLimiting {
            private boolean enabled = false;
            private long limit = 100000L;
            private int durationInSeconds = 3600;

            public RateLimiting() {
            }

            public boolean isEnabled() {
                return this.enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public long getLimit() {
                return this.limit;
            }

            public void setLimit(long limit) {
                this.limit = limit;
            }

            public int getDurationInSeconds() {
                return this.durationInSeconds;
            }

            public void setDurationInSeconds(int durationInSeconds) {
                this.durationInSeconds = durationInSeconds;
            }
        }
    }

    public static class Social {
        private String redirectAfterSignIn = "/#/home";

        public Social() {
        }

        public String getRedirectAfterSignIn() {
            return this.redirectAfterSignIn;
        }

        public void setRedirectAfterSignIn(String redirectAfterSignIn) {
            this.redirectAfterSignIn = redirectAfterSignIn;
        }
    }

    public static class Logging {
        private final JHipsterProperties.Logging.Logstash logstash = new JHipsterProperties.Logging.Logstash();
        private final JHipsterProperties.Logging.SpectatorMetrics spectatorMetrics = new JHipsterProperties.Logging.SpectatorMetrics();

        public Logging() {
        }

        public JHipsterProperties.Logging.Logstash getLogstash() {
            return this.logstash;
        }

        public JHipsterProperties.Logging.SpectatorMetrics getSpectatorMetrics() {
            return this.spectatorMetrics;
        }

        public static class SpectatorMetrics {
            private boolean enabled = false;

            public SpectatorMetrics() {
            }

            public boolean isEnabled() {
                return this.enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }
        }

        public static class Logstash {
            private boolean enabled = false;
            private String host = "localhost";
            private int port = 5000;
            private int queueSize = 512;

            public Logstash() {
            }

            public boolean isEnabled() {
                return this.enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public String getHost() {
                return this.host;
            }

            public void setHost(String host) {
                this.host = host;
            }

            public int getPort() {
                return this.port;
            }

            public void setPort(int port) {
                this.port = port;
            }

            public int getQueueSize() {
                return this.queueSize;
            }

            public void setQueueSize(int queueSize) {
                this.queueSize = queueSize;
            }
        }
    }

    public static class Metrics {
        private final JHipsterProperties.Metrics.Jmx jmx = new JHipsterProperties.Metrics.Jmx();
        private final JHipsterProperties.Metrics.Graphite graphite = new JHipsterProperties.Metrics.Graphite();
        private final JHipsterProperties.Metrics.Prometheus prometheus = new JHipsterProperties.Metrics.Prometheus();
        private final JHipsterProperties.Metrics.Logs logs = new JHipsterProperties.Metrics.Logs();

        public Metrics() {
        }

        public JHipsterProperties.Metrics.Jmx getJmx() {
            return this.jmx;
        }

        public JHipsterProperties.Metrics.Graphite getGraphite() {
            return this.graphite;
        }

        public JHipsterProperties.Metrics.Prometheus getPrometheus() {
            return this.prometheus;
        }

        public JHipsterProperties.Metrics.Logs getLogs() {
            return this.logs;
        }

        public static class Logs {
            private boolean enabled = false;
            private long reportFrequency = 60L;

            public Logs() {
            }

            public boolean isEnabled() {
                return this.enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public long getReportFrequency() {
                return this.reportFrequency;
            }

            public void setReportFrequency(long reportFrequency) {
                this.reportFrequency = reportFrequency;
            }
        }

        public static class Prometheus {
            private boolean enabled = false;
            private String endpoint = "/prometheusMetrics";

            public Prometheus() {
            }

            public boolean isEnabled() {
                return this.enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public String getEndpoint() {
                return this.endpoint;
            }

            public void setEndpoint(String endpoint) {
                this.endpoint = endpoint;
            }
        }

        public static class Graphite {
            private boolean enabled = false;
            private String host = "localhost";
            private int port = 2003;
            private String prefix = "jhipsterApplication";

            public Graphite() {
            }

            public boolean isEnabled() {
                return this.enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public String getHost() {
                return this.host;
            }

            public void setHost(String host) {
                this.host = host;
            }

            public int getPort() {
                return this.port;
            }

            public void setPort(int port) {
                this.port = port;
            }

            public String getPrefix() {
                return this.prefix;
            }

            public void setPrefix(String prefix) {
                this.prefix = prefix;
            }
        }

        public static class Jmx {
            private boolean enabled = true;

            public Jmx() {
            }

            public boolean isEnabled() {
                return this.enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }
        }
    }

    public static class Swagger {
        private String title = "Application API";
        private String description = "API documentation";
        private String version = "0.0.1";
        private String termsOfServiceUrl;
        private String contactName;
        private String contactUrl;
        private String contactEmail;
        private String license;
        private String licenseUrl;
        private String defaultIncludePattern;
        private String host;
        private String[] protocols;

        public Swagger() {
            this.termsOfServiceUrl = JHipsterDefaults.Swagger.termsOfServiceUrl;
            this.contactName = JHipsterDefaults.Swagger.contactName;
            this.contactUrl = JHipsterDefaults.Swagger.contactUrl;
            this.contactEmail = JHipsterDefaults.Swagger.contactEmail;
            this.license = JHipsterDefaults.Swagger.license;
            this.licenseUrl = JHipsterDefaults.Swagger.licenseUrl;
            this.defaultIncludePattern = "/api/.*";
            this.host = JHipsterDefaults.Swagger.host;
            this.protocols = JHipsterDefaults.Swagger.protocols;
        }

        public String getTitle() {
            return this.title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return this.description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getVersion() {
            return this.version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getTermsOfServiceUrl() {
            return this.termsOfServiceUrl;
        }

        public void setTermsOfServiceUrl(String termsOfServiceUrl) {
            this.termsOfServiceUrl = termsOfServiceUrl;
        }

        public String getContactName() {
            return this.contactName;
        }

        public void setContactName(String contactName) {
            this.contactName = contactName;
        }

        public String getContactUrl() {
            return this.contactUrl;
        }

        public void setContactUrl(String contactUrl) {
            this.contactUrl = contactUrl;
        }

        public String getContactEmail() {
            return this.contactEmail;
        }

        public void setContactEmail(String contactEmail) {
            this.contactEmail = contactEmail;
        }

        public String getLicense() {
            return this.license;
        }

        public void setLicense(String license) {
            this.license = license;
        }

        public String getLicenseUrl() {
            return this.licenseUrl;
        }

        public void setLicenseUrl(String licenseUrl) {
            this.licenseUrl = licenseUrl;
        }

        public String getDefaultIncludePattern() {
            return this.defaultIncludePattern;
        }

        public void setDefaultIncludePattern(String defaultIncludePattern) {
            this.defaultIncludePattern = defaultIncludePattern;
        }

        public String getHost() {
            return this.host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String[] getProtocols() {
            return this.protocols;
        }

        public void setProtocols(String[] protocols) {
            this.protocols = protocols;
        }
    }

    public static class Security {
        private final JHipsterProperties.Security.ClientAuthorization clientAuthorization = new JHipsterProperties.Security.ClientAuthorization();
        private final JHipsterProperties.Security.Authentication authentication = new JHipsterProperties.Security.Authentication();
        private final JHipsterProperties.Security.RememberMe rememberMe = new JHipsterProperties.Security.RememberMe();

        public Security() {
        }

        public JHipsterProperties.Security.ClientAuthorization getClientAuthorization() {
            return this.clientAuthorization;
        }

        public JHipsterProperties.Security.Authentication getAuthentication() {
            return this.authentication;
        }

        public JHipsterProperties.Security.RememberMe getRememberMe() {
            return this.rememberMe;
        }

        public static class RememberMe {
            @NotNull
            private String key;

            public RememberMe() {
                this.key = JHipsterDefaults.Security.RememberMe.key;
            }

            public String getKey() {
                return this.key;
            }

            public void setKey(String key) {
                this.key = key;
            }
        }

        public static class Authentication {
            private final JHipsterProperties.Security.Authentication.Jwt jwt = new JHipsterProperties.Security.Authentication.Jwt();

            public Authentication() {
            }

            public JHipsterProperties.Security.Authentication.Jwt getJwt() {
                return this.jwt;
            }

            public static class Jwt {
                private String secret;
                private long tokenValidityInSeconds;
                private long tokenValidityInSecondsForRememberMe;

                public Jwt() {
                    this.secret = JHipsterDefaults.Security.Authentication.Jwt.secret;
                    this.tokenValidityInSeconds = 1800L;
                    this.tokenValidityInSecondsForRememberMe = 2592000L;
                }

                public String getSecret() {
                    return this.secret;
                }

                public void setSecret(String secret) {
                    this.secret = secret;
                }

                public long getTokenValidityInSeconds() {
                    return this.tokenValidityInSeconds;
                }

                public void setTokenValidityInSeconds(long tokenValidityInSeconds) {
                    this.tokenValidityInSeconds = tokenValidityInSeconds;
                }

                public long getTokenValidityInSecondsForRememberMe() {
                    return this.tokenValidityInSecondsForRememberMe;
                }

                public void setTokenValidityInSecondsForRememberMe(long tokenValidityInSecondsForRememberMe) {
                    this.tokenValidityInSecondsForRememberMe = tokenValidityInSecondsForRememberMe;
                }
            }
        }

        public static class ClientAuthorization {
            private String accessTokenUri;
            private String tokenServiceId;
            private String clientId;
            private String clientSecret;

            public ClientAuthorization() {
                this.accessTokenUri = JHipsterDefaults.Security.ClientAuthorization.accessTokenUri;
                this.tokenServiceId = JHipsterDefaults.Security.ClientAuthorization.tokenServiceId;
                this.clientId = JHipsterDefaults.Security.ClientAuthorization.clientId;
                this.clientSecret = JHipsterDefaults.Security.ClientAuthorization.clientSecret;
            }

            public String getAccessTokenUri() {
                return this.accessTokenUri;
            }

            public void setAccessTokenUri(String accessTokenUri) {
                this.accessTokenUri = accessTokenUri;
            }

            public String getTokenServiceId() {
                return this.tokenServiceId;
            }

            public void setTokenServiceId(String tokenServiceId) {
                this.tokenServiceId = tokenServiceId;
            }

            public String getClientId() {
                return this.clientId;
            }

            public void setClientId(String clientId) {
                this.clientId = clientId;
            }

            public String getClientSecret() {
                return this.clientSecret;
            }

            public void setClientSecret(String clientSecret) {
                this.clientSecret = clientSecret;
            }
        }
    }

    public static class Mail {
        private String from = "";
        private String baseUrl = "";

        public Mail() {
        }

        public String getFrom() {
            return this.from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getBaseUrl() {
            return this.baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
    }

    public static class Cache {
        private final JHipsterProperties.Cache.Hazelcast hazelcast = new JHipsterProperties.Cache.Hazelcast();
        private final JHipsterProperties.Cache.Ehcache ehcache = new JHipsterProperties.Cache.Ehcache();
        private final JHipsterProperties.Cache.Infinispan infinispan = new JHipsterProperties.Cache.Infinispan();

        public Cache() {
        }

        public JHipsterProperties.Cache.Hazelcast getHazelcast() {
            return this.hazelcast;
        }

        public JHipsterProperties.Cache.Ehcache getEhcache() {
            return this.ehcache;
        }

        public JHipsterProperties.Cache.Infinispan getInfinispan() {
            return this.infinispan;
        }

        public static class Infinispan {
            private String configFile = "default-configs/default-jgroups-tcp.xml";
            private boolean statsEnabled = false;
            private final JHipsterProperties.Cache.Infinispan.Local local = new JHipsterProperties.Cache.Infinispan.Local();
            private final JHipsterProperties.Cache.Infinispan.Distributed distributed = new JHipsterProperties.Cache.Infinispan.Distributed();
            private final JHipsterProperties.Cache.Infinispan.Replicated replicated = new JHipsterProperties.Cache.Infinispan.Replicated();

            public Infinispan() {
            }

            public String getConfigFile() {
                return this.configFile;
            }

            public void setConfigFile(String configFile) {
                this.configFile = configFile;
            }

            public boolean isStatsEnabled() {
                return this.statsEnabled;
            }

            public void setStatsEnabled(boolean statsEnabled) {
                this.statsEnabled = statsEnabled;
            }

            public JHipsterProperties.Cache.Infinispan.Local getLocal() {
                return this.local;
            }

            public JHipsterProperties.Cache.Infinispan.Distributed getDistributed() {
                return this.distributed;
            }

            public JHipsterProperties.Cache.Infinispan.Replicated getReplicated() {
                return this.replicated;
            }

            public static class Replicated {
                private long timeToLiveSeconds = 60L;
                private long maxEntries = 100L;

                public Replicated() {
                }

                public long getTimeToLiveSeconds() {
                    return this.timeToLiveSeconds;
                }

                public void setTimeToLiveSeconds(long timeToLiveSeconds) {
                    this.timeToLiveSeconds = timeToLiveSeconds;
                }

                public long getMaxEntries() {
                    return this.maxEntries;
                }

                public void setMaxEntries(long maxEntries) {
                    this.maxEntries = maxEntries;
                }
            }

            public static class Distributed {
                private long timeToLiveSeconds = 60L;
                private long maxEntries = 100L;
                private int instanceCount = 1;

                public Distributed() {
                }

                public long getTimeToLiveSeconds() {
                    return this.timeToLiveSeconds;
                }

                public void setTimeToLiveSeconds(long timeToLiveSeconds) {
                    this.timeToLiveSeconds = timeToLiveSeconds;
                }

                public long getMaxEntries() {
                    return this.maxEntries;
                }

                public void setMaxEntries(long maxEntries) {
                    this.maxEntries = maxEntries;
                }

                public int getInstanceCount() {
                    return this.instanceCount;
                }

                public void setInstanceCount(int instanceCount) {
                    this.instanceCount = instanceCount;
                }
            }

            public static class Local {
                private long timeToLiveSeconds = 60L;
                private long maxEntries = 100L;

                public Local() {
                }

                public long getTimeToLiveSeconds() {
                    return this.timeToLiveSeconds;
                }

                public void setTimeToLiveSeconds(long timeToLiveSeconds) {
                    this.timeToLiveSeconds = timeToLiveSeconds;
                }

                public long getMaxEntries() {
                    return this.maxEntries;
                }

                public void setMaxEntries(long maxEntries) {
                    this.maxEntries = maxEntries;
                }
            }
        }

        public static class Ehcache {
            private int timeToLiveSeconds = 3600;
            private long maxEntries = 100L;

            public Ehcache() {
            }

            public int getTimeToLiveSeconds() {
                return this.timeToLiveSeconds;
            }

            public void setTimeToLiveSeconds(int timeToLiveSeconds) {
                this.timeToLiveSeconds = timeToLiveSeconds;
            }

            public long getMaxEntries() {
                return this.maxEntries;
            }

            public void setMaxEntries(long maxEntries) {
                this.maxEntries = maxEntries;
            }
        }

        public static class Hazelcast {
            private int timeToLiveSeconds = 3600;
            private int backupCount = 1;
            private final JHipsterProperties.Cache.Hazelcast.ManagementCenter managementCenter = new JHipsterProperties.Cache.Hazelcast.ManagementCenter();

            public Hazelcast() {
            }

            public JHipsterProperties.Cache.Hazelcast.ManagementCenter getManagementCenter() {
                return this.managementCenter;
            }

            public int getTimeToLiveSeconds() {
                return this.timeToLiveSeconds;
            }

            public void setTimeToLiveSeconds(int timeToLiveSeconds) {
                this.timeToLiveSeconds = timeToLiveSeconds;
            }

            public int getBackupCount() {
                return this.backupCount;
            }

            public void setBackupCount(int backupCount) {
                this.backupCount = backupCount;
            }

            public static class ManagementCenter {
                private boolean enabled = false;
                private int updateInterval = 3;
                private String url = "";

                public ManagementCenter() {
                }

                public boolean isEnabled() {
                    return this.enabled;
                }

                public void setEnabled(boolean enabled) {
                    this.enabled = enabled;
                }

                public int getUpdateInterval() {
                    return this.updateInterval;
                }

                public void setUpdateInterval(int updateInterval) {
                    this.updateInterval = updateInterval;
                }

                public String getUrl() {
                    return this.url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }
            }
        }
    }

    public static class Http {
        private final JHipsterProperties.Http.Cache cache = new JHipsterProperties.Http.Cache();
        public JHipsterProperties.Http.Version version;

        public Http() {
            this.version = Version.V_1_1;
        }

        public JHipsterProperties.Http.Cache getCache() {
            return this.cache;
        }

        public JHipsterProperties.Http.Version getVersion() {
            return this.version;
        }

        public void setVersion(JHipsterProperties.Http.Version version) {
            this.version = version;
        }

        public static class Cache {
            private int timeToLiveInDays = 1461;

            public Cache() {
            }

            public int getTimeToLiveInDays() {
                return this.timeToLiveInDays;
            }

            public void setTimeToLiveInDays(int timeToLiveInDays) {
                this.timeToLiveInDays = timeToLiveInDays;
            }
        }

        public static enum Version {
            V_1_1,
            V_2_0;

            private Version() {
            }
        }
    }

    public static class Async {
        private int corePoolSize = 2;
        private int maxPoolSize = 50;
        private int queueCapacity = 10000;

        public Async() {
        }

        public int getCorePoolSize() {
            return this.corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public int getMaxPoolSize() {
            return this.maxPoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public int getQueueCapacity() {
            return this.queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }
    }
}
