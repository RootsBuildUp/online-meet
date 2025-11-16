package com.ticonsys.online_meet.model.user;

import com.square.emp.util.DateTimeUtil;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Accessors(chain = true)
@NoArgsConstructor
@Data
@Entity(name = "Password_reset_token")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private LocalDateTime expiryDate;

    public PasswordResetToken(String token, User user, Integer passwordResetTokenMinutes) {
        this.token = token;
        this.user = user;
        this.expiryDate = calculateExpiryDate(passwordResetTokenMinutes);
    }

    public void setExpiryDate(Integer passwordResetTokenMinutes) {
        this.expiryDate = calculateExpiryDate(passwordResetTokenMinutes);
    }

    private LocalDateTime calculateExpiryDate(Integer passwordResetTokenMinutes) {
        return DateTimeUtil.getCurrentLocalDateTime().plusMinutes(passwordResetTokenMinutes);
    }

    public boolean isExpired() {
        return DateTimeUtil.getCurrentLocalDateTime().isAfter(expiryDate);
    }
}
