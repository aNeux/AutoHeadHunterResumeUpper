package ru.aneux.autohhresumeupper.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ResumePage {
    @FindBy(xpath = "//*[@id=\"HH-React-Root\"]/div/div[3]/div[1]/div/div/div[1]/div[3]/div[2]/div/div[6]/div/div/div[1]/div[1]/span/button")
    private WebElement upButton;

    @FindBy(xpath = "/html/body/div[11]/div/div[1]/div[2]/div[1]/button")
    private WebElement closeDialogButton;

    public ResumePage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public void upResume() {
        upButton.click();
        closeDialogButton.click();
    }
}
