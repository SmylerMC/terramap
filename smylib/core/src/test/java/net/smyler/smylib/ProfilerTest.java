package net.smyler.smylib;

import org.junit.jupiter.api.Test;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class ProfilerTest {

    @Test
    void canMeasureSubsectionTimes() throws InterruptedException {
        Profiler profiler = new Profiler();
        profiler.enable();
        profiler.enterSection("child1");
        sleep(100);
        profiler.nextSection("child2");
        sleep(100);
        profiler.enterSection("grandchild1");
        sleep(100);
        profiler.nextSection("grandchild2");
        sleep(100);
        profiler.leaveSection();
        profiler.leaveSection();
        sleep(100);
        profiler.tick();

        Profiler.Section data = profiler.getData();
        assertNotNull(data);
        assertSectionShareOfParent(0.2d, 5e-2d, data, "child1");
        assertSectionShareOfParent(0.6d, 5e-2d, data, "child2");
        assertSectionShareOfParent(0.33d, 5e-2d, data, "child2.grandchild1");
        assertSectionShareOfParent(0.33d, 5e-2d, data, "child2.grandchild2");
    }

    static void assertSectionShareOfParent(double share, double delta, Profiler.Section data, String path) {
        Profiler.Section subsection = data.getSubsectionByPath(path);
        assertNotNull(subsection);
        assertEquals(share, subsection.shareOfParent(), delta);
    }

}
