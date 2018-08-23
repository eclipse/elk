package org.eclipse.elk.alg.test.framework.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * This test is executed to cause a test failure. This should be done if a white box test has not been executed. This
 * can be happen if the test should be executed before or after processors which are not executed.
 */
public class NoExecutionFailure {

    /**
     * This test fails in every case.
     */
    @Test
    public void failTest() {
        Assert.assertTrue("An white box test should be executed at least once.", false);
    }

}
