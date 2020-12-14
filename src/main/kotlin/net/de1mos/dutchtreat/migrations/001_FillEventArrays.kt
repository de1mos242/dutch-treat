package net.de1mos.dutchtreat.migrations

import com.github.cloudyrock.mongock.ChangeLog
import com.github.cloudyrock.mongock.ChangeSet
import com.mongodb.client.MongoDatabase
import net.de1mos.dutchtreat.repositories.Event
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection
import org.litote.kmongo.setValue

@ChangeLog(order = "001")
class `001_FillEventArrays` {

    @ChangeSet(order = "001", id = "FillEventArrays", author = "d.yakovlev")
    fun up(mongoDatabase: MongoDatabase) {
        val col = mongoDatabase.getCollection<Event>("events")
        col.updateMany(Event::participants.eq(null), setValue(Event::participants, emptyList()))
        col.updateMany(Event::purchases.eq(null), setValue(Event::purchases, emptyList()))
        col.updateMany(Event::transfers.eq(null), setValue(Event::transfers, emptyList()))
    }
}