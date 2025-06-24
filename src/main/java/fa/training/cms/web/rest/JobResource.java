package fa.training.cms.web.rest;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/job")
public class JobResource {
    private final JobLauncher jobLauncher;
    private final Job postSeedingJob;

    public JobResource(JobLauncher jobLauncher,
                       @Qualifier("postSeedingJob") Job postSeedingJob) {
        this.jobLauncher = jobLauncher;
        this.postSeedingJob = postSeedingJob;
    }
    @GetMapping("/launch")
    public String launch() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("startTime", Instant.now().toString())
                    .toJobParameters();
            jobLauncher.run(postSeedingJob, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Launched";
    }
}
