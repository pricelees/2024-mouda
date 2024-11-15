package mouda.backend.notification.implement.fcm;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.google.firebase.messaging.MulticastMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mouda.backend.notification.domain.CommonNotification;
import mouda.backend.notification.domain.FcmToken;
import mouda.backend.notification.domain.Recipient;
import mouda.backend.notification.implement.NotificationSender;
import mouda.backend.notification.implement.fcm.token.FcmTokenFinder;

@Component
@RequiredArgsConstructor
@Slf4j
public class FcmNotificationSender implements NotificationSender {

	private final FcmMessageFactory fcmMessageFactory;
	private final FcmTokenFinder fcmTokenFinder;
	private final AsyncFcmNotificationSender asyncFcmNotificationSender;

	@Override
	public void sendNotification(CommonNotification notification, List<Recipient> recipients) {
		List<FcmToken> tokens = fcmTokenFinder.findAllTokensByMemberIn(recipients);
		List<String> tokenStrings = tokens.stream()
			.map(FcmToken::getToken)
			.toList();

		List<MulticastMessage> messages = fcmMessageFactory.createMessage(notification, tokenStrings);
		asyncFcmNotificationSender.sendAllMulticastMessage(notification, messages, tokens);
	}
}
