package com.example.helloworld;

import static java.util.Arrays.stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@ConfigurationPropertiesScan
public class HelloWorldApplication {

  enum DotEnv {
    PORT,
    CLIENT_ORIGIN_URL,
    AUTH0_DOMAIN,
    AUTH0_AUDIENCE,
    POSTGRES_USER,
    POSTGRES_PASSWORD,
    POSTGRES_DB,
    POSTGRES_URL
  }

  
  public static void main(final String[] args) {

    // Verifica que el archivo .env esté completo.
    dotEnvSafeCheck();

    SpringApplication.run(HelloWorldApplication.class, args);
    logger.info("LISTO!");

  }


  /* Private */

  private static final Logger logger = LoggerFactory.getLogger(HelloWorldApplication.class);


  // Verifica que las variables descritas en el Enum
  // DotEnv estén registradas en el archivo .env. Si alguna
  // no existe, termina la ejecución del programa, con valor
  // de salida 1.
  private static void dotEnvSafeCheck() {
    final var dotenv = Dotenv.configure()
      .ignoreIfMissing()
      .load();

    stream(DotEnv.values())
      .map(DotEnv::name)
      .filter(varName -> dotenv.get(varName, "").isEmpty())
      .findFirst()
      .ifPresent(varName -> {
        logger.error("[Fatal] Missing or empty environment variable: {}", varName);

        System.exit(1);
      });
  }
}
