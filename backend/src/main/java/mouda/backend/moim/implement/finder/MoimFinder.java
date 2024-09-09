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
import mouda.backend.moim.exception.MoimErrorMessage;
import mouda.backend.moim.exception.MoimException;
import mouda.backend.moim.infrastructure.ChamyoRepository;
import mouda.backend.moim.infrastructure.MoimRepository;

@Component
@RequiredArgsConstructor
public class MoimFinder {

	private final MoimRepository moimRepository;
	private final ChamyoRepository chamyoRepository;

	public Moim read(long moimId, long currentDarakbangId) {
		return moimRepository.findByIdAndDarakbangId(moimId, currentDarakbangId)
			.orElseThrow(() -> new MoimException(HttpStatus.NOT_FOUND, MoimErrorMessage.NOT_FOUND));
	}

	public List<MoimWithZzim> readAll(long darakbangId) {
		return moimRepository.findAllMoimWithZzim(darakbangId);
	}

	public List<MoimWithZzim> readAllMyMoim(DarakbangMember darakbangMember, FilterType filterType) {
		List<MoimWithZzim> myMoims = moimRepository.findAllMyMoimWithZzim(darakbangMember);
		Predicate<MoimWithZzim> moimPredicate = getPredicateByFilterType(filterType);

		return myMoims.stream().filter(moimPredicate).toList();
	}

	public List<MoimWithZzim> readAllZzimedMoim(DarakbangMember darakbangMember) {
		return moimRepository.findAllZzimedMoim(darakbangMember);
	}

	private Predicate<MoimWithZzim> getPredicateByFilterType(FilterType filterType) {
		if (filterType == FilterType.PAST) {
			return moimWithZzim -> moimWithZzim.getMoim().isPastMoim();
		}
		return moimWithZzim -> moimWithZzim.getMoim().isUpcomingMoim();
	}

	public int countCurrentPeople(Moim moim) {
		return chamyoRepository.countByMoim(moim);
	}
}
