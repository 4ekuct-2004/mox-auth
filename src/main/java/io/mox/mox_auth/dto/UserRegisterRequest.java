package io.mox.mox_auth.dto;

public record UserRegisterRequest(
        String username,
        String password
) {
}
