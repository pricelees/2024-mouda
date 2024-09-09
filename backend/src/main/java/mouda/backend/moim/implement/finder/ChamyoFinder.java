package mouda.backend.moim.implement.finder;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mouda.backend.darakbangmember.domain.DarakbangMember;
import mouda.backend.moim.domain.Chamyo;
import mouda.backend.moim.domain.Moim;
import mouda.backend.moim.domain.MoimRole;
import mouda.backend.moim.exception.ChamyoErrorMessage;
import mouda.backend.moim.exception.ChamyoException;
import mouda.backend.moim.implement.validator.ChamyoValidator;
import mouda.backend.moim.implement.validator.MoimValidator;
import mouda.backend.moim.infrastructure.ChamyoRepository;

@Component
@RequiredArgsConstructor
public class ChamyoFinder {

	private final ChamyoRepository chamyoRepository;

	public Chamyo read(Moim moim, DarakbangMember darakbangMember) {
		return find(moim.getId(), darakbangMember)
			.orElseThrow(() -> new ChamyoException(HttpStatus.NOT_FOUND, ChamyoErrorMessage.NOT_FOUND));
	}

	private Optional<Chamyo> find(long moimId, DarakbangMember darakbangMember) {
		return chamyoRepository.findByMoimIdAndDarakbangMemberId(moimId, darakbangMember.getId());
	}

	public MoimRole readMoimRole(Moim moim, DarakbangMember darakbangMember) {
		Optional<Chamyo> chamyoOptional = find(moim.getId(), darakbangMember);
		if (chamyoOptional.isEmpty()) {
			return MoimRole.NON_MOIMEE;
		}

		Chamyo chamyo = chamyoOptional.get();
		return chamyo.getMoimRole();
	}

	public List<Chamyo> readAll(long moimId, long darakbangId) {
		return chamyoRepository.findAllByMoimIdAndDarakbangMember_DarakbangId(moimId, darakbangId);
	}

	public boolean isMoimer(long moimId, DarakbangMember darakbangMember) {
		return chamyoRepository.isMoimer(moimId, darakbangMember.getId());
	}
}
