package mouda.backend.moim.domain;

import java.util.List;

import lombok.Getter;

@Getter
public class ParentComment {

	private final Comment comment;
	private final List<Comment> children;

	public ParentComment(Comment comment, List<Comment> children) {
		this.comment = comment;
		this.children = initialize(children);
	}

	private List<Comment> initialize(List<Comment> children) {
		if (children.isEmpty()) {
			return List.of();
		}
		return children;
	}
}
