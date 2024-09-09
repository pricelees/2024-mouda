package mouda.backend.moim.implement.writer;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mouda.backend.darakbangmember.domain.DarakbangMember;
import mouda.backend.moim.domain.Comment;
import mouda.backend.moim.domain.Moim;
import mouda.backend.moim.implement.validator.CommentValidator;
import mouda.backend.moim.infrastructure.CommentRepository;

@Component
@RequiredArgsConstructor
public class CommentWriter {


	private final CommentRepository commentRepository;
	private final CommentValidator commentValidator;

	public void saveComment(Moim moim, DarakbangMember darakbangMember, Long parentId, String content) {
		commentValidator.validateParentCommentExists(parentId);

		Comment comment = Comment.builder()
			.content(content)
			.moim(moim)
			.darakbangMember(darakbangMember)
			.parentId(parentId)
			.build();

		commentRepository.save(comment);
	}
}
