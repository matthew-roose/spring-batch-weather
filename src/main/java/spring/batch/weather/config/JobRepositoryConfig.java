package spring.batch.weather.config;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class JobRepositoryConfig extends DefaultBatchConfigurer {
    @Override
    public void setDataSource(@NonNull DataSource dataSource) {
        // use map-based job repository instead of Postgres
    }
}
