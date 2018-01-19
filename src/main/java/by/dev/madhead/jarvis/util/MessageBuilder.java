package by.dev.madhead.jarvis.util;

import by.dev.madhead.jarvis.Messages;
import by.dev.madhead.jarvis.model.Email;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class MessageBuilder {

    private final Email email;
    private Set<Address> addresses;

    public MessageBuilder(Email email, Set<Address> addresses) {
        this.email = email;
        RecipientParser.addAddresses(addresses, email.getBuild()
                .getChangeSet().stream()
                .map(change -> change.getAuthor().getEmail())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));
        this.addresses = addresses;
    }

    public boolean isMsgHasRecipients() {
        return !addresses.isEmpty();
    }

    public Message buildMessage(Session session) throws IOException, MessagingException {
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(Messages.jarvis_fromName()));
        msg.setRecipients(Message.RecipientType.TO, addresses.toArray(new Address[addresses.size()]));
        msg.setSubject(email.getSubject());
        msg.setContent(ContentMaker.buildContent(email));
        return msg;
    }

}