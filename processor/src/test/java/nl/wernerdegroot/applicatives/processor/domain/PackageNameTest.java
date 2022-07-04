package nl.wernerdegroot.applicatives.processor.domain;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public final class PackageNameTest {

    @Test
    public void equalsHashCodeToString() {
        EqualsVerifier.forClass(PackageName.class).verify();
        ToStringVerifier.forClass(PackageName.class).verify();
    }
}
