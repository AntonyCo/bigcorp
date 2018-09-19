package com.training.spring.bigcorp.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "authorities")
public class Authority {
    @EmbeddedId
    private AuthorityId authorityId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Authority authority = (Authority) o;
        return Objects.equals(authorityId, authority.authorityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorityId);
    }

    public AuthorityId getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(AuthorityId authorityId) {
        this.authorityId = authorityId;
    }
}
