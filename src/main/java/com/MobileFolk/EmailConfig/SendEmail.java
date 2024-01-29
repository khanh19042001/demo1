package com.MobileFolk.EmailConfig;

import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SendEmail implements ITestListener {

    @Override
    public void onFinish(ITestContext context) {

        // Gọi EmailSender ở đây sau khi tất cả các bài kiểm tra tự động đã hoàn thành

        String host = "smtp.gmail.com";
        String port = "465";
        String username = "minsoah1904@gmail.com";
        String password = "ytavfajofrmoiplq";//ytavfajofrmoiplq -qrifvnfdymfuifmp
        String from = "minsoah1904@gmail.com";

//        String[] recipients = {"ngan@mobilefolk.com","thao@mobilefolk.com","viet@mobilefolk.com","Khanhthi455@gmail.com"};
        String[] recipients = {"Khanhthi455@gmail.com"};

        Email email;
        email = new Email(host, port, username, password, from, recipients);
        EmailUtil emailSender = new EmailUtil();

        emailSender.sendEmail(email);
    }




}
