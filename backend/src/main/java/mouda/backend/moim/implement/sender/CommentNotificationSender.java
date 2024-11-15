package mouda.backend.moim.implement.sender;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.extern.slf4j.Slf4j;
import mouda.backend.common.config.UrlConfig;
import mouda.backend.darakbangmember.domain.DarakbangMember;
import mouda.backend.moim.domain.Comment;
import mouda.backend.moim.domain.CommentRecipient;
import mouda.backend.moim.domain.Moim;
import mouda.backend.moim.implement.finder.CommentRecipientFinder;
import mouda.backend.notification.domain.NotificationEvent;
import mouda.backend.notification.domain.NotificationType;
import mouda.backend.notification.domain.Recipient;

@Component
@Slf4j
public class CommentNotificationSender extends AbstractMoimNotificationSender {

	private final CommentRecipientFinder commentRecipientFinder;
	private final ApplicationEventPublisher eventPublisher;

	public CommentNotificationSender(UrlConfig urlConfig, CommentRecipientFinder commentRecipientFinder,
		ApplicationEventPublisher eventPublisher) {
		super(urlConfig);
		this.commentRecipientFinder = commentRecipientFinder;
		this.eventPublisher = eventPublisher;
	}

	@Async
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public void sendCommentNotification(Comment comment, DarakbangMember author) {
		List<CommentRecipient> commentRecipients = commentRecipientFinder.getAllRecipient(comment);

		commentRecipients.forEach(commentRecipient -> {
			sendNotification(commentRecipient, comment, author);
		});
	}

	private void sendNotification(CommentRecipient commentRecipient, Comment comment, DarakbangMember author) {
		String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
		NotificationType notificationType = commentRecipient.getNotificationType();
		String message = notificationType.createMessage(author.getNickname());
		List<Recipient> recipients = commentRecipient.getRecipients();
		Moim moim = comment.getMoim();
		NotificationEvent notificationEvent = new NotificationEvent(notificationType, moim.getTitle(), message,
			getMoimUrl(moim.getDarakbangId(), moim.getId()), recipients);

		eventPublisher.publishEvent(notificationEvent);
		log.info("알림 전송 이벤트 발행 완료. 트랜잭션 이름: {}, 스레드: {}", transactionName, Thread.currentThread().getName());
	}
}
