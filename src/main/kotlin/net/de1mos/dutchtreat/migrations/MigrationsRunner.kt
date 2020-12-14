package net.de1mos.dutchtreat.migrations

import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.driver.MongoSync4Driver
import com.github.cloudyrock.standalone.MongockStandalone
import com.mongodb.client.MongoClient
import net.de1mos.dutchtreat.config.DatabaseProperties

class MigrationsRunner(private val mongoClient: MongoClient, private val databaseProperties: DatabaseProperties) {

    fun runMigrations() {
        val runner = MongockStandalone.builder()
            .setDriver(MongoSync4Driver.withDefaultLock(mongoClient, databaseProperties.dbName))
            .addChangeLogsScanPackage(MigrationsRunner::class.java.packageName)
            .buildRunner()
        runner.execute()
    }
}