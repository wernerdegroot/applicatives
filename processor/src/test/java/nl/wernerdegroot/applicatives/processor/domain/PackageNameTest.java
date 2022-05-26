package nl.wernerdegroot.applicatives.processor.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public final class PackageNameTest {

    @Test
    public void equals() {
        EqualsVerifier.forClass(PackageName.class).verify();
    }
}
