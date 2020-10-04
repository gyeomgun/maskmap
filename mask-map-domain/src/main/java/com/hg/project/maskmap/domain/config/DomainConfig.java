package com.hg.project.maskmap.domain.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
@ImportAutoConfiguration(FeignAutoConfiguration.class)
@EnableFeignClients(basePackages = "com.hg.project.maskmap.domain.client")
@ComponentScan(basePackages = "com.hg.project.maskmap.domain.service")
public class DomainConfig extends AbstractMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return "mask_map";
    }

    @Override
    public MongoClient mongoClient() {
        String uri = "mongodb://27.96.134.172:38017";
        MongoClientURI mongoUri = new MongoClientURI(uri);
        return new MongoClient(mongoUri);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {

        //remove _class
        MappingMongoConverter converter =
                new MappingMongoConverter(mongoDbFactory(), new MongoMappingContext());
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        converter.afterPropertiesSet();

        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory(), converter);

        return mongoTemplate;

    }

    @Bean
    public Decoder decoder() {
        return new Decoder.Default();
    }
    @Bean
    public Encoder encoder() {
        return new Encoder.Default();
    }
    @Bean
    public Retryer retryer() { return Retryer.NEVER_RETRY; }
}
