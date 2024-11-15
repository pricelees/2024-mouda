package mouda.backend.moim.business;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mouda.backend.darakbangmember.domain.DarakbangMember;
import mouda.backend.moim.domain.Comment;
import mouda.backend.moim.domain.Moim;
import mouda.backend.moim.implement.finder.MoimFinder;
import mouda.backend.moim.implement.sender.CommentNotificationSender;
import mouda.backend.moim.implement.writer.CommentWriter;
import mouda.backend.moim.presentation.request.comment.CommentCreateRequest;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

	private final MoimFinder moimFinder;
	private final CommentWriter commentWriter;
	private final CommentNotificationSender commentNotificationSender;

	public void createComment(
		Long darakbangId, Long moimId, DarakbangMember darakbangMember, CommentCreateRequest request
	) {
		String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
		log.info("댓글 작성 시작. 트랜잭션 이름: {}, 스레드: {}", transactionName, Thread.currentThread().getName());
		long start = System.nanoTime();
		Moim moim = moimFinder.read(moimId, darakbangId);
		Comment comment = commentWriter.saveComment(moim, darakbangMember, request.parentId(), request.content());

		commentNotificationSender.sendCommentNotification(comment, darakbangMember);
		long end = System.nanoTime();
		log.info("댓글 작성 및 알림 전송 요청 완료. 트랜잭션 이름: {}, 실행 시간: {}ms, 스레드: {}", transactionName, ((end - start) / 1_000_000) , Thread.currentThread().getName());
	}
}
