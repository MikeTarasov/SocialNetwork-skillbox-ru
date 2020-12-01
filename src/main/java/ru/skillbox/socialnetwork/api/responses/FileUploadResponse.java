package ru.skillbox.socialnetwork.api.responses;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.skillbox.socialnetwork.model.enums.FileType;


@AllArgsConstructor
@NoArgsConstructor
public class FileUploadResponse {

  private String id;
  private int ownerId;
  private String fileName;
  private String relativeFilePath;
  private String rawFileURL;
  private String fileFormat;
  private long bytes;
  private FileType fileType;
  private long createdAt;
}
