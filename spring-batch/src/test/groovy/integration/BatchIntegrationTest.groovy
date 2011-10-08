package integration

import net.huangp.example.springbatch.context.AppContext
import net.huangp.example.springbatch.domain.Record
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasSize
import org.junit.Before

class BatchIntegrationTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchIntegrationTest)
    static def context
    static MongoOperations mongoOps

    @BeforeClass
    static void setupContext() {
        context = new AnnotationConfigApplicationContext(AppContext.class)
        mongoOps = context.getBean(MongoOperations)
    }

    @Before
    public void setup() {
        mongoOps.dropCollection(Record)
    }


    @Test
    public void smoke() throws Exception {

        JobLauncher jobLauncher = context.getBean(JobLauncher);

        Job job = context.getBean(Job);

        JobExecution execution = jobLauncher.run(job, new JobParametersBuilder().toJobParameters());

        Assert.assertThat(execution.exitStatus.exitCode, equalTo("COMPLETED"));

        def records = mongoOps.find(new Query(), Record)
        records.each {
            LOGGER.info("{}", it)
        }
        Assert.assertThat(records, hasSize(5));

    }
}
