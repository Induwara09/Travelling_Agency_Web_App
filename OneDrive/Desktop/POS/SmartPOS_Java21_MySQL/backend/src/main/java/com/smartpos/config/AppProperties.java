package com.smartpos.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "smartpos")
public class AppProperties {
    private Jwt jwt = new Jwt();
    private Business business = new Business();
    private Bootstrap bootstrap = new Bootstrap();
    private Reports reports = new Reports();
    private Backup backup = new Backup();
    private Device device = new Device();
    private Sync sync = new Sync();

    @Getter @Setter public static class Jwt { private String secret; private long expirationMinutes = 720; }
    @Getter @Setter public static class Business {
        private String name; private String address; private String phone; private String email;
        private String currency = "LKR"; private String invoicePrefix = "INV";
        private BigDecimal serviceChargePercent = BigDecimal.ZERO; private BigDecimal taxPercent = BigDecimal.ZERO;
    }
    @Getter @Setter public static class Bootstrap { private String adminPassword; private String managerPassword; private String cashierPassword; }
    @Getter @Setter public static class Reports { private String monthlyCron; private String recipients; }
    @Getter @Setter public static class Backup { private boolean enabled = true; private String directory; private String cron; private String mysqldump = "mysqldump"; }
    @Getter @Setter public static class Device { private String agentKey; }
    @Getter @Setter public static class Sync { private boolean enabled; private String endpoint; private String apiKey; }
}
