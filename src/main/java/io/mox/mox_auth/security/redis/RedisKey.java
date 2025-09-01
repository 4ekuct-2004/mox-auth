package io.mox.mox_auth.security.redis;

public enum RedisKey {
    LOGIN_ATTEMPTS_IP("login:attempts:ip:"),
    LOGIN_ATTEMPTS_USER("login:attempts:user:"),
    LOGIN_BLOCKED_IP("login:blocked:ip:"),
    LOGIN_BLOCKED_USER("login:blocked:user:");

    private final String prefix;

    RedisKey(String prefix) {
        this.prefix = prefix;
    }

    public String of(String value) {
        return prefix + value;
    }
}
