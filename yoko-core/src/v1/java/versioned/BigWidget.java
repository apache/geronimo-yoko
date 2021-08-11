package versioned;

import acme.Widget;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class BigWidget extends NonSerializableSuper implements Widget {
    private static final long serialVersionUID = 1L;
    private String a = "a_v1";
    private transient String b = "b_v1";
    private String c = "c_v1";
    private String e = "e_v1";
    private String f;
    private transient String g;
    private String h;
    private String j;

    public BigWidget() {
        f = "f_v1";
        g = "g_v1";
        h = "h_v1";
        j = "j_v1";
    }
    
    @Override
    public Widget validateAndReplace() {
        // check the v2 was transmitted correctly
        assertThat(a, endsWith("v2"));
        assertThat(b, nullValue());
        assertThat(c, nullValue());
        assertThat(e, endsWith("v2"));
        assertThat(f, endsWith("v2"));
        assertThat(g, nullValue());
        assertThat(h, nullValue());
        assertThat(j, endsWith("v2"));
        return new BigWidget();
    }

    @Override
    public String toString() {
        return "WidgetImpl{" +
                "a='" + a + '\'' +
                ", b='" + b + '\'' +
                ", c='" + c + '\'' +
                ", e='" + e + '\'' +
                ", f='" + f + '\'' +
                ", g='" + g + '\'' +
                ", h='" + h + '\'' +
                ", j='" + j + '\'' +
                '}';
    }
}
