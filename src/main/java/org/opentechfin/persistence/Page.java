package org.opentechfin.persistence;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Page <T> implements Iterable<T> {

  protected PageCursor<T> pageCursor;

  protected List<T> pageContent;

  protected PageMeta meta;

  protected Page(PageMeta metaParam, List<T> pageContentParam) {
    meta = metaParam;
    pageContent = pageContentParam;
    pageCursor = new PageCursor<>(pageContent);
  }

  public List<T> getPageContent() {
    return pageContent;
  }

  public Iterator<T> iterator() {
    return new PageCursor<>(pageCursor);
  }

  public T cursorAt() {
    return pageCursor.peek();
  }

  public boolean canMoveCursorNext() {
    return pageCursor.hasNext();
  }

  public T moveCursorNext() {
    return pageCursor.next();
  }

  public PageMeta getMeta() {
    return meta;
  }

  public static <T> Page<T> create(PageMeta pageMeta, List<T> pageContent) {
    return new Page<>(pageMeta, pageContent);
  }

  public static <T> Page<T> emptyPage(PageMeta pageMeta) {
    return new Page<>(pageMeta, Collections.<T>emptyList());
  }
}
