package mouda.backend.notification.implement;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mouda.backend.notification.domain.CommonNotification;
import mouda.backend.notification.domain.NotificationType;
import mouda.backend.notification.domain.Recipient;
import mouda.backend.notification.infrastructure.entity.MemberNotificationEntity;
import mouda.backend.notification.infrastructure.repository.MemberNotificationRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationWriter {

	private final MemberNotificationRepository memberNotificationRepository;

	public void saveAllMemberNotification(CommonNotification notification, List<Recipient> recipients) {
		String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
		if (notification.getType() == NotificationType.NEW_CHAT) {
			return;
		}
		List<MemberNotificationEntity> memberNotifications = recipients.stream()
			.map(recipient -> createEntity(notification, recipient))
			.toList();

		memberNotificationRepository.saveAll(memberNotifications);
		log.info("회원별 알림 저장 완료. 트랜잭션 이름: {}, 스레드: {}", transactionName, Thread.currentThread().getName());
	}

	private MemberNotificationEntity createEntity(CommonNotification notification, Recipient recipient) {
		return MemberNotificationEntity.builder()
			.darakbangMemberId(recipient.getDarakbangMemberId())
			.type(notification.getType().name())
			.title(notification.getTitle())
			.body(notification.getBody())
			.targeturl(notification.getRedirectUrl())
			.createdAt(notification.getCreatedAt())
			.build();
	}
}
