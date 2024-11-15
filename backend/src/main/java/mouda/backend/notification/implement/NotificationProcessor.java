package mouda.backend.notification.implement;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mouda.backend.notification.domain.NotificationPayload;
import mouda.backend.notification.domain.NotificationSendEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationProcessor {

	private final NotificationWriter notificationWriter;
	private final ApplicationEventPublisher notificationSendEventPublisher;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void process(NotificationPayload payload) {
		notificationWriter.saveMemberNotification(payload);

		NotificationSendEvent event = NotificationSendEvent.from(payload);
		notificationSendEventPublisher.publishEvent(event);
		log.info("알림 전송 이벤트 발행 완료. 트랜잭션 이름: {}, 스레드: {}", TransactionSynchronizationManager.getCurrentTransactionName(),
			Thread.currentThread().getName());
	}
}
