package mouda.backend.notification.business;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mouda.backend.notification.domain.CommonNotification;
import mouda.backend.notification.domain.NotificationEvent;
import mouda.backend.notification.domain.Recipient;
import mouda.backend.notification.implement.NotificationSender;
import mouda.backend.notification.implement.NotificationWriter;
import mouda.backend.notification.implement.filter.SubscriptionFilter;
import mouda.backend.notification.implement.filter.SubscriptionFilterRegistry;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

	private final NotificationWriter notificationWriter;
	private final SubscriptionFilterRegistry subscriptionFilterRegistry;
	private final NotificationSender notificationSender;

	@TransactionalEventListener(classes = NotificationEvent.class, phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void sendNotification(NotificationEvent notificationEvent) {
		String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
		log.info("알림 전송 이벤트 수신. 전송 시작. 트랜잭션 이름: {}, 스레드: {}", transactionName, Thread.currentThread().getName());

		List<Recipient> recipients = notificationEvent.getRecipients();
		CommonNotification commonNotification = notificationEvent.toCommonNotification();
		notificationWriter.saveAllMemberNotification(commonNotification, recipients);

		notificationSender.sendNotification(commonNotification, recipients);
	}
}
