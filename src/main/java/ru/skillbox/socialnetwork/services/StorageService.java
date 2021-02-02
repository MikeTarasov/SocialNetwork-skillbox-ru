package ru.skillbox.socialnetwork.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;
import ru.skillbox.socialnetwork.api.responses.FileUploadResponse;
import ru.skillbox.socialnetwork.model.enums.FileType;
import ru.skillbox.socialnetwork.security.PersonDetailsService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StorageService {

  @Value("${cloudinary.cloud.name}")
  private String cloudName;

  @Value("${cloudinary.api.key}")
  private String cloudApiKey;

  @Value("${cloudinary.api.secret}")
  private String cloudApiSecret;

  @Value("#{'${upload.file.types}'.split(',')}")
  private List<String> uploadFileTypes;

  private final PersonDetailsService personDetailsService;

  public StorageService(PersonDetailsService personDetailsService) {
    this.personDetailsService = personDetailsService;
  }


  public ResponseEntity<?> getUpload(String type, MultipartFile file) {
    try {
      validateFile(file);
      Cloudinary cloudinary = new Cloudinary(makeConfig());
      Map res = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

      return ResponseEntity.status(HttpStatus.OK)
              .body(new ErrorTimeDataResponse(
                      "",
                      System.currentTimeMillis(),
                      makeFileUploadResponse(res)));

    }catch (Exception e) {
      e.printStackTrace();

      return ResponseEntity.status(HttpStatus.OK)
          .body(new ErrorTimeDataResponse(
              e.getMessage(),
              System.currentTimeMillis(),
              null));
    }
  }


  public void validateFile(MultipartFile file) throws Exception{
    String contType = file.getContentType();

    if(file.isEmpty()){
      throw new IllegalArgumentException("File can not be empty");
    }
    if(contType == null){
      throw new IllegalArgumentException("Content type is null");
    }

    if(uploadFileTypes.stream().noneMatch(contType::contains)){
      throw new IllegalArgumentException("Unknown file type");
    }

  }

  private FileUploadResponse makeFileUploadResponse(Map<?, ?> res) {
    return FileUploadResponse.builder()
            .id((String) res.get("public_id"))
            .ownerId(personDetailsService.getCurrentUser().getId())
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
        .toInstant().toEpochMilli();
  }

  private Map<String, String> makeConfig() {
    Map<String, String> config = new HashMap<>();
    config.put("cloud_name", cloudName);
    config.put("api_key", cloudApiKey);
    config.put("api_secret", cloudApiSecret);
    return config;
  }
}
