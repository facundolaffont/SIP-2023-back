package com.example.helloworld;

import static java.util.Arrays.stream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

  // Verifica que el archivo .env esté completo e inicia la aplicación Spring.
  public static void main(final String[] args) {
    dotEnvSafeCheck();
    SpringApplication.run(HelloWorldApplication.class, args);
    logger.info("LISTO!");
  }


  /* Private */

  private static final Logger logger = LogManager.getLogger(HelloWorldApplication.class);

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
