package in.tmkolkata.config;

import com.zaxxer.hikari.HikariDataSource;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

  @Bean
  @ConditionalOnExpression("'${DATABASE_URL:}' != ''")
  public DataSource railwayDataSource() {
    URI databaseUri = URI.create(System.getenv("DATABASE_URL"));
    String[] userInfo = databaseUri.getUserInfo().split(":", 2);

    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setJdbcUrl("jdbc:postgresql://" + databaseUri.getHost() + ":" + databaseUri.getPort()
        + databaseUri.getPath());
    dataSource.setUsername(decode(userInfo[0]));
    dataSource.setPassword(userInfo.length > 1 ? decode(userInfo[1]) : "");
    return dataSource;
  }

  private String decode(String value) {
    return URLDecoder.decode(value, StandardCharsets.UTF_8);
  }
}
