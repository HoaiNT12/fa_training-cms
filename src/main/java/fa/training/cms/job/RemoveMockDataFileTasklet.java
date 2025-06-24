package fa.training.cms.job;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.file.Files;
import java.nio.file.Path;
@Component
@StepScope
public class RemoveMockDataFileTasklet implements Tasklet {
    private static final String MOCK_FILE_NAME = "mock_posts.json";
    private final String folderLocation;
    public RemoveMockDataFileTasklet(@Value("${job.postDel.folderPath}") String folderLocation) {
        this.folderLocation = folderLocation;
    }
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        if (folderLocation == null || folderLocation.isBlank()) {
            throw new IllegalArgumentException("Folder location is required! Please configure 'batch.folder.path' in application.yaml.");
        }
        Path filePath = Path.of(folderLocation, MOCK_FILE_NAME);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            System.out.println("Removed mock data file: " + filePath.toString());
        } else {
            System.out.println("Mock data file not found, no deletion needed: " + filePath.toString());
        }
        return RepeatStatus.FINISHED;
    }
}
