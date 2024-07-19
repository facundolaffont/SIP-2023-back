package ar.edu.unlu.spgda;

import static java.util.Arrays.stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import ch.qos.logback.classic.Level;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SPGDA_Application {

  enum DotEnv {
    LOG_LEVEL,
    PORT,
    CLIENT_ORIGIN_URL,
    AUTH0_DOMAIN,
    AUTH0_AUDIENCE,
    AUTH0_APP_CLIENT_ID,
    AUTH0_APP_SECRET,
    AUTH0_DB_CONNECTION,
    AUTH0_ROLID_DOCENTE,
    POSTGRES_USER,
    POSTGRES_PASSWORD,
    POSTGRES_DB,
    POSTGRES_URL
  }

  
  public static void main(final String[] args) {

    // Verifica que el archivo .env esté completo.
    dotEnvSafeCheck();

    // Inicia la aplicación Spring.
    SpringApplication.run(SPGDA_Application.class, args);

    // Cambia el nivel de log.
    ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
    if (dotenv.get("LOG_LEVEL").equals("DEBUG"))
      root.setLevel(Level.DEBUG);
    logger.info("LISTO!");
  
  }


  /* Private */

  private static final Logger logger = LoggerFactory.getLogger(SPGDA_Application.class);
  private static Dotenv dotenv;

  // Verifica que las variables descritas en el Enum
  // DotEnv estén registradas en el archivo .env. Si alguna
  // no existe, termina la ejecución del programa, con valor
  // de salida 1.
  private static void dotEnvSafeCheck() {
    // final var dotenv = Dotenv.configure()
    dotenv = Dotenv.configure()
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
