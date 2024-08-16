package mouda.backend.darakbang.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mouda.backend.darakbang.domain.Darakbang;
import mouda.backend.darakbang.dto.request.DarakbangCreateRequest;
import mouda.backend.darakbang.dto.request.DarakbangEnterRequest;
import mouda.backend.darakbang.dto.response.CodeValidationResponse;
import mouda.backend.darakbang.dto.response.DarakbangResponse;
import mouda.backend.darakbang.dto.response.DarakbangResponses;
import mouda.backend.darakbang.dto.response.InvitationCodeResponse;
import mouda.backend.darakbang.exception.DarakbangErrorMessage;
import mouda.backend.darakbang.exception.DarakbangException;
import mouda.backend.darakbang.repository.DarakbangRepository;
import mouda.backend.darakbangmember.domain.DarakBangMemberRole;
import mouda.backend.darakbangmember.domain.DarakbangMember;
import mouda.backend.darakbangmember.exception.DarakbangMemberErrorMessage;
import mouda.backend.darakbangmember.exception.DarakbangMemberException;
import mouda.backend.darakbangmember.repository.repository.DarakbangMemberRepository;
import mouda.backend.member.domain.Member;

@Service
@Transactional
@RequiredArgsConstructor
public class DarakbangService {

	private final DarakbangRepository darakbangRepository;
	private final DarakbangMemberRepository darakbangMemberRepository;
	private final InvitationCodeGenerator invitationCodeGenerator;

	public Darakbang createDarakbang(DarakbangCreateRequest darakbangCreateRequest, Member member) {
		if (darakbangRepository.existsByName(darakbangCreateRequest.name())) {
			throw new DarakbangException(HttpStatus.BAD_REQUEST, DarakbangErrorMessage.NAME_ALREADY_EXIST);
		}

		String invitationCode = generateInvitationCode();
		Darakbang entity = darakbangCreateRequest.toEntity(invitationCode);
		Darakbang darakbang = darakbangRepository.save(entity);

		DarakbangMember darakbangMember = DarakbangMember.builder()
			.darakbang(darakbang)
			.member(member)
			.nickname(darakbangCreateRequest.nickname())
			.role(DarakBangMemberRole.MANAGER)
			.build();
		darakbangMemberRepository.save(darakbangMember);

		return darakbang;
	}

	private String generateInvitationCode() {
		String invitationCode = invitationCodeGenerator.generate();
		if (darakbangRepository.existsByCode(invitationCode)) {
			throw new DarakbangException(HttpStatus.INTERNAL_SERVER_ERROR, DarakbangErrorMessage.CODE_ALREADY_EXIST);
		}
		return invitationCode;
	}

	@Transactional(readOnly = true)
	public DarakbangResponses findAllMyDarakbangs(Member member) {
		List<DarakbangMember> darakbangMembers = darakbangMemberRepository.findAllByMemberId(member.getId());
		List<DarakbangResponse> responses = darakbangMembers.stream()
			.map(DarakbangResponse::toResponse)
			.toList();

		return DarakbangResponses.toResponse(responses);
	}

	@Transactional(readOnly = true)
	public InvitationCodeResponse findInvitationCode(Long darakbangId, Member member) {
		DarakBangMemberRole role = darakbangMemberRepository.findByDarakbangIdAndMemberId(darakbangId, member.getId())
			.orElseThrow(() -> new DarakbangMemberException(HttpStatus.NOT_FOUND,
				DarakbangMemberErrorMessage.DARAKBANG_MEMBER_NOT_EXIST))
			.getRole();

		if (role != DarakBangMemberRole.MANAGER) {
			throw new DarakbangMemberException(HttpStatus.FORBIDDEN, DarakbangMemberErrorMessage.NOT_ALLOWED_TO_READ);
		}

		Darakbang darakbang = darakbangRepository.findById(darakbangId)
			.orElseThrow(() -> new DarakbangException(HttpStatus.NOT_FOUND, DarakbangErrorMessage.DARAKBANG_NOT_FOUND));

		return InvitationCodeResponse.toResponse(darakbang);
	}

	@Transactional(readOnly = true)
	public CodeValidationResponse validateCode(String code) {
		if (darakbangRepository.existsByCode(code)) {
			return CodeValidationResponse.toResponse(true);
		}
		return CodeValidationResponse.toResponse(false);
	}

	public Darakbang enter(String code, DarakbangEnterRequest request, Member member) {
		Darakbang darakbang = darakbangRepository.findByCode(code)
			.orElseThrow(() -> new DarakbangException(HttpStatus.NOT_FOUND, DarakbangErrorMessage.DARAKBANG_NOT_FOUND));

		if (darakbangMemberRepository.existsByDarakbangIdAndNickname(darakbang.getId(), request.nickname())) {
			throw new DarakbangMemberException(HttpStatus.BAD_REQUEST,
				DarakbangMemberErrorMessage.NICKNAME_ALREADY_EXIST);
		}
		if (darakbangMemberRepository.existsByDarakbangIdAndMemberId(darakbang.getId(), member.getId())) {
			throw new DarakbangMemberException(HttpStatus.BAD_REQUEST,
				DarakbangMemberErrorMessage.MEMBER_ALREADY_EXIST);
		}

		DarakbangMember entity = request.toEntity(darakbang, member);
		darakbangMemberRepository.save(entity);

		return darakbang;
	}
}
