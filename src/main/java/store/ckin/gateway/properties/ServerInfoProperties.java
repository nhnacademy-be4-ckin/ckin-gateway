package store.ckin.gateway.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ckin")
@Getter
@Setter
public class ServerInfoProperties {
    private String authUri;
    private String couponUri;
    private String apiUri;
}
