package com.sociogram.main_service.requests.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sociogram.main_service.event.model.Event;
import com.sociogram.main_service.event.model.RequestStatus;
import com.sociogram.main_service.user.model.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "PARTICIPATION_REQUEST")
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @NotNull
    private LocalDateTime created;
    @ManyToOne
    private Event event;
    @ManyToOne
    @NotNull
    private User requester;
    @NotNull
    @Enumerated(value = EnumType.STRING)
    private RequestStatus status;
}
