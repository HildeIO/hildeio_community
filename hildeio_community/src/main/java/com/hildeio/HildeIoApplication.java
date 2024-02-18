package com.hildeio;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/***********************************************************************************************
 * @author cdr - support@hildeio.com
 * @since 20.01.2024
 * @version 24.1.13
 *
 * Die Startklasse HildeIoApplication erbt von der Klasse SpringBootServletInitializer.
 * Wird HildeIO unter einem Apache Tomcat 10 (.war) ausgefuehrt ist SpringBootServletInitializer erforderlich.
 * 
 * Zum Deployen der war-Datei unter Linux muss der Apache Tomcat 10 neu gestartet werden. 
 * 
 * sudo systemctl stop tomcat
 * sudo systemctl status tomcat 
 * sudo systemctl start tomcat
 * 
 * 
 ***********************************************************************************************/
@SpringBootApplication
public class HildeIoApplication extends SpringBootServletInitializer {

	/***********************************************************************************************
	 * Die Startmethode main() ist die einzige Methode dieser Startklasse.
	 * 
	 * @param args Aufrufparameter werden keine erwartet.
	 * @throws BeansException Erforderliche Exception.
	 ***********************************************************************************************/
	public static void main(String[] args) throws BeansException {
		SpringApplication.run(HildeIoApplication.class, args);
	}
}

