package utils;

import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import controllers.Register;

public class EmailSender {
	public static final String FROM = "jianrenxing00@163.com";
	public static final String PSW = "Passw0rd";
	public static final String HOST = "smtp.163.com";
	public static final String PORT = "25";
	
	private static Session getSession(){
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", HOST);
		properties.put("mail.smtp.port", PORT);
		
		Session session = Session.getInstance(properties, 
    			new Authenticator() {
    		
    				protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(FROM, PSW);
					}
		});
		return  session;
	}
	
	public static void send(String toEmail,String content,String subject){
		Session session = getSession();
		Message message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(FROM));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
			message.setSubject(subject);
			message.setContent(content, "text/html; charset=utf-8");
			Transport.send(message);
			
			System.out.println("ok");
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 生成随机验证码
	 * @return
	 */
	private static String generateValiCode(){
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<4;i++){
			int nextInt = random.nextInt(10);
			sb.append(nextInt);
		}
		return sb.toString();
	}

	public static void main(String[] args) {
	}
}
