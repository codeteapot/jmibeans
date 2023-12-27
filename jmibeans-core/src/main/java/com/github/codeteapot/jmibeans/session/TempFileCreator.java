package com.github.codeteapot.jmibeans.session;

import static java.io.File.createTempFile;

import java.io.File;
import java.io.IOException;

class TempFileCreator {

  TempFileCreator() {}

  File create(String prefix) throws IOException {
    File file = createTempFile(prefix, "");
    file.deleteOnExit();
    return file;
  }
}
