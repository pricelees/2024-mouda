package mouda.backend.common.data;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import mouda.backend.darakbang.domain.Darakbang;
import mouda.backend.darakbang.infrastructure.DarakbangRepository;
import mouda.backend.darakbangmember.domain.DarakBangMemberRole;
import mouda.backend.darakbangmember.domain.DarakbangMember;
import mouda.backend.darakbangmember.infrastructure.DarakbangMemberRepository;
import mouda.backend.member.domain.Member;
import mouda.backend.member.infrastructure.MemberRepository;

@Component
@RequiredArgsConstructor
public class InitialDataLoader {

	private final DarakbangRepository darakbangRepository;
	private final DarakbangMemberRepository darakbangMemberRepository;
	private final MemberRepository memberRepository;

	@PostConstruct
	public void loadInitialData() {
		Member member = memberRepository.save(
			Member.builder()
				.kakaoId(1231231234L)
				.nickname("다락방장")
				.build()
		);

		Darakbang darakbang = darakbangRepository.save(
			Darakbang.builder()
				.code("NOTI")
				.name("알림 테스트")
				.build()
		);

		darakbangMemberRepository.save(
			DarakbangMember.builder()
				.nickname("다락방장")
				.role(DarakBangMemberRole.MANAGER)
				.memberId(member.getId())
				.darakbang(darakbang)
				.build()
		);
	}
}
