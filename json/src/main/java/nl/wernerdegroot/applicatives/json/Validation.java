package nl.wernerdegroot.applicatives.json;

public interface Validation<A, B> {

    B validate(A toValidate, ValidationContext ctx);
}
