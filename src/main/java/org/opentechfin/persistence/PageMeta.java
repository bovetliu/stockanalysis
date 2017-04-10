package org.opentechfin.persistence;

import org.opentechfin.utils.VerifyArgs;

public class PageMeta {

  protected final int pageSize;

  protected final int pageNumber;

  protected final String repoName;

  protected PageMeta(int pageNumber, int pageSize, String repoName) {
    if (pageSize <= 0) {
      throw new IllegalArgumentException("pageSize should be positive.");
    }
    VerifyArgs.nonNullOrEmptyStr(repoName, "repoName cannot be null or empty.");
    this.pageNumber = pageNumber;
    this.pageSize = pageSize;
    this.repoName = repoName;
  }

  public int getPageSize() {
    return pageSize;
  }

  public int getPageNumber() {
    return pageNumber;
  }

  public String getRepoName() {
    return repoName;
  }
}
