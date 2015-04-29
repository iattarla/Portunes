/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portunesv2;


import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author gunebakan
 */
public class Exception {
    private final String from_mail= "ufakisleratolyesi@gmail.com";;
    private final String from_password = "oncupiknik";
    private final String to_mail = "bilaltonga@gmail.com";
    private final String to_subject = "Portunes Exception";
    
    public Exception(Exception ex,String error_class){
        
        sendEmail("Portunes programı bir hata yürüttü.Lütfen kullanıcı ile iletişime geçin\n"+
                  "hata bilgileri aşağıdadır.\n\n" +
                "--------------------------\n"+
                  ex.getClass() + "  " + error_class + "\n" +
                "--------------------------\n\n"+
                  "Exception type: Exception\n" +
                "--------------------------\n\n"+
                ex.toString() +
                "\n\n\n" +
                "--------------------------\n"+
                "bu bir robot maildir."
        
        );

    }
    public Exception(SQLException ex,String error_class){
        sendEmail("Portunes programı bir hata yürüttü.Lütfen kullanıcı ile iletişime geçin\n"+
                  "hata bilgileri aşağıdadır.\n\n" +
                "--------------------------\n"+
                  ex.getClass() + "  " + error_class + "\n" +
                "--------------------------\n\n"+
                  "Exception type: SQLException\n" +
                "--------------------------\n\n"+
                ex.toString() +
                "\n\n\n" +
                "--------------------------\n"+
                "bu bir robot maildir."
        
        );

    }
    public Exception(ParseException ex,String error_class){
        sendEmail("Portunes programı bir hata yürüttü.Lütfen kullanıcı ile iletişime geçin\n"+
                  "hata bilgileri aşağıdadır.\n\n" +
                "--------------------------\n"+
                  ex.getClass() + "  " + error_class + "\n" +
                "--------------------------\n\n"+
                  "Exception type: ParseException\n" +
                "--------------------------\n\n"+
                ex.toString() +
                "\n\n\n" +
                "--------------------------\n"+
                "bu bir robot maildir."
        
        );

        
    }
    public Exception(MessagingException ex,String error_class){
        sendEmail("Portunes programı bir hata yürüttü.Lütfen kullanıcı ile iletişime geçin\n"+
                  "hata bilgileri aşağıdadır.\n\n" +
                "--------------------------\n"+
                  ex.getClass() + "  " + error_class + "\n" +
                "--------------------------\n\n"+
                  "Exception type: MessagingException\n" +
                "--------------------------\n\n"+
                ex.toString() +
                "\n\n\n" +
                "--------------------------\n"+
                "bu bir robot maildir."
        
        );

        
    }
    
    public Exception(IOException ex,String error_class){
        sendEmail("Portunes programı bir hata yürüttü.Lütfen kullanıcı ile iletişime geçin\n"+
                  "hata bilgileri aşağıdadır.\n\n" +
                "--------------------------\n"+
                ex.getClass() + "  " + error_class + "\n" +
                "--------------------------\n\n"+
                  "Exception type: IOException\n" +
                "--------------------------\n\n"+
                ex.toString() +
                "\n\n\n" +
                "--------------------------\n"+
                "bu bir robot maildir."
        
        );

        
    }
    
    private boolean sendEmail(String to_message){
        
        Config config = new Config();
        
        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
          new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from_mail, from_password);
            }
          });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("bilal@ufakisler.net"));
            message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(to_mail));
            message.setSubject(to_subject + " - portunes_door");
            message.setText(to_message);

            Transport.send(message);

            System.out.println("Hata e-postası sistem yöneticisine gönderildi!");

        } catch (MessagingException e) {
            //throw new RuntimeException(e);
            System.out.println(e);
        }
        
        return true;
    }
}
