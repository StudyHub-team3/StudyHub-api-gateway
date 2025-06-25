package com.studyhub.api_gateway.secret.jwt.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "blacklist")
public class BlacklistAccessToken {
    @Id
    private String jti;
    private Boolean blacked = true;

    @Builder
    public BlacklistAccessToken(String jti) {
        this.jti = jti;
    }
}
