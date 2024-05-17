package ar.edu.unlu.spgda.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import lombok.Value;

@Value
@ConstructorBinding
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

  private String clientOriginUrl;
  private String audience;
}
