package net.huangp.example.springbatch.context

import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.support.SimpleJobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.FlatFileItemWriter

import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper
import org.springframework.batch.item.file.mapping.DefaultLineMapper
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor
import org.springframework.batch.item.file.transform.DelimitedLineAggregator
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer
import org.springframework.batch.support.transaction.ResourcelessTransactionManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.transaction.PlatformTransactionManager

import net.huangp.example.springbatch.domain.Record
import java.text.SimpleDateFormat
import org.springframework.beans.propertyeditors.CustomDateEditor
import java.text.DateFormat
import java.beans.PropertyEditor
import net.huangp.example.springbatch.domain.SendOrReceive
import java.beans.PropertyEditorSupport

@Configuration
@ImportResource(['classpath:/spring/property-context.xml', 'classpath:/spring/job.xml'])
class AppContext {
    @Value('#{properties["output"]}')
    String output

    @Value('#{properties["input.fields"]}')
    String[] inputFields

    @Value('#{properties["input.included"]}')
    int[] inputIncludedFields

    @Value('#{properties["output.fields"]}')
    String[] outputFields

    @Value("#{properties[input]}")
    Resource input

    @Bean(name = "outputResource")
    Resource outputResource() {
        new FileSystemResource(output)
    }

    @Bean(name = "reader")
    FlatFileItemReader<Record> reader() {
        //default use comma as delimiter
        def tokenizer = new DelimitedLineTokenizer(names: inputFields, includedFields: inputIncludedFields)
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

    @Bean(name = "writer")
    FlatFileItemWriter<Record> writer() {
        def fieldExtractor = new BeanWrapperFieldExtractor<Record>(names: outputFields)
        //default use comma as delimiter
        def lineAggregator = new DelimitedLineAggregator<Record>(fieldExtractor: fieldExtractor)
        
        new FlatFileItemWriter<Record>(resource: outputResource(), lineAggregator: lineAggregator)
    }

    @Bean(name = "jobLauncher")
    JobLauncher jobLauncher() {
        new SimpleJobLauncher(jobRepository: jobRepository())
    }

    @Bean(name = "jobRepository")
    JobRepository jobRepository() {
        new MapJobRepositoryFactoryBean(transactionManager()).jobRepository
    }

    @Bean(name = "transactionManager")
    PlatformTransactionManager transactionManager() {
        new ResourcelessTransactionManager()
    }
}
