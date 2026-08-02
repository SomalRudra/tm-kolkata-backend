package in.tmkolkata.auth;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminUserRepository extends JpaRepository<AdminUserEntity, Long> {

  Optional<AdminUserEntity> findByUsername(String username);

  Optional<AdminUserEntity> findByEmail(String email);
}
