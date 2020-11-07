package net.de1mos.dutchtreat.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import java.util.*

@Configuration
@EnableMongoRepositories(basePackages = ["net.de1mos.dutchtreat"])
class MongoConfig(@Value("\${mongoUrl}") val mongoUrl: String) : AbstractMongoClientConfiguration() {
    override fun getDatabaseName(): String {
        return "dutch"
    }

    override fun configureClientSettings(builder: MongoClientSettings.Builder) {
        builder.applyConnectionString(ConnectionString(String(Base64.getDecoder().decode(mongoUrl))))
    }

}