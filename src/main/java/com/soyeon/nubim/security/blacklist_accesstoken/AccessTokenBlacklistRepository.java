package com.soyeon.nubim.security.blacklist_accesstoken;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessTokenBlacklistRepository extends CrudRepository<AccessTokenBlacklist, String> {
}
