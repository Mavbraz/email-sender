package sender;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class EmailSender {

	public static void main(String[] args) {
		EmailSender emailSender = new EmailSender();
		emailSender.init(args);
	}

	protected void init(String[] args) {
		try {
			Options options = new Options();
			Option optionHost = Option.builder().required().hasArg().longOpt("host").type(String.class).build();
			Option optionPort = Option.builder().required().hasArg().longOpt("port").type(Number.class).build();
			Option optionSSL = Option.builder().longOpt("ssl").build();
			Option optionUser = Option.builder().required().hasArg().longOpt("user").type(String.class).build();
			Option optionPassword = Option.builder().required().hasArg().longOpt("password").type(String.class).build();
			Option optionTo = Option.builder().required().hasArgs().longOpt("to").type(String.class).build();
			Option optionCc = Option.builder().hasArgs().longOpt("cc").type(String.class).build();
			Option optionBcc = Option.builder().hasArgs().longOpt("bcc").type(String.class).build();
			Option optionSubject = Option.builder().required().hasArg().longOpt("subject").type(String.class).build();
			Option optionMessage = Option.builder().required().hasArg().longOpt("message").type(String.class).build();
			Option optionAttachment = Option.builder().hasArgs().longOpt("attachment").type(String.class).build();

			options.addOption(optionHost);
			options.addOption(optionPort);
			options.addOption(optionSSL);
			options.addOption(optionUser);
			options.addOption(optionPassword);
			options.addOption(optionTo);
			options.addOption(optionCc);
			options.addOption(optionBcc);
			options.addOption(optionSubject);
			options.addOption(optionMessage);
			options.addOption(optionAttachment);

			CommandLineParser parser = new DefaultParser();
			CommandLine commandLine = parser.parse(options, args);

			Email email = new Email();

			if (commandLine.hasOption(optionHost.getLongOpt())) {
				email.setHost(commandLine.getOptionValue("host"));
			}

			if (commandLine.hasOption(optionPort.getLongOpt())) {
				email.setPort(commandLine.getOptionValue("port"));
			}

			if (commandLine.hasOption(optionSSL.getLongOpt())) {
				email.setSsl(true);
			}

			if (commandLine.hasOption(optionUser.getLongOpt())) {
				email.setUser(commandLine.getOptionValue("user"));
			}

			if (commandLine.hasOption(optionPassword.getLongOpt())) {
				email.setPassword(commandLine.getOptionValue("password"));
			}

			if (commandLine.hasOption(optionTo.getLongOpt())) {
				email.setTo(commandLine.getOptionValues("to"));
			}

			if (commandLine.hasOption(optionCc.getLongOpt())) {
				email.setCc(commandLine.getOptionValues("cc"));
			}

			if (commandLine.hasOption(optionBcc.getLongOpt())) {
				email.setBcc(commandLine.getOptionValues("bcc"));
			}

			if (commandLine.hasOption(optionSubject.getLongOpt())) {
				email.setSubject(commandLine.getOptionValue("subject"));
			}

			if (commandLine.hasOption(optionMessage.getLongOpt())) {
				email.setMessage(commandLine.getOptionValue("message"));
			}

			if (commandLine.hasOption(optionAttachment.getLongOpt())) {
				email.setAttachments(commandLine.getOptionValues("attachment"));
			}

			this.sendEmail(email);
			System.out.println("The email has been sent");
		} catch (ParseException e) {
			System.out.print("Parse error: ");
			System.out.println(e.getMessage());
		} catch (MessagingException e) {
			System.out.print("Messaging error: ");
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.print("Attachment error: ");
			System.out.println(e.getMessage());
		}
	}

	protected void sendEmail(Email email) throws MessagingException, IOException {
		// Based on
		// "https://blog.jedox.com/batch-excel-part-2-send-excel-file-attachment-using-javamail/"
		Properties properties = System.getProperties();

		if (email.isSsl()) {
			properties.put("mail.smtp.starttls.enable", true);
			// It's needed in script to avoid error "Could not convert socket to TLS"
			properties.setProperty("mail.smtp.ssl.trust", email.getHost());
		}

		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.host", email.getHost());
		properties.put("mail.smtp.user", email.getUser());
		properties.put("mail.smtp.password", email.getPassword());
		properties.put("mail.smtp.port", email.getPort());

		Session session = Session.getDefaultInstance(properties);

		// Create a new e-mail message
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(email.getUser()));

		for (String to : email.getTo()) {
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		}

		if (email.getCc() != null && email.getCc().length > 0) {
			for (String cc : email.getCc()) {
				message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
			}
		}

		if (email.getBcc() != null && email.getBcc().length > 0) {
			for (String bcc : email.getBcc()) {
				message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
			}
		}

		message.setSubject(email.getSubject());

		// Create body part for the message
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(email.getMessage(), "text/html");

		// Create the multipart
		Multipart multipart = new MimeMultipart();
		message.setContent(multipart);
		multipart.addBodyPart(messageBodyPart);

		// Add attachment
		if (email.getAttachments().length > 0) {
			MimeBodyPart attachmentBodyPart = new MimeBodyPart();

			for (String attachment : email.getAttachments()) {
				attachmentBodyPart.attachFile(new File(attachment));
			}

			multipart.addBodyPart(attachmentBodyPart);
		}

		Transport transport = session.getTransport("smtp");
		transport.connect(email.getHost(), email.getUser(), email.getPassword());
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}

}
