package ru.skillbox.socialnetwork.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.File;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;
import ru.skillbox.socialnetwork.api.responses.FileUploadResponse;
import ru.skillbox.socialnetwork.model.enums.FileType;

@Service
public class StorageService {

  @Value("${cloudinary.cloud.name}")
  private String cloudName;

  @Value("${cloudinary.api.key}")
  private String cloudApiKey;

  @Value("${cloudinary.api.secret}")
  private String cloudApiSecret;

  @Value("${upload.max.file.size}")
  private int maxFileSizeInMb;

  @Value("#{'${upload.file.types}'.split(',')}")
  private List<String> uploadFileTypes;

  private final long BYTES_IN_MEGABYTES = 1000000;

  private final AccountService accountService;

  public StorageService(AccountService accountService) {
    this.accountService = accountService;
  }


  public ResponseEntity<?> getUpload2(String pathToFile) {

    try {
      File srcFile = validateFile(pathToFile);
      Cloudinary cloudinary = new Cloudinary(makeConfig());
      Map res = cloudinary.uploader().upload(srcFile, ObjectUtils.emptyMap());

      return ResponseEntity.status(HttpStatus.OK)
          .body(makeFileUploadResponse(res));

    } catch (Exception e) {
      e.printStackTrace();

      return ResponseEntity.status(HttpStatus.OK)
          .body(new ErrorTimeDataResponse(
              e.getMessage(),
              System.currentTimeMillis(),
              null));
    }
  }

  private FileUploadResponse makeFileUploadResponse(Map<?, ?> res) {
    return FileUploadResponse.builder()
        .id((String) res.get("public_id"))
        .ownerId(accountService.getCurrentUser().getId())
        .fileName((String) res.get("original_filename"))
        .relativeFilePath((String) res.get("secure_url"))
        .rawFileURL((String) res.get("secure_url"))
        .fileFormat((String) res.get("format"))
        .bytes((int) (res.get("bytes")))
        .fileType(FileType.IMAGE)
        .createdAt(getTimestamp((String) res.get("created_at")))
        .build();
  }

  private long getTimestamp(String dateTime) {
    return ZonedDateTime.parse(dateTime).toLocalDateTime().atZone(ZoneId.systemDefault())
        .toEpochSecond();
  }

  private Map<String, String> makeConfig() {
    Map<String, String> config = new HashMap<>();
    config.put("cloud_name", cloudName);
    config.put("api_key", cloudApiKey);
    config.put("api_secret", cloudApiSecret);
    return config;
  }

  private File validateFile(String pathToFile) throws Exception {
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

  private String getFileExtension(File file) {
    String fileName = file.getName();
    if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
      return fileName.substring(fileName.lastIndexOf(".") + 1);
    } else {
      return "";
    }
  }
}
