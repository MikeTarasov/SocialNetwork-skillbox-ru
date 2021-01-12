package ru.skillbox.socialnetwork.model.enums;

public enum ErrorDescriptions {
  UNAUTHORIZED("Unauthorized"),
  NO_AUTHORIZATION_CODE("An authorization code must be supplied"),
  REDIRECT_URI_MISMATCH("Redirect URI mismatch"),
  INVALID_AUTHORIZATION_CODE("Invalid authorization code: CODE"), //TODO CODE - ?????
  BAD_CREDENTIALS("Bad credentials");

  String description;

  ErrorDescriptions(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}