package com.studyhub.api_gateway.secret.jwt.domain.repository;

import com.studyhub.api_gateway.secret.jwt.domain.BlacklistAccessToken;
import org.springframework.data.repository.CrudRepository;

public interface BlacklistAccessTokenRepository extends CrudRepository<BlacklistAccessToken, String> {
}
