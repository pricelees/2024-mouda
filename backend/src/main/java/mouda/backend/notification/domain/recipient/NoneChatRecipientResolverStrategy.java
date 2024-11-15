package mouda.backend.notification.domain.recipient;

import java.util.List;

import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mouda.backend.darakbangmember.infrastructure.DarakbangMemberRepository;
import mouda.backend.moim.infrastructure.ChamyoRepository;
import mouda.backend.notification.domain.MemberNotification;
import mouda.backend.notification.domain.MoudaNotification;
import mouda.backend.notification.infrastructure.MemberNotificationRepository;

@RequiredArgsConstructor
@Slf4j
public abstract class NoneChatRecipientResolverStrategy implements RecipientResolverStrategy {

	protected final DarakbangMemberRepository darakbangMemberRepository;
	protected final MemberNotificationRepository memberNotificationRepository;
	protected final ChamyoRepository chamyoRepository;

	public void saveNotificationsForMembers(List<Long> recipientsIds, long darakbangId,
		MoudaNotification notification) {
		String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
		memberNotificationRepository.saveAll(recipientsIds.stream()
			.map(memberId -> MemberNotification.builder()
				.memberId(memberId)
				.darakbangId(darakbangId)
				.moudaNotification(notification)
				.build())
			.toList());
		log.info("회원별 알림 저장 완료. 트랜잭션 이름: {},  스레드: {}", transactionName, Thread.currentThread().getName());
	}
}
