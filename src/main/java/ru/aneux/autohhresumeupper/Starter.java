package ru.aneux.autohhresumeupper;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import ru.aneux.autohhresumeupper.pages.LoginPage;
import ru.aneux.autohhresumeupper.pages.ResumePage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import static java.lang.String.format;

public class Starter {
    private static final DateTimeFormatter timestampFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void main(String[] args) {
        Properties properties;
        try (InputStream input = Files.newInputStream(Paths.get("config.properties"))) {
            properties = new Properties();
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error occurred while reading configuration file", e);
        }

        String email = properties.getProperty("email"), password = properties.getProperty("password");
        int refreshInterval = Integer.parseInt(properties.getProperty("refresh_interval", "60"));
        log(format("Starting HH.ru resume updater for user '%s' (refresh interval %d min)", email, refreshInterval));

        refreshInterval *= 60 * 1000; // Need to use milliseconds here
        while (true) {
            up(email, password);
            try {
                Thread.sleep(refreshInterval);
            } catch (InterruptedException e) {
                System.out.println("Sleep has been interrupted. Will try to up resume now");
            }
        }
    }

    private static void up(String email, String password) {
        WebDriverManager.chromedriver().setup();

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        WebDriver driver = null;
        try {
            driver = new ChromeDriver(chromeOptions);
            driver.manage().window().maximize();

            driver.get("https://hh.ru/account/login");
            new LoginPage(driver).login(email, password);

            driver.navigate().to("https://hh.ru/applicant/resumes?hhtmFromLabel=header&hhtmFrom=main");
            // FIXME: for some reason navigating ones not work here
            driver.navigate().to("https://hh.ru/applicant/resumes?hhtmFromLabel=header&hhtmFrom=main");

            new ResumePage(driver).upResume();
            log("Resume has been successfully upped!");
        } catch (Exception e) {
            log("Couldn't up resume now. Waiting for next try..");
        } finally {
            if (driver != null)
                driver.quit();
        }
    }

    private static void log(String msg) {
        System.out.printf("[%s]: %s%n", LocalDateTime.now().format(timestampFormat), msg);
    }
}
