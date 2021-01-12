package ru.skillbox.socialnetwork.services;

import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.model.entity.Person;

@Service
public class EmailSenderService {

  private final JavaMailSender emailSender;

  @Value("${application.host}")
  private String applicationHost;


  @Autowired
  public EmailSenderService(JavaMailSender emailSender) {
    this.emailSender = emailSender;
  }

  public boolean sendEmailChangePassword(Person person, String newPassword) {
    try {
      MimeMessage message = emailSender.createMimeMessage();

      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      String htmlMsg = createHtmlMessage(person.getFirstName(), newPassword);

      message.setContent(htmlMsg, "text/html; charset=utf-8");

      helper.setTo(person.getEmail());

      helper.setSubject("Восстановление пароля в " + applicationHost);

      emailSender.send(message);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  private String createHtmlMessage(String personName, String newPassword) {
    return "<h3>Здравствуйте, " + personName + "!</h3>" +
            "<p><br>&nbsp;&nbsp;&nbsp;&nbsp;От Вашего имени подана заявка на смену пароля в "
            + applicationHost + ".<br>" +
            "Вам сгенерирован новый пароль - " + newPassword +
            "<br><br>&nbsp;&nbsp;&nbsp;&nbsp;Если вы не инициировали это действие, возможно, " +
            "ваша учетная запись была взломана.<br>" +
            "Пожалуйста, свяжитесь с администрацией " + applicationHost + "<br><br>" +
            "С уважением,<br>" +
            "администрация " + applicationHost + "</p>";
  }
}