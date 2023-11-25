package ru.practicum.main.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.enums.RequestStatus;
import ru.practicum.main.request.model.ParticipationRequest;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByEvent_Initiator_IdAndEvent_Id(Long userId, Long eventId);

    Integer countByEventIdAndStatus(Long eventId, RequestStatus requestStatus);

    List<ParticipationRequest> findAllByRequester_Id(Long userId);

    List<ParticipationRequest> findAllByEventIdAndRequesterId(Long eventId, Long userId);
}
