package versioned;

import java.util.function.Function;

public class FunctionChainer<T,R> implements Function<Function<T,R>, Function<T,R>> {
    @Override
    public Function<T, R> apply(Function<T, R> fun) {
        return fun::apply;
    }
}
