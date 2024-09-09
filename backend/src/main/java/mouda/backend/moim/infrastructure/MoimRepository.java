package mouda.backend.moim.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mouda.backend.darakbangmember.domain.DarakbangMember;
import mouda.backend.moim.domain.Moim;
import mouda.backend.moim.domain.MoimStatus;
import mouda.backend.moim.domain.MoimWithZzim;

public interface MoimRepository extends JpaRepository<Moim, Long> {

	@Query("""
			UPDATE Moim m
			SET m.moimStatus = :status
			WHERE m.id = :id
		""")
	@Modifying
	int updateMoimStatusById(@Param("id") Long moimId, @Param("status") MoimStatus status);

	@Query("""
			SELECT m From Moim m
			WHERE m.darakbangId = :darakbangId AND m.moimStatus = 'MOIMING'
			ORDER BY m.id DESC
		""")
	List<Moim> findAllByDarakbangIdOrderByIdDesc(@Param("darakbangId") Long darakbangId);

	Optional<Moim> findByIdAndDarakbangId(Long moimId, Long darakbangId);

	boolean existsByIdAndDarakbangId(Long moimId, Long darakbangId);

	@Query("""
			SELECT new mouda.backend.moim.domain.MoimWithZzim(m, (SELECT CASE WHEN COUNT(z) > 0 THEN true ELSE false END FROM Zzim z WHERE z.moim.id = m.id))
			FROM Moim m
			WHERE m.darakbangId = :darakbangId
			ORDER BY m.id DESC
		""")
	List<MoimWithZzim> findAllMoimWithZzim(@Param("darakbangId") Long darakbangId);


	@Query("""
			SELECT new mouda.backend.moim.domain.MoimWithZzim(c.moim, (SELECT CASE WHEN COUNT(z) > 0 THEN true ELSE false END FROM Zzim z WHERE z.moim.id = c.moim.id))
			FROM Chamyo c
			WHERE c.darakbangMember = :darakbangMember
			ORDER BY c.moim.id DESC
		""")
	List<MoimWithZzim> findAllMyMoimWithZzim(DarakbangMember darakbangMember);

	@Query("""
			SELECT new mouda.backend.moim.domain.MoimWithZzim(m, (SELECT CASE WHEN COUNT(z) > 0 THEN true ELSE false END FROM Zzim z WHERE z.moim.id = m.id AND z.darakbangMember = :darakbangMember))
			FROM Moim m
			ORDER BY m.id DESC
		""")
	List<MoimWithZzim> findAllZzimedMoim(@Param("darakbangMember") DarakbangMember darakbangMember);
}
