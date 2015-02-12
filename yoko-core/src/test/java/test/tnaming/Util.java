package test.tnaming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.CosNaming.BindingHolder;
import org.omg.CosNaming.BindingIterator;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

public class Util {
    static final NameComponent[] ITERATOR_TEST_CONTEXT_PATH = Util.makeName("iteratorTest");
    static final Set<String> EXPECTED_NAMES = Collections.unmodifiableSet(new TreeSet<>(Arrays.asList("test0 test1 test2 test3 test4 test5 test6 test7 test8 test9".split(" "))));
    static final int NS_PORT = 40001;
    static final String NS_LOC = "corbaloc::localhost:40001/NameService";
    
    static void assertTestIsBound(String expectedId, NamingContextExt ctx, NameComponent ...path) throws CannotProceed, InvalidName {
        assertNotNull(path);
        assertNotEquals(0, path.length);
        try {
            org.omg.CORBA.Object o = ctx.resolve(path);
            Test test = TestHelper.narrow(o);
            assertTrue(test.get_id().equals(expectedId));
        } catch (NotFound e) {
            fail("Should have found Test object at path: " + ctx.to_string(path) );
        }
    }
    
    static void assertNameNotBound(NamingContext initialContext, NameComponent...path) throws CannotProceed, InvalidName {
        try {
            initialContext.resolve(path);
            fail("Expected NotFound exception");
        } catch (NotFound e) {
            // expected exception
        }
    }
    static NameComponent[] makeName(String name) {
        return new NameComponent[]{new NameComponent(name, "")};
    }

    static void assertTestIsBound(Test expected, NamingContextExt initialContext, String name) throws UserException {
        Test test1a = TestHelper.narrow(initialContext.resolve_str(name));
        assertNotNull(test1a);
        assertEquals(test1a.get_id(),expected.get_id());
    }

    static void createBindingsOverWhichToIterate(ORB orb, NamingContext initialContext) throws Exception {
        System.out.println("creating bindings");
        // get the root poa
        POA rootPoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
        System.out.println("got poa");
        // create the context
        NamingContext nc = initialContext.bind_new_context(ITERATOR_TEST_CONTEXT_PATH);
        // add the bindings
        for (String name : EXPECTED_NAMES) 
            nc.bind(makeName(name), new Test_impl(rootPoa, name)._this_object(orb));
    }

    public static void unbindEverything(NamingContext ctx) throws Exception {
        BindingIteratorHolder iterHolder = new BindingIteratorHolder();
        ctx.list(0, new BindingListHolder(), iterHolder);
        BindingIterator bi = iterHolder.value;
        BindingHolder bh = new BindingHolder();
        while (bi.next_one(bh)) {
            if (bh.value.binding_type.value() == BindingType._ncontext) {
                org.omg.CORBA.Object o = ctx.resolve(bh.value.binding_name);
                NamingContext nestedCtx = NamingContextHelper.narrow(o);
                assertNotNull(nestedCtx);
                unbindEverything(nestedCtx);
                nestedCtx.destroy();
            }
            ctx.unbind(bh.value.binding_name);
        }
    }
}
