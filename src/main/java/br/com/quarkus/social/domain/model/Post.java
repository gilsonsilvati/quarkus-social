package br.com.quarkus.social.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Post extends EntityBase implements Serializable {

    private static final long serialVersionUID = -5746126904394809887L;

    @Column(name = "post_text", nullable = false)
    private String text;

    @Column(name = "date_time", nullable = false)
    private ZonedDateTime date;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    private void prePersist() {
        date = ZonedDateTime.now();
    }
}
