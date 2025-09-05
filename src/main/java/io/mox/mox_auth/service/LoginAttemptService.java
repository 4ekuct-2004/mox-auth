package io.mox.mox_auth.service;

import io.mox.mox_auth.model.BannedNote;
import io.mox.mox_auth.model.LoginAttempt;
import io.mox.mox_auth.repository.BanRepo;
import io.mox.mox_auth.repository.LoginRepo;
import io.mox.mox_auth.security.redis.RedisKey;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class LoginAttemptService {

    private final StringRedisTemplate redis;
    private final BanRepo banRepo;
    private final LoginRepo loginRepo;

    public LoginAttemptService(StringRedisTemplate stringRedisTemplate, BanRepo banRepo, LoginRepo loginRepo) {
        this.redis = stringRedisTemplate;
        this.banRepo = banRepo;
        this.loginRepo = loginRepo;
    }

    public boolean isBlocked(String username, String ip){
        boolean ipBlocked = redis.hasKey(RedisKey.LOGIN_BLOCKED_IP.of(ip));
        boolean userBlocked = redis.hasKey(RedisKey.LOGIN_BLOCKED_USER.of(username));
        return ipBlocked || userBlocked;
    }

    public void recordFailedAttempt(LoginAttempt attempt){
        String ip = RedisKey.LOGIN_ATTEMPTS_IP.of(attempt.getIpAddress());
        String user = RedisKey.LOGIN_ATTEMPTS_USER.of(attempt.getAccount().getUsername());

        loginRepo.save(attempt);

        Long ipAttempts = redis.opsForValue().increment(ip);
        redis.expire(ip, Duration.ofMinutes(30));

        if(ipAttempts != null && ipAttempts >= 3){
            redis.opsForValue().set(RedisKey.LOGIN_BLOCKED_IP.of(attempt.getIpAddress()), "BLOCKED", Duration.ofMinutes(30));
            banRepo.save(new BannedNote("IP", attempt.getAccount().getUsername()));
        }

        redis.opsForSet().add(user, attempt.getIpAddress());
        redis.expire(user, Duration.ofMinutes(30));

        Long uniqueIps = redis.opsForSet().size(user);
        if(uniqueIps != null && uniqueIps >= 3){
            redis.opsForValue().set(RedisKey.LOGIN_BLOCKED_USER.of(attempt.getAccount().getUsername()),
                    "BLOCKED", Duration.ofMinutes(30) );
        }
    }

    public void recordSuccessfulAttempt(LoginAttempt attempt){
        reset(attempt.getIpAddress(), attempt.getAccount().getUsername());
    }

    public void reset(String ip, String username){
        redis.delete(RedisKey.LOGIN_ATTEMPTS_IP.of(ip));
        redis.delete(RedisKey.LOGIN_ATTEMPTS_USER.of(username));
    }


}
