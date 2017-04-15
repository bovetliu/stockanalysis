package org.opentechfin.persistence;

import java.util.Objects;
import java.util.function.BiFunction;
import org.opentechfin.utils.VerifyArgs;

public class PageMeta {

  /**
   * Defined for readability.
   */
  private static final boolean IS_FOR_NEXT_PAGE = true;

  public static final BiFunction<PageMeta, Boolean, PageMeta> DEFAULT_BIFUNC = (pageMeta, aBoolean) ->
      new PageMeta(pageMeta.pageNumber + (aBoolean ? 1 : -1), pageMeta.pageSize, pageMeta.repoName);

  protected final int pageSize;

  protected final int pageNumber;

  protected final String repoName;

  protected final BiFunction<PageMeta, Boolean, PageMeta> prevNextPageMetaBiFunc;

  protected PageMeta(int pageNumber, int pageSize, String repoName) {
    this(pageNumber, pageSize, repoName, DEFAULT_BIFUNC);
  }

  protected PageMeta(int pageNumber, int pageSize, String repoName,
      BiFunction<PageMeta, Boolean, PageMeta> pageMetaBiFunc) {
    if (pageSize <= 0) {
      throw new IllegalArgumentException("pageSize should be positive.");
    }
    Objects.requireNonNull(pageMetaBiFunc, "cannot be null");
    VerifyArgs.nonNullOrEmptyStr(repoName, "repoName cannot be null or empty.");
    this.pageNumber = pageNumber;
    this.pageSize = pageSize;
    this.repoName = repoName;
    prevNextPageMetaBiFunc = pageMetaBiFunc;
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

  public BiFunction<PageMeta, Boolean, PageMeta> getPrevNextPageMetaBiFunc() {
    return prevNextPageMetaBiFunc;
  }

  public PageMeta getNextPageMeta() {
    return prevNextPageMetaBiFunc.apply(this, IS_FOR_NEXT_PAGE);
  }

  public PageMeta getPrevPageMeta() {
    return prevNextPageMetaBiFunc.apply(this, !IS_FOR_NEXT_PAGE);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PageMeta pageMeta = (PageMeta) o;

    if (pageSize != pageMeta.pageSize) {
      return false;
    }
    if (pageNumber != pageMeta.pageNumber) {
      return false;
    }
    return repoName.equals(pageMeta.repoName);
  }

  @Override
  public int hashCode() {
    int result = pageSize;
    result = 31 * result + pageNumber;
    result = 31 * result + repoName.hashCode();
    return result;
  }

  public static PageMeta create(int pageNumber, int pageSize, String repoName) {
    return new PageMeta(pageNumber, pageSize, repoName);
  }

  public static PageMeta create(int pageNumber, int pageSize, String repoName,
      BiFunction<PageMeta, Boolean, PageMeta> biFunction) {
    return new PageMeta(pageNumber, pageSize, repoName, biFunction);
  }
}
