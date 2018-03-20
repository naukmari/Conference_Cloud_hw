package com.google.devrel.training.conference.servlet;

import com.google.appengine.api.utils.SystemProperty;
import com.google.devrel.training.conference.domain.Conference;

import static com.google.devrel.training.conference.service.OfyService.ofy;

import com.google.common.base.Joiner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * A servlet for sending a notification e-mail.
 */
public class SendAnnouncementEmail extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(SendAnnouncementEmail.class.getName());

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Iterable<Conference> iterable = ofy().load().type(Conference.class).filter("seatsAvailable <", 5)
				.filter("seatsAvailable >", 0);
		List<String> conferenceNames = new ArrayList<>(0);
		for (Conference conference : iterable) {
			conferenceNames.add(conference.getName());
		}
		if (conferenceNames.size() > 0) {
			// Build a String that announces the nearly sold-out conferences
			StringBuilder announcementStringBuilder = new StringBuilder(
					"Last chance to attend! The following conferences are nearly sold out: ");
			Joiner joiner = Joiner.on(", ").skipNulls();
			announcementStringBuilder.append(joiner.join(conferenceNames));
		String email = "naukmari@gmail.com";
        // String conferenceInfo = request.getParameter("conferenceInfo");
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        String body = announcementStringBuilder.toString();
        try {
            Message message = new MimeMessage(session);
            InternetAddress from = new InternetAddress(
                    String.format("noreply@%s.appspotmail.com",
                            SystemProperty.applicationId.get()), "Conference Central");
            message.setFrom(from);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, ""));
            message.setSubject("Last chance!!!");
            message.setText(body);
            Transport.send(message);
        } catch (MessagingException e) {
            LOG.log(Level.WARNING, String.format("Failed to send an mail to %s", email), e);
            throw new RuntimeException(e);
        }
		
		response.setStatus(204);
	}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setStatus(404);
		//

		// // TODO
		// // Query for conferences with less than 5 seats left
		// Iterable<Conference> iterable =
		// ofy().load().type(Conference.class).filter("seatsAvailable <", 5)
		// .filter("seatsAvailable >", 0);
		//
		// // TODO
		// // Iterate over the conferences with less than 5 seats less
		// // and get the name of each one
		// List<String> conferenceNames = new ArrayList<>(0);
		// for (Conference conference : iterable) {
		// conferenceNames.add(conference.getName());
		// }
		// if (conferenceNames.size() > 0) {
		//
		// // Build a String that announces the nearly sold-out conferences
		// StringBuilder announcementStringBuilder = new StringBuilder(
		// "Last chance to attend! The following conferences are nearly sold out: ");
		// Joiner joiner = Joiner.on(", ").skipNulls();
		// announcementStringBuilder.append(joiner.join(conferenceNames));
		//
		// Properties props = new Properties();
		// Session session = Session.getDefaultInstance(props, null);
		// String body = announcementStringBuilder + "\n";
		// try {
		// Message message = new MimeMessage(session);
		// InternetAddress from = new InternetAddress(
		// String.format("noreply@%s.appspotmail.com",
		// SystemProperty.applicationId.get()),
		// "Conference Central");
		// message.setFrom(from);
		// message.addRecipient(Message.RecipientType.TO, new InternetAddress(email,
		// ""));
		// message.setSubject("Hello! Don't forget to take part!");
		// message.setText(body);
		// Transport.send(message);
		// } catch (MessagingException e) {
		// LOG.log(Level.WARNING, String.format("Failed to send an mail to %s", email),
		// e);
		// throw new RuntimeException(e);
		// }
		// }
	}
}