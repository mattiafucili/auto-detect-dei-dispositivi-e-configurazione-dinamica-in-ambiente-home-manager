/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.unibo.homemanager.util;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
//Per Allegato
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;



/**
 *
 * @author s0000590338
 */


public class MailUtility
{
  public static void sendMail (String host,String user,String password,String []toAddr, String mitt, String oggetto, String testoEmail) throws MessagingException
  {
      //int port = 465; //porta 25 per non usare SSL
      int port=25;
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.user", mitt);
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.port", port);
 
    // commentare la riga seguente per non usare SSL 
    props.put("mail.smtp.starttls.enable","true");
    props.put("mail.smtp.socketFactory.port", port);
 
    // commentare la riga seguente per non usare SSL 
    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    props.put("mail.smtp.socketFactory.fallback", "false");
 
    Session session = Session.getInstance(props, null);
    session.setDebug(true);
 
    // Creazione delle BodyParts del messaggio
    MimeBodyPart messageBodyPart1 = new MimeBodyPart();
    //MimeBodyPart messageBodyPart2 = new MimeBodyPart();
 
    try{
      // COSTRUZIONE DEL MESSAGGIO
      Multipart multipart = new MimeMultipart();
      MimeMessage msg = new MimeMessage(session);
 
      // header del messaggio
      msg.setSubject(oggetto);
      msg.setSentDate(new Date());
      msg.setFrom(new InternetAddress(mitt));
 
      // destinatario
      for(int i = 0; i < toAddr.length; i++)
       {
           msg.addRecipient(Message.RecipientType.TO,
                new InternetAddress(toAddr[i]));
         // msg.addRecipient(Message.RecipientType.TO,new InternetAddress(toAddr[i]));
       }
      
      // corpo del messaggio
      messageBodyPart1.setText("Shopper Agent: Shopping List\n"+testoEmail);
      multipart.addBodyPart(messageBodyPart1);
 
      // allegato al messaggio
      //DataSource source = new FileDataSource("");
      //messageBodyPart2.setDataHandler(new DataHandler(source));
      //messageBodyPart2.setFileName("");
      //multipart.addBodyPart(messageBodyPart2);
 
      // inserimento delle parti nel messaggio
      msg.setContent(multipart);
 
      Transport transport = session.getTransport("smtps"); //("smtp") per non usare SSL
      transport.connect(host, user, password);
      transport.sendMessage(msg, msg.getAllRecipients());
      transport.close();
 
      System.out.println("Invio dell'email Terminato");
 
    }catch(AddressException ae) {
      ae.printStackTrace();
    }catch(NoSuchProviderException nspe){
      nspe.printStackTrace();
    }catch(MessagingException me){
      me.printStackTrace();
    }
  }
}
