package versioned;

import acme.Widget;
import org.apache.yoko.orb.CORBA.InputStream;

import java.util.function.Function;

/** Read a value from this class's class loading context - i.e. with this class on the call stack */
public class WidgetReader implements Function<InputStream, Widget> {
    public Widget apply(InputStream in) { return (Widget)in.read_value(); }
}
