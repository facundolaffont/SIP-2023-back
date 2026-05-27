package ar.edu.unlu.spgda.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class BackupService {

    private static final Logger logger = LoggerFactory.getLogger(BackupService.class);

    @Value("${backup.email.destination}")
    private String emailDestination;

    @Value("${backup.command}")
    private String backupCommand;

    @Autowired
    private JavaMailSender mailSender;

    // ENVÍO DE BACKUP VÍA MAIL DE FORMA PERÍODICA
    // @Scheduled(cron = "0 */3 * * * *")
    public void performBackupAndSendEmail() {
        logger.info("Iniciando proceso de backup de base de datos...");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File sqlFile = new File("backup_" + timestamp + ".sql");
        File zipFile = new File("backup_" + timestamp + ".zip");

        try {
            // 1. Ejecutar el comando de backup
            // Dividir el comando en partes para el ProcessBuilder
            String[] commandParts = backupCommand.split(" ");
            ProcessBuilder pb = new ProcessBuilder(commandParts);
            
            // Redirigir la salida estándar del proceso al archivo SQL
            pb.redirectOutput(sqlFile);
            
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                logger.error("El proceso de backup falló con código de salida: " + exitCode);
                return;
            }
            logger.info("Backup SQL generado correctamente: " + sqlFile.getName());

            // 2. Comprimir el archivo SQL a formato ZIP
            try (FileOutputStream fos = new FileOutputStream(zipFile);
                 ZipOutputStream zos = new ZipOutputStream(fos);
                 FileInputStream fis = new FileInputStream(sqlFile)) {

                ZipEntry zipEntry = new ZipEntry(sqlFile.getName());
                zos.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zos.write(bytes, 0, length);
                }
                logger.info("Archivo comprimido exitosamente a: " + zipFile.getName());
            }

            // 3. Enviar el correo electrónico con el archivo ZIP adjunto
            logger.info("Enviando backup por email a: " + emailDestination);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(emailDestination);
            helper.setSubject("Backup Semanal de Base de Datos - SPGDA");
            helper.setText("Adjunto se encuentra el backup semanal de la base de datos generado el " + timestamp + ".");

            FileSystemResource fileResource = new FileSystemResource(zipFile);
            helper.addAttachment(zipFile.getName(), fileResource);

            mailSender.send(message);
            logger.info("Email de backup enviado exitosamente.");

        } catch (Exception e) {
            logger.error("Error durante el proceso de backup y envío: ", e);
        } finally {
            // 4. Limpiar los archivos temporales
            try {
                if (sqlFile.exists()) {
                    Files.delete(sqlFile.toPath());
                }
                if (zipFile.exists()) {
                    Files.delete(zipFile.toPath());
                }
            } catch (Exception e) {
                logger.error("Error al eliminar archivos temporales: ", e);
            }
        }
    }
}
