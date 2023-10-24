package com.sociogram.main_service.comments.model;

import com.sociogram.main_service.event.model.Event;
import com.sociogram.main_service.user.model.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDateTime createdOn;

    @NotBlank
    private String text;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;
}
