package org.opentechfin.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by boweiliu on 4/9/17.
 */
public class RegularUtilsTest {

  @Test
  public void testIsSorted() {
    List<Float> toBeTestedList = Arrays.asList(2.3f, 4.5f, 4.6f, 4.6f, 7.8f);
    toBeTestedList = new ArrayList<>(toBeTestedList);
    Assert.assertTrue(RegularUtils.isSorted(toBeTestedList, Float::compare));
    Collections.reverse(toBeTestedList);
    Assert.assertTrue(RegularUtils.isSorted(toBeTestedList, Float::compare));

    toBeTestedList = Arrays.asList(2.3f, 4.5f, 4.4f, 4.6f, 7.8f);
    Assert.assertFalse(RegularUtils.isSorted(toBeTestedList, Float::compare));
    Collections.reverse(toBeTestedList);
    Assert.assertFalse(RegularUtils.isSorted(toBeTestedList, Float::compare));
  }

}
