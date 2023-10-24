package com.sociogram.main_service.requests.repository;

import com.sociogram.main_service.event.dto.ConfirmedEventDto;
import com.sociogram.main_service.event.model.RequestStatus;
import com.sociogram.main_service.requests.model.ParticipationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long>, JpaSpecificationExecutor<ParticipationRequest> {
    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    @Query(value = "SELECT new com.sociogram.main_service.event.dto.ConfirmedEventDto(r.event.id, count(r.event.id)) " +
            "FROM ParticipationRequest r " +
            "WHERE r.event.id IN :eventIds " +
            "AND r.status = :status " +
            "GROUP BY r.event.id")
    List<ConfirmedEventDto> countConfirmedRequests(List<Long> eventIds, RequestStatus status);

    Long countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    List<ParticipationRequest> findAllByIdIn(List<Long> requestIds);

    List<ParticipationRequest> findAllByEventIdAndStatus(Long eventId, RequestStatus status);

    Optional<ParticipationRequest> findByEventIdAndRequesterId(Long eventId, Long requesterId);

    List<ParticipationRequest> findAllByEventIdAndStatusAndRequesterId(Long eventId, RequestStatus status, Long requesterId);
}
