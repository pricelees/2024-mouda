package mouda.backend.moim.implement.finder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mouda.backend.moim.domain.Comment;
import mouda.backend.moim.domain.Moim;
import mouda.backend.moim.domain.ParentComment;
import mouda.backend.moim.infrastructure.CommentRepository;

@Component
@RequiredArgsConstructor
public class CommentFinder {

	private final CommentRepository commentRepository;

	public List<ParentComment> readAllParentComments(Moim moim) {
		List<Comment> comments = commentRepository.findAllByMoimOrderByCreatedAt(moim);
		Map<Long, List<Comment>> childCommentsGroupedByParentId = readAllChildCommentsGroupedByParentId(comments);

		return comments.stream()
			.filter(comment -> comment.getParentId() == null)
			.map(parentComment -> new ParentComment(parentComment, childCommentsGroupedByParentId.get(parentComment.getId())))
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
