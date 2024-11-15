package mouda.backend.notification.domain.recipient;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.extern.slf4j.Slf4j;
import mouda.backend.darakbangmember.domain.DarakbangMember;
import mouda.backend.darakbangmember.infrastructure.DarakbangMemberRepository;
import mouda.backend.moim.domain.Moim;
import mouda.backend.moim.infrastructure.ChamyoRepository;
import mouda.backend.notification.domain.MemberNotification;
import mouda.backend.notification.domain.MoudaNotification;
import mouda.backend.notification.domain.NotificationType;
import mouda.backend.notification.domain.NotificationTypeProvider;
import mouda.backend.notification.infrastructure.MemberNotificationRepository;

@Component
@NotificationTypeProvider(NotificationType.NEW_COMMENT)
@Slf4j
public class NewCommentNotificationRecipientResolver extends NoneChatRecipientResolverStrategy {
	public NewCommentNotificationRecipientResolver(
		DarakbangMemberRepository darakbangMemberRepository,
		MemberNotificationRepository memberNotificationRepository,
		ChamyoRepository chamyoRepository) {
		super(darakbangMemberRepository, memberNotificationRepository, chamyoRepository);
	}

	@Override
	public List<Long> resolveRecipients(long darakbangId, MoudaNotification notification, Moim moim,
		DarakbangMember sender) {
		String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
		List<Long> recipients = new ArrayList<>();
		Long moimerId = chamyoRepository.findMoimerIdByMoimId(moim.getId());
		if (moimerId.equals(sender.getMemberId())) {
			return recipients;
		}
		memberNotificationRepository.save(MemberNotification.builder()
			.memberId(moimerId)
			.darakbangId(darakbangId)
			.moudaNotification(notification)
			.build());
		recipients.add(moimerId);
		log.info("회원별 알림 저장 완료. 트랜잭션 이름: {},  스레드: {}", transactionName, Thread.currentThread().getName());
		return recipients;
	}
}
