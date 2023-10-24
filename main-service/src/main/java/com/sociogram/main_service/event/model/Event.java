package com.sociogram.main_service.event.model;

import com.sociogram.main_service.category.model.Category;
import com.sociogram.main_service.event.location.model.Location;
import com.sociogram.main_service.user.model.User;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(max = 2000, min = 20)
    @NotBlank
    private String annotation;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Category category;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    @Size(max = 7000, min = 20)
    private String description;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    @NotNull
    private User initiator;
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    @Column(name = "state")
    @Enumerated(value = EnumType.STRING)
    @NotNull
    private State state;
    @NotBlank
    @Size(max = 120, min = 3)
    private String title;

}
