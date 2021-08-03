package spring.batch.weather.config;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import spring.batch.weather.model.WeatherItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.lang.String.valueOf;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class JobConfiguration {

    private final StepBuilderFactory stepBuilderFactory;
    private final JobBuilderFactory jobBuilderFactory;

    private final DataSource dataSource;


    @Bean
    public JsonItemReader<WeatherItem> weatherJsonItemReader() {
        return new JsonItemReaderBuilder<WeatherItem>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(WeatherItem.class))
                .resource(new ClassPathResource("weather.json"))
                .name("weatherJsonItemReader")
                .build();
    }

    @Bean
    public ItemWriter<WeatherItem> weatherItemWriter() {
        return items -> {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO WEATHER (location_name, location_state, " +
                            "location_country, lon, lat) " +
                            "VALUES (?,?,?,?,?)");
            items.forEach(item -> {
                try {
                    statement.setString(1, item.getName());
                    statement.setString(2, item.getState());
                    statement.setString(3, item.getCountry());
                    statement.setString(4, valueOf(item.getCoord().getLon()));
                    statement.setString(5, valueOf(item.getCoord().getLat()));
                    statement.execute();
                } catch (SQLException throwable) {
                    throwable.printStackTrace();
                }
            });
            connection.close();
        };
    }

    @Bean
    public Step step(ItemReader<WeatherItem> itemReader,
                     ItemWriter<WeatherItem> itemWriter) {
        return stepBuilderFactory
                .get("step")
                .<WeatherItem, WeatherItem>chunk(1000)
                .reader(itemReader)
                .writer(itemWriter)
                .build();
    }

    @Bean
    public Job job(Step step) {
        return jobBuilderFactory
                .get("job")
                .start(step)
                .build();
    }
}
