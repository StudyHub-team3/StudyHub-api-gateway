package com.studyhub.api_gateway.secret.jwt.authentication;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class UserPrincipal implements Principal {
    private final String userId;
    private final String userName;

    public boolean hasUserId() {
        return userId != null;
    }

    public boolean hasName() {
        return userName != null;
    }
    public boolean hasMandatory() {
        return (this.hasName() && this.hasUserId());
    }
    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getName() {
        return userId;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        if (another == null) return false;
        if (!getClass().isAssignableFrom(another.getClass())) return false;
        UserPrincipal principal = (UserPrincipal) another;
        if (!Objects.equals(userId, principal.userId)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        return result;
    }
}
