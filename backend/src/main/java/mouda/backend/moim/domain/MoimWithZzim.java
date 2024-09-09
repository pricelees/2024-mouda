package mouda.backend.moim.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MoimWithZzim {

	private final Moim moim;
	private final int currentPeople;
	private final boolean isZzimed;

	@Builder
	public MoimWithZzim(Moim moim, int currentPeople, boolean isZzimed) {
		this.moim = moim;
		this.currentPeople = currentPeople;
		this.isZzimed = isZzimed;
	}
}
