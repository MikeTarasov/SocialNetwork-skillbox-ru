package ru.skillbox.socialnetwork.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;
import ru.skillbox.socialnetwork.api.responses.FileUploadResponse;
import ru.skillbox.socialnetwork.model.enums.FileType;

@Service
public class StorageService {

  @Value("${upload.path}")
  private String uploadPath;

  @Value("${upload.subdir.name.length}")
  private int nameSubdirLength;

  @Value("${upload.subdir.depth}")
  private int uploadSubdirDepth;

  @Value("${upload.max.file.size}")
  private int maxFileSizeInMb;

  @Value("#{'${upload.file.types}'.split(',')}")
  private List<String> uploadFileTypes;

  @Value("${upload.length.file.id}")
  private int lengthFileId;

  private final long BYTES_IN_MEGABYTES = 1000000;

  private final AccountService accountService;

  public StorageService(AccountService accountService) {
    this.accountService = accountService;
  }


  public ResponseEntity<?> getUpload(String pathToFile) {

    InputStream is = null;
    OutputStream os = null;
    File dstFile;

    try {
      File srcFile = validateFile(pathToFile);
      dstFile = new File(makeDirectories() + "/" + srcFile.getName());

      is = new FileInputStream(srcFile);
      os = new FileOutputStream(dstFile);

      byte[] buffer = new byte[1024];
      int length;

      while ((length = is.read(buffer)) > 0) {
        os.write(buffer, 0, length);
      }

      return ResponseEntity.status(HttpStatus.OK)
          .body(new ErrorTimeDataResponse(
              "",
              System.currentTimeMillis(),
              makeFileUploadResponse(dstFile)));

    } catch (Exception e) {
      e.printStackTrace();

      return ResponseEntity.status(HttpStatus.OK)
          .body(new ErrorTimeDataResponse(
              e.getMessage(),
              System.currentTimeMillis(),
              null));

    } finally {
      closeStreams(is, os);
    }
  }


  private FileUploadResponse makeFileUploadResponse(File dstFile) {
    return FileUploadResponse.builder()
        .id(generateRandomString(lengthFileId))
        .ownerId(accountService.getCurrentUser().getId())
        .fileName(dstFile.getName())
        .relativeFilePath(dstFile.getPath())
        .rawFileURL(dstFile.toURI().toString())
        .fileFormat(getFileExtension(dstFile))
        .bytes(dstFile.length())
        .fileType(FileType.IMAGE)
        .createdAt(System.currentTimeMillis())
        .build();
  }

  private void closeStreams(InputStream is, OutputStream os) {
    try {
      is.close();
      os.close();
    } catch (IOException | NullPointerException e) {
      e.printStackTrace();
    }
  }

  private File validateFile(String pathToFile) {
    if (pathToFile.isEmpty()) {
      throw new IllegalArgumentException("Path to file is empty");
    }

    File srcFile = new File(pathToFile);

    if (!srcFile.isFile()) {
      throw new IllegalArgumentException("No file found in the specified path");
    }

    String ext = getFileExtension(srcFile);

    if (ext.isBlank() || !uploadFileTypes.contains(ext)) {
      throw new IllegalArgumentException("Unknown file type");
    }

    if (srcFile.length() == 0) {
      throw new IllegalArgumentException("File is empty");
    }
    if (srcFile.length() > maxFileSizeInMb * BYTES_IN_MEGABYTES) {
      throw new IllegalArgumentException("File size exceeds maximum");
    }
    return srcFile;
  }


  private File makeDirectories() {
    File dstPath = new File(uploadPath +
        getRandomPathToFile(nameSubdirLength, uploadSubdirDepth));

    if (dstPath.mkdirs()) {
      System.out.println("File upload directories " + dstPath.getPath() + " was created");
    } else {
      throw new RuntimeException("File upload directories not created");
    }
    return dstPath;
  }

  private String getFileExtension(File file) {
    String fileName = file.getName();
    if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
      return fileName.substring(fileName.lastIndexOf(".") + 1);
    } else {
      return "";
    }
  }

  private String getRandomPathToFile(int lenChar, int countSubDir) {
    String randomString = generateRandomString(lenChar * countSubDir);
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < countSubDir; i++) {
      String randChar = new Random().ints(lenChar, 0, randomString.length())
          .mapToObj(randomString::charAt)
          .map(Object::toString)
          .collect(Collectors.joining());
      sb.append("/").append(randChar);
    }
    return sb.toString();
  }

  private String generateRandomString(int len) {
    String symbols = "abcdefghijklmnopqrstuvwxyz0123456789";
    return new Random().ints(len, 0, symbols.length())
        .mapToObj(symbols::charAt)
        .map(Object::toString)
        .collect(Collectors.joining());
  }

}
