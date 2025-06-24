package fa.training.cms.job;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
/**
 * Tasklet tự động tạo folder cho từng job sử dụng folderPath truyền qua job parameters.
 */
@Component
@StepScope
public class JobFolderCreatorTasklet implements Tasklet {
    private final String folderPath;
    // Inject folderPath từ job parameter at runtime
    public JobFolderCreatorTasklet(
            @Value("${job.postDel.folderPath}") String folderPath
    ) {
        this.folderPath = folderPath;
    }
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws IOException {
        if (folderPath == null || folderPath.isBlank()) {
            throw new IllegalArgumentException("Job parameter 'folderPath' is required!");
        }
        Path path = Path.of(folderPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        return RepeatStatus.FINISHED;
    }
}