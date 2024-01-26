package com.github.codeteapot.jmibeans.session;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.codeteapot.jmibeans.session.TempFileCreator;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TempFileCreatorTest {

  private static final String SOME_PREFIX = "some-prefix";

  private TempFileCreator tempFileCreator;

  @BeforeEach
  public void setUp() {
    tempFileCreator = new TempFileCreator();
  }

  @Test
  public void createTempFile() throws Exception {
    File tempFile = tempFileCreator.create(SOME_PREFIX);

    assertThat(tempFile.getName()).startsWith(SOME_PREFIX);
    
    tempFile.delete();
  }
}
