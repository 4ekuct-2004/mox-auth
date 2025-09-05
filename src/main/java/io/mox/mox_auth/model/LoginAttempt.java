package io.mox.mox_auth.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
public class LoginAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String ipAddress;

    @ManyToOne
    private User account;

    @CreatedDate
    private LocalDateTime loginTimestamp;

    @Column(nullable = false)
    private boolean successful;

    public LoginAttempt() {}
    public LoginAttempt(String ipAdress, User account, boolean successful, LocalDateTime loginTimestamp) {
        this.ipAddress = ipAdress;
        this.account = account;
        this.successful = successful;
        this.loginTimestamp = loginTimestamp;
    }

    public long getId() { return id; }
    public String getIpAddress() { return ipAddress; }
    public User getAccount() { return account; }
    public LocalDateTime getLoginTimestamp() { return loginTimestamp; }
    public boolean isSuccessful() { return successful; }

    public void setId(long id) { this.id = id; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public void setAccount(User account) { this.account = account; }
    public void setLoginTimestamp(LocalDateTime loginTimestamp) { this.loginTimestamp = loginTimestamp; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
}
