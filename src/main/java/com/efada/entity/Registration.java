package com.efada.entity;

import java.time.Instant;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

@Entity
@Table(name = "registrations")
public class Registration {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "attendee_id", foreignKey = @ForeignKey(name = "fk_attendee", foreignKeyDefinition = "FOREIGN KEY (attendee_id) REFERENCES users(id) ON DELETE SET NULL"))
    private AppUser attendee;

    @ManyToOne
    @JoinColumn(name = "session_id", foreignKey = @ForeignKey(name = "fk_session", foreignKeyDefinition = "FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE SET NULL"))
    private Session session;

    private Instant registeredAt;
}
