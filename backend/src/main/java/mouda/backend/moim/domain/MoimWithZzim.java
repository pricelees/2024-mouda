package mouda.backend.moim.domain;

import lombok.Getter;

@Getter
public class MoimWithZzim {

	private final Moim moim;
	private final boolean isZzimed;

	public MoimWithZzim(Moim moim, boolean isZzimed) {
		this.moim = moim;
		this.isZzimed = isZzimed;
	}
}
