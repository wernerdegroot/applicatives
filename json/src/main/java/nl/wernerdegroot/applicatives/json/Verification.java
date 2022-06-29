package nl.wernerdegroot.applicatives.json;

public interface Verification<T> {

    void verify(T toValidate, ValidationContext ctx);
}
