package io.mox.mox_auth.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class BannedNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @CreationTimestamp
    private LocalDateTime timestamp;

    @Column
    private String type;

    @Column
    private String target;

    public BannedNote() {}

    public BannedNote(String type, String target) {
        this.type = type;
        this.target = target;
    }

    public String getTarget() { return target; }
    public long getId() { return id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getType() { return type; }
    public void setTarget(String usernameOrIp) { this.target = usernameOrIp; }
    public void setId(long id) { this.id = id; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setType(String type) { this.type = type; }
}
