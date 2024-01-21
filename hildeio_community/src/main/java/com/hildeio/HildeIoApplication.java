package com.hildeio;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 
 * @author cdr - support@hildeio.com
 * @since 20.01.2024
 * @version 24.1.13
 *
 *
 * <p>Die Startklasse HildeIoApplication erbt von der Klasse SpringBootServletInitializer.<br>
 * Wird HildeIO unter einem Apache Tomcat 10 (.war) ausgefuehrt ist SpringBootServletInitializer erforderlich.</p>
 * 
 * <p>Zum Deployen der war-Datei unter Linux muss der Apache Tomcat 10 neu gestartet werden.</p> 
 * <p>
 * <b>sudo systemctl stop tomcat</b><br>
 * <b>sudo systemctl status tomcat</b><br> 
 * <b>sudo systemctl start tomcat</b><br>
 * </p>
 * <p>
 * Unter <b><i>{deine_hildeio_url}</i>/api/swagger-ui/index.html"></b> ist die Swagger-API verfuegbar.
 * </p> 
 */
@SpringBootApplication
public class HildeIoApplication extends SpringBootServletInitializer {

	/**
	 * Die Startmethode main() ist die einzige Methode dieser Startklasse.
	 * 
	 * @param args Aufrufparameter werden keine erwartet.
	 * @throws BeansException Erforderliche Exception.
	 */
	public static void main(String[] args) throws BeansException {
		SpringApplication.run(HildeIoApplication.class, args);
	}
}

