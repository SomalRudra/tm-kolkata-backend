package in.tmkolkata.auth;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminTokenRepository extends JpaRepository<AdminTokenEntity, Long> {

  Optional<AdminTokenEntity> findByTokenHashAndTokenType(String tokenHash, String tokenType);
}
