package fa.training.cms.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fa.training.cms.entity.Post;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
@Component
@StepScope
public class PostJsonReader implements ItemReader<Post> {
    private static final String MOCK_FILE_NAME = "mock_posts.json";
    private final String folderLocation;
    private Iterator<Post> postIterator;
    private boolean isInitialized = false;
    public PostJsonReader(@Value("${job.postDel.folderPath}") String folderLocation) {
        this.folderLocation = folderLocation;
    }
    private void initialize() throws Exception {
        if (isInitialized) {
            return;
        }
        if (folderLocation == null || folderLocation.isBlank()) {
            throw new IllegalArgumentException("Folder location is required! Please configure 'job.postDel.folderPath' in application.yaml.");
        }
        Path filePath = Path.of(folderLocation, MOCK_FILE_NAME);
        File inputFile = filePath.toFile();
        if (!inputFile.exists()) {
            throw new IllegalStateException("Mock data file not found at: " + filePath.toString());
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        byte[] fileContent = Files.readAllBytes(filePath);
        List<Post> posts = Arrays.asList(mapper.readValue(fileContent, Post[].class));
        this.postIterator = posts.iterator();
        this.isInitialized = true;
    }
    @Override
    public Post read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        initialize();
        return postIterator.hasNext() ? postIterator.next() : null;
    }
}