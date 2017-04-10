package org.opentechfin.persistence;

import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class PageCursor<T> implements Iterator<T> {

  protected int pos = 0;

  protected boolean isForward = true;

  protected List<T> currPageContent;

  protected PageCursor(PageCursor<T> pageCursor) {
    this(pageCursor.currPageContent);
  }

  protected PageCursor(List<T> currPageContentParam) {
    currPageContent = currPageContentParam;
  }

  protected PageCursor(List<T> currPageContentParam, boolean isForwardParam) {
    currPageContent = currPageContentParam;
    isForward = isForwardParam;
    pos = isForwardParam ? 0 : currPageContentParam.size() - 1;
  }

  @Override
  public boolean hasNext() {
    if (isForward) {
      return pos < currPageContent.size() - 1;
    } else {
      return pos > 0;
    }
  }

  @Override
  public T next() {
    if (!hasNext()) {
      throw new IllegalStateException("invoke next() will cause index out of boundary");
    }
    pos += isForward ? 1 : -1;
    return currPageContent.get(pos);
  }

  public T peek() {
    return currPageContent.get(pos);
  }

  public boolean isForward() {
    return isForward;
  }

  public void setForward(boolean forward) {
    isForward = forward;
  }
}
