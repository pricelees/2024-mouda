package mouda.backend.moim.implement.finder;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mouda.backend.darakbangmember.domain.DarakbangMember;
import mouda.backend.moim.domain.Chamyo;
import mouda.backend.moim.domain.FilterType;
import mouda.backend.moim.domain.Moim;
import mouda.backend.moim.domain.MoimWithZzim;
import mouda.backend.moim.domain.Zzim;
import mouda.backend.moim.exception.MoimErrorMessage;
import mouda.backend.moim.exception.MoimException;
import mouda.backend.moim.infrastructure.ChamyoRepository;
import mouda.backend.moim.infrastructure.MoimRepository;
import mouda.backend.moim.infrastructure.ZzimRepository;

@Component
@RequiredArgsConstructor
public class MoimFinder {

	private final MoimRepository moimRepository;
	private final ChamyoRepository chamyoRepository;
	private final ZzimFinder zzimFinder;
	private final ZzimRepository zzimRepository;

	public Moim read(long moimId, long currentDarakbangId) {
		return moimRepository.findByIdAndDarakbangId(moimId, currentDarakbangId)
			.orElseThrow(() -> new MoimException(HttpStatus.NOT_FOUND, MoimErrorMessage.NOT_FOUND));
	}

	public List<MoimWithZzim> readAll(long darakbangId, DarakbangMember darakbangMember) {
		List<Moim> moims = moimRepository.findAllByDarakbangIdOrderByIdDesc(darakbangId);
		return parseToMoimWithZzim(moims, darakbangMember);
	}

	public List<MoimWithZzim> readAllMyMoim(DarakbangMember darakbangMember, FilterType filterType) {
		List<Moim> moims = chamyoRepository.findAllByDarakbangMemberIdOrderByIdDesc(darakbangMember.getId())
			.stream().map(Chamyo::getMoim).toList();
		List<MoimWithZzim> myMoims = parseToMoimWithZzim(moims, darakbangMember);
		Predicate<MoimWithZzim> moimPredicate = getPredicateByFilterType(filterType);

		return myMoims.stream().filter(moimPredicate).toList();
	}

	public List<MoimWithZzim> readAllZzimedMoim(DarakbangMember darakbangMember) {
		List<Moim> moims = zzimRepository.findAllByDarakbangMemberIdOrderByIdDesc(darakbangMember.getId())
			.stream().map(Zzim::getMoim).toList();

		return parseToMoimWithZzim(moims, darakbangMember);
	}

	private Predicate<MoimWithZzim> getPredicateByFilterType(FilterType filterType) {
		if (filterType == FilterType.PAST) {
			return moimWithZzim -> moimWithZzim.getMoim().isPastMoim();
		}
		if (filterType == FilterType.UPCOMING) {
			return moimWithZzim -> moimWithZzim.getMoim().isUpcomingMoim();
		}
		return moimWithZzim -> true;
	}

	private List<MoimWithZzim> parseToMoimWithZzim(List<Moim> moims, DarakbangMember darakbangMember) {
		return moims.stream()
			.map(moim -> MoimWithZzim.builder()
				.moim(moim)
				.currentPeople(countCurrentPeople(moim))
				.isZzimed(zzimFinder.isMoimZzimedByMember(moim.getId(), darakbangMember))
				.build()
			)
			.toList();
	}

	public int countCurrentPeople(Moim moim) {
		return chamyoRepository.countByMoim(moim);
	}
}
