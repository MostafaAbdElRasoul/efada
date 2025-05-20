package com.efada.entity;

import java.time.LocalDateTime;
import jakarta.persistence.ForeignKey;
import java.util.List;

import com.efada.base.BaseEntity;
import com.efada.enums.SessionStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "sessions")
public class Session extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    private SessionStatus status = SessionStatus.PENDING;

    private LocalDateTime startTime;
    
    private LocalDateTime endTime;

    private String resourceUrl;

    // Relations
    
    @ManyToOne
    @JoinColumn(name = "speaker_id", foreignKey = @ForeignKey(name = "fk_speaker", foreignKeyDefinition = "FOREIGN KEY (speaker_id) REFERENCES users(id) ON DELETE SET NULL"))
    private AppUser speaker;

    @ManyToOne
    @JoinColumn(name = "conference_id", foreignKey = @ForeignKey(name = "fk_conference", foreignKeyDefinition = "FOREIGN KEY (conference_id) REFERENCES conferences(id) ON DELETE SET NULL"))
    private Conference conference;

//    @OneToMany(mappedBy = "session")
//    private List<Registration> registrations;

}
