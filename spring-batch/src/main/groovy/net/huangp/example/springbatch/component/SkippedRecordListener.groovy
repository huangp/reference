////////////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2011, Patrick Huang. All rights reserved.
//
// Blah blah blah.
//
////////////////////////////////////////////////////////////////////////////////
package net.huangp.example.springbatch.component

import net.huangp.example.springbatch.domain.Record
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.core.listener.SkipListenerSupport
import org.springframework.batch.item.file.FlatFileParseException
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component

//first generic argument is the item type when skipped in process, second is skipped in write
@Component
class SkippedRecordListener extends SkipListenerSupport<Object, Record> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkippedRecordListener)

    @Value('${error}')
    Resource errorFile

    def ln = System.getProperty('line.separator')

    void onSkipInRead(Throwable t) {
        if (t in FlatFileParseException) {
            def parseException = t as FlatFileParseException
            LOGGER.error("error reading line {}: {}",parseException.lineNumber, parseException.input)
            errorFile.file << "$parseException.input$ln"
        } else {
            LOGGER.error("error reading", t)
        }
    }
}
