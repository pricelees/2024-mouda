package mouda.backend.moim.implement.finder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mouda.backend.moim.domain.Comment;
import mouda.backend.moim.domain.Moim;
import mouda.backend.moim.domain.ParentComment;
import mouda.backend.moim.infrastructure.CommentRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentFinder {

	private final CommentRepository commentRepository;

	public List<ParentComment> readAllParentComments(Moim moim) {
		String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
		List<Comment> comments = commentRepository.findAllByMoimOrderByCreatedAt(moim);
		Map<Long, List<Comment>> childCommentsGroupedByParentId = readAllChildCommentsGroupedByParentId(comments);

		log.info("CommentFinder.readAllParentComments transactionName: {}, Thread: {}", transactionName, Thread.currentThread().getName());
		return comments.stream()
			.filter(comment -> comment.getParentId() == null)
			.map(parentComment -> new ParentComment(parentComment, childCommentsGroupedByParentId.getOrDefault(parentComment.getId(), List.of())))
			.collect(Collectors.toList());
	}

	private Map<Long, List<Comment>> readAllChildCommentsGroupedByParentId(List<Comment> comments) {
		return comments.stream()
			.filter(comment -> comment.getParentId() != null)
			.collect(Collectors.groupingBy(Comment::getParentId));
	}

	public Long readMemberIdByParentId(Long parentId) {
		return commentRepository.findMemberIdByParentId(parentId);
	}
}
