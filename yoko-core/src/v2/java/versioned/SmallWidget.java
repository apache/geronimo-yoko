package versioned;

import acme.Widget;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class SmallWidget extends NonSerializableSuper implements Widget {
    private static final long serialVersionUID = 1L;
    private String a = "a_v2";
    private transient String b = "b_v2";
    private String d = "d_v2";

    @Override
    public Widget validateAndReplace() {
        validateInV2Context();
        return new SmallWidget();
    }

    private void validateInV2Context() {
        // check the v1 was transmitted correctly
        assertThat(a, endsWith("v1"));
        assertThat(b, nullValue());
        assertThat(d, nullValue());
    }

    @Override
    public String toString() {
        return "WidgetImpl{" +
                "a='" + a + '\'' +
                ", b='" + b + '\'' +
                ", d='" + d + '\'' +
                '}';
    }
}
