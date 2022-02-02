package nl.wernerdegroot.applicatives.processor.domain.containing;

import nl.wernerdegroot.applicatives.processor.domain.PackageName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ContainingPackageTest {

    private final PackageName packageName = new PackageName("nl.wernerdegroot.applicatives");

    @Test
    public void of() {
        ContainingPackage expected = new ContainingPackage(packageName);
        ContainingPackage toVerify = ContainingPackage.of(packageName);

        assertEquals(expected, toVerify);
    }

    // Test for `match` not necessary. If it compiles, it must work.

    @Test
    public void getPackageName() {
        PackageName expected = packageName;
        PackageName toVerify = new ContainingPackage(packageName).getPackageName();

        assertEquals(expected, toVerify);
    }

    @Test
    public void isPackage() {
        assertTrue(new ContainingPackage(packageName).isPackage());
    }

    @Test
    public void isClass() {
        assertFalse(new ContainingPackage(packageName).isClass());
    }

    @Test
    public void isOuterClass() {
        assertFalse(new ContainingPackage(packageName).isOuterClass());
    }

    @Test
    public void isStaticInnerClass() {
        assertFalse(new ContainingPackage(packageName).isStaticInnerClass());
    }
}
