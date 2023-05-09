package com.example.helloworld;

import static java.util.Arrays.stream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.log4j.Log4j2;

@Log4j2 // Agregar un logger llamado log.
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
  }

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
        log.error("[Fatal] Missing or empty environment variable: {}", varName);

        System.exit(1);
      });
  }
}
