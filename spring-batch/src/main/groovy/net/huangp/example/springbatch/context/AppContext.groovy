package net.huangp.example.springbatch.context

import java.text.SimpleDateFormat
import net.huangp.example.springbatch.component.RecordWriter
import net.huangp.example.springbatch.domain.Record
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.support.SimpleJobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper
import org.springframework.batch.item.file.mapping.DefaultLineMapper
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer
import org.springframework.batch.support.transaction.ResourcelessTransactionManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.propertyeditors.CustomDateEditor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.core.io.Resource
import org.springframework.data.mongodb.core.MongoFactoryBean
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.transaction.PlatformTransactionManager

@Configuration
@ImportResource('classpath:/spring/job.xml')
@ComponentScan("net.huangp.example.springbatch.component")
@PropertySource("classpath:app.properties")
class AppContext {
    //this is from @PropertySource above
    @Autowired Environment env

    //PropertySource included properties can be used this way and take advantage of spring's type resolver power
    @Value('${input}') Resource input

    @Value('${error.output}') Resource error

    @Autowired RecordWriter recordWriter

    @Bean(name = "smsFileReader")
    FlatFileItemReader<Record> reader() {
        String[] inputFields = env.getProperty('input.fields', String[].class)
        int[] includedFields = env.getProperty('input.included', int[].class)
        //default use comma as delimiter
        def tokenizer = new DelimitedLineTokenizer(names: inputFields, includedFields: includedFields)
        def mapper = new BeanWrapperFieldSetMapper<Record>(targetType: Record, customEditors: customEditors())
        def lineMapper = new DefaultLineMapper<Record>(lineTokenizer: tokenizer, fieldSetMapper: mapper)

        new FlatFileItemReader<Record>(resource: input, lineMapper: lineMapper)
    }

    private def customEditors() {
        def customEditors = [:]
        def format = new SimpleDateFormat("yyyy.MM.dd HH:mm")
        customEditors.put(Date, new CustomDateEditor(format, true))
        customEditors
    }

//    @Bean(name = "writer")
//    FlatFileItemWriter<Record> writer() {
//        def fieldExtractor = new BeanWrapperFieldExtractor<Record>(names: outputFields)
//        //default use comma as delimiter
//        def lineAggregator = new DelimitedLineAggregator<Record>(fieldExtractor: fieldExtractor)
//
//        new FlatFileItemWriter<Record>(resource: error, lineAggregator: lineAggregator)
//    }

    @Bean
    MongoTemplate mongoTemplate() {
        new MongoTemplate(mongo().object, "smsdb")
    }

    @Bean
    MongoFactoryBean mongo() {
        MongoFactoryBean mongo = new MongoFactoryBean()
        mongo.setHost("localhost")
        return mongo
    }

    //default use method name as bean name
    @Bean
    JobLauncher jobLauncher() {
        new SimpleJobLauncher(jobRepository: jobRepository())
    }

    @Bean
    JobRepository jobRepository() {
        new MapJobRepositoryFactoryBean(transactionManager()).jobRepository
    }

    @Bean
    PlatformTransactionManager transactionManager() {
        new ResourcelessTransactionManager()
    }
}
