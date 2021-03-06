package com.mycompany.mystaff.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mycompany.mystaff.domain.User;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findOneByActivationKey(String activationKey);

  List<User> findAllByActivatedIsFalseAndCreatedDateBefore(Instant dateTime);

  Optional<User> findOneByResetKey(String resetKey);

  Optional<User> findOneByEmail(String email);

  Optional<User> findOneByLogin(String login);

  @EntityGraph(attributePaths = "authorities")
  User findOneWithAuthoritiesByIdAndCompanyId(Long id, Long companyId);

  @EntityGraph(attributePaths = "authorities")
  @Cacheable(cacheNames = "users")
  Optional<User> findOneWithAuthoritiesByLogin(String login);

  Page<User> findAllByLoginNotAndCompanyId(Pageable pageable, String login, Long companyId);

}
