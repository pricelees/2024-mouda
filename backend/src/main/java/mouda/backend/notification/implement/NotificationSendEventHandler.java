package mouda.backend.notification.implement;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mouda.backend.notification.domain.NotificationSendEvent;
import mouda.backend.notification.domain.Recipient;
import mouda.backend.notification.implement.filter.SubscriptionFilterRegistry;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationSendEventHandler {

	private final SubscriptionFilterRegistry subscriptionFilterRegistry;
	private final NotificationSender notificationSender;

	@Async
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener(classes = NotificationSendEvent.class, phase = TransactionPhase.AFTER_COMMIT)
	public void handle(NotificationSendEvent event) {
		String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
		log.info("알림 전송 이벤트 수신. 트랜잭션 이름: {}, 스레드: {}", transactionName, Thread.currentThread().getName());
		notificationSender.sendNotification(event.getNotification(), event.getRecipients());
	}

	private List<Recipient> filterRecipientsBySubscription(NotificationSendEvent event) {
		return subscriptionFilterRegistry.getFilter(event.getNotification().getType()).filter(event);
	}
}
