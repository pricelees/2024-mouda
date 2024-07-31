package mouda.backend.comment.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentErrorMessage {

	CONTENT_NOT_FOUND("댓글 내용이 존재하지 않습니다."),
	MEMBER_NOT_FOUND("작성자가 존재하지 않습니다.");

	private final String message;
}
