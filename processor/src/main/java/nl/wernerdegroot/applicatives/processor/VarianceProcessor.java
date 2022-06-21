package nl.wernerdegroot.applicatives.processor;

import nl.wernerdegroot.applicatives.processor.logging.Log;
import nl.wernerdegroot.applicatives.processor.validation.Validated;
import nl.wernerdegroot.applicatives.processor.validation.Validator;

public interface VarianceProcessor<ToValidate> {

    void generate();
}
