package mouda.backend.moim.business;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mouda.backend.darakbangmember.domain.DarakbangMember;
import mouda.backend.moim.domain.Moim;
import mouda.backend.moim.domain.MoimRole;
import mouda.backend.moim.implement.finder.ChamyoFinder;
import mouda.backend.moim.implement.finder.CommentFinder;
import mouda.backend.moim.implement.finder.MoimFinder;
import mouda.backend.moim.implement.writer.CommentWriter;
import mouda.backend.moim.presentation.request.comment.CommentCreateRequest;
import mouda.backend.notification.business.NotificationService;
import mouda.backend.notification.domain.NotificationType;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

	private final MoimFinder moimFinder;
	private final ChamyoFinder chamyoFinder;
	private final CommentFinder commentFinder;
	private final CommentWriter commentWriter;
	private final NotificationService notificationService;

	public void createComment(
		Long darakbangId, Long moimId, DarakbangMember darakbangMember, CommentCreateRequest request
	) {
		String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
		long start = System.currentTimeMillis();
		Moim moim = moimFinder.read(moimId, darakbangId);
		commentWriter.saveComment(moim, darakbangMember, request.parentId(), request.content());

		sendCommentNotification(moim, darakbangMember, request.parentId(), darakbangId);
		long end = System.currentTimeMillis();
		log.info("댓글 작성 및 알림 전송 완료. 트랜잭션 이름: {}, 실행 시간: {}ms, 스레드: {}", transactionName, end - start, Thread.currentThread().getName());
	}

	private void sendCommentNotification(Moim moim, DarakbangMember author, Long parentId, Long darakbangId) {
		if (parentId != null) {
			Long parentCommentAuthorId = commentFinder.readMemberIdByParentId(parentId);
			if (parentCommentAuthorId.equals(author.getId())) {
				return;
			}
			notificationService.notifyToMember(NotificationType.NEW_REPLY, darakbangId, moim, author,
				parentCommentAuthorId);
		}

		if (chamyoFinder.readMoimRole(moim, author) == MoimRole.MOIMEE) {
			return;
		}
		notificationService.notifyToMembers(NotificationType.NEW_COMMENT, darakbangId, moim, author);
	}
}
