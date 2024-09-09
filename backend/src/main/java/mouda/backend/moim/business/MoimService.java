package mouda.backend.moim.business;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mouda.backend.darakbangmember.domain.DarakbangMember;
import mouda.backend.moim.domain.FilterType;
import mouda.backend.moim.domain.Moim;
import mouda.backend.moim.domain.MoimWithZzim;
import mouda.backend.moim.domain.ParentComment;
import mouda.backend.moim.implement.finder.CommentFinder;
import mouda.backend.moim.implement.finder.MoimFinder;
import mouda.backend.moim.implement.writer.MoimWriter;
import mouda.backend.moim.presentation.request.moim.MoimCreateRequest;
import mouda.backend.moim.presentation.request.moim.MoimEditRequest;
import mouda.backend.moim.presentation.response.comment.CommentResponses;
import mouda.backend.moim.presentation.response.moim.MoimDetailsFindResponse;
import mouda.backend.moim.presentation.response.moim.MoimFindAllResponses;
import mouda.backend.notification.business.NotificationService;
import mouda.backend.notification.domain.NotificationType;

@Transactional
@Service
@RequiredArgsConstructor
public class MoimService {

	private final MoimWriter moimWriter;
	private final MoimFinder moimFinder;
	private final CommentFinder commentFinder;
	private final NotificationService notificationService;

	@Transactional(readOnly = true)
	public MoimDetailsFindResponse findMoimDetails(long darakbangId, long moimId) {
		Moim moim = moimFinder.read(moimId, darakbangId);

		List<ParentComment> parentComments = commentFinder.readAllParentComments(moim);
		CommentResponses commentResponses = CommentResponses.toResponse(parentComments);

		return MoimDetailsFindResponse.toResponse(moim, moimFinder.countCurrentPeople(moim), commentResponses);
	}

	@Transactional(readOnly = true)
	public MoimFindAllResponses findAllMoim(Long darakbangId, DarakbangMember darakbangMember) {
		List<MoimWithZzim> moimWithZzims = moimFinder.readAll(darakbangId, darakbangMember);

		return MoimFindAllResponses.toResponse(moimWithZzims);
	}

	@Transactional(readOnly = true)
	public MoimFindAllResponses findAllMyMoim(DarakbangMember darakbangMember, FilterType filter) {
		System.out.println("filter = " + filter);
		List<MoimWithZzim> moimWithZzims = moimFinder.readAllMyMoim(darakbangMember, filter);

		return MoimFindAllResponses.toResponse(moimWithZzims);
	}

	@Transactional(readOnly = true)
	public MoimFindAllResponses findZzimedMoim(DarakbangMember darakbangMember) {
		List<MoimWithZzim> moimWithZzims = moimFinder.readAllZzimedMoim(darakbangMember);

		return MoimFindAllResponses.toResponse(moimWithZzims);
	}

	public Long createMoim(Long darakbangId, DarakbangMember darakbangMember, MoimCreateRequest moimCreateRequest) {
		Moim moim = moimWriter.save(moimCreateRequest.toEntity(darakbangId), darakbangMember);

		notificationService.notifyToMembers(NotificationType.MOIM_CREATED, darakbangId, moim, darakbangMember);

		return moim.getId();
	}

	public void completeMoim(Long darakbangId, Long moimId, DarakbangMember darakbangMember) {
		Moim moim = moimFinder.read(moimId, darakbangId);
		moimWriter.completeMoim(moim, darakbangMember);

		notificationService.notifyToMembers(NotificationType.MOIMING_COMPLETED, darakbangId, moim, darakbangMember);
	}

	public void cancelMoim(Long darakbangId, Long moimId, DarakbangMember darakbangMember) {
		Moim moim = moimFinder.read(moimId, darakbangId);
		moimWriter.cancelMoim(moim, darakbangMember);

		notificationService.notifyToMembers(NotificationType.MOIM_CANCELLED, darakbangId, moim, darakbangMember);
	}

	public void reopenMoim(Long darakbangId, Long moimId, DarakbangMember darakbangMember) {
		Moim moim = moimFinder.read(moimId, darakbangId);
		moimWriter.reopenMoim(moim, darakbangMember);

		notificationService.notifyToMembers(NotificationType.MOINING_REOPENED, darakbangId, moim, darakbangMember);
	}

	public void editMoim(Long darakbangId, MoimEditRequest request, DarakbangMember darakbangMember) {
		Moim moim = moimFinder.read(request.moimId(), darakbangId);
		moimWriter.updateMoim(moim, darakbangMember, request.title(), request.date(), request.time(), request.place(),
			request.maxPeople(), request.description());

		notificationService.notifyToMembers(NotificationType.MOIM_MODIFIED, darakbangId, moim, darakbangMember);
	}
}
