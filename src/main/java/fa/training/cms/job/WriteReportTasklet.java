package fa.training.cms.job;

import fa.training.cms.repository.PostRepository;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
@Component
@StepScope
public class WriteReportTasklet implements Tasklet {
    private static final String REPORT_FILE_NAME = "seeding_report.txt";
    private final String folderLocation;
    private final PostRepository postRepository;
    public WriteReportTasklet(
            @Value("${job.postDel.folderPath}") String folderLocation,
            PostRepository postRepository) {
        this.folderLocation = folderLocation;
        this.postRepository = postRepository;
    }
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        if (folderLocation == null || folderLocation.isBlank()) {
            throw new IllegalArgumentException("Folder location is required! Please configure 'job.postDel.folderPath' in application.yaml.");
        }
        Path reportPath = Path.of(folderLocation, REPORT_FILE_NAME);
        long totalPosts = postRepository.count();
        String reportText = "Total posts in DB: " + totalPosts;
        Files.write(reportPath, Collections.singletonList(reportText));
        System.out.println("Wrote report to: " + reportPath.toString() + " with content: " + reportText);
        return RepeatStatus.FINISHED;
    }
}