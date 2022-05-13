package nl.wernerdegroot.applicatives.runtime;

public interface Tuple0 {

    <First> Tuple1<First> withFirst(First second);
}

