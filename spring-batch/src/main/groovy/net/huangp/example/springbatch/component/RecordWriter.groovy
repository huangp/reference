package net.huangp.example.springbatch.component

import net.huangp.example.springbatch.domain.Record
import org.springframework.batch.item.ItemWriter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component

@Component
class RecordWriter implements ItemWriter<Record> {

    @Autowired
    MongoTemplate mongoTemplate

    void write(List<? extends Record> items) {
        items.each {
            mongoTemplate.insert(it)
        }
    }
}
