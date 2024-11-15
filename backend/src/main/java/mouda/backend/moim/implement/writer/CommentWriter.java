package mouda.backend.moim.implement.writer;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mouda.backend.darakbangmember.domain.DarakbangMember;
import mouda.backend.moim.domain.Comment;
import mouda.backend.moim.domain.Moim;
import mouda.backend.moim.implement.validator.CommentValidator;
import mouda.backend.moim.infrastructure.CommentRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentWriter {

	private final CommentRepository commentRepository;
	private final CommentValidator commentValidator;

	public Comment saveComment(Moim moim, DarakbangMember darakbangMember, Long parentId, String content) {
		String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
		commentValidator.validateParentCommentExists(parentId);

		Comment comment = Comment.builder()
			.content(content)
			.moim(moim)
			.darakbangMember(darakbangMember)
			.parentId(parentId)
			.createdAt(LocalDateTime.now())
			.build();

		log.info("댓글 추가 완료. 트랜잭션 이름: {}, 스레드: {}", transactionName, Thread.currentThread().getName());
		return commentRepository.save(comment);
	}
}
