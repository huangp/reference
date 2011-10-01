package integration

import org.junit.Test
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import net.huangp.example.springbatch.context.AppContext
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobParametersBuilder
import org.junit.Assert
import static org.hamcrest.Matchers.equalTo

class BatchIntegrationTest {
    @Test
    public void smoke() throws Exception {

        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppContext.class);

        JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);

        Job job = applicationContext.getBean(Job.class);

        JobExecution execution = jobLauncher.run(job, new JobParametersBuilder().toJobParameters());

        Assert.assertThat(execution.exitStatus.exitCode, equalTo("COMPLETED"));
    }
}