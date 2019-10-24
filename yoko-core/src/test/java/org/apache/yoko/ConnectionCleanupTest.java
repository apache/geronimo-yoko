package org.apache.yoko;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import testify.iiop.Skellington;
import testify.util.MultiException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static javax.rmi.PortableRemoteObject.narrow;

public class ConnectionCleanupTest {
    ORB serverORB;
    ORB clientORB;

    @Before
    public void setup() throws Exception {
        serverORB = ORB.init((String[])null, null);
        clientORB = ORB.init((String[])null, null);
    }

    @Test
    public void testOneClient() throws Exception {
        newRemoteImpl().gcAndSleep(1000);
    }

    private TheInterface newRemoteImpl() {
        try {
            return (TheInterface)narrow(clientORB.string_to_object(new TheImpl().publish(serverORB)), TheInterface.class);
        } catch (InvalidName | AdapterInactive | WrongPolicy | ServantAlreadyActive e) {
            e.printStackTrace();
            throw new AssertionError(e);
        }
    }

    @Test
    public void testRecursiveClient() throws Exception {
        newRemoteImpl().gcAndSleepRecursive(10000, 100);
    }

    //@Test
    public void doNotTestOneHundredClients() throws Throwable {
        // To avoid multiple threads initializing the singleton at once
        // during the first call to PRO.narrow() force it to be called
        // once before the other threads start.
        testOneClient();
        // OK - now do a hundred threads.
        List<Future<Throwable>> futures = new ArrayList<>();
        ExecutorService xs = Executors.newFixedThreadPool(100);
        final CyclicBarrier cb = new CyclicBarrier(101);
        for (int i = 0; i < 100; i++) {
            futures.add(xs.submit(new Callable<Throwable>() {
                @Override
                public Throwable call() throws Exception {
                    try {
                        cb.await();
                        recurse(10); // use recursion so stack trace tells us how far in we failed
                        return null;
                    } catch (Throwable t) {
                        return t;
                    }
                }
                private void recurse(int times) throws Exception {
                    testOneClient();
                    if (times > 0) recurse(times - 1);
                }
            }));
        }
        cb.await();

        MultiException me = new MultiException(futures);
        if (me.isEmpty()) return;
        throw me;
    }


    public interface TheInterface extends Remote {
        void gcAndSleep(long millis) throws RemoteException;
        void gcAndSleepRecursive(long millis, int depth) throws RemoteException;
    }

    private class TheImpl extends Skellington implements TheInterface {
        @Override
        public void gcAndSleep(long millis) {
            //forceGarbageCollection();
            try {
                Thread.sleep(millis);
            } catch (InterruptedException ignored) {}
        }

        @Override
        public void gcAndSleepRecursive(long millis, int depth) throws RemoteException  {
            if (depth == 1)
                newRemoteImpl().gcAndSleep(millis);
            else
                newRemoteImpl().gcAndSleepRecursive(millis, depth - 1);
        }

        @Override
        protected OutputStream dispatch(String method, InputStream in, ResponseHandler reply) throws RemoteException {
            switch (method) {
                case "gcAndSleep":
                    gcAndSleep(in.read_longlong());
                    return reply.createReply();
                case "gcAndSleepRecursive":
                    gcAndSleepRecursive(in.read_longlong(), in.read_long());
                    return reply.createReply();
                default:
                    throw new BAD_OPERATION();
            }
        }
    }

    private static long forceGarbageCollection() {
        List<byte[]> extents = new ArrayList<>();
        long tally = 0;
        // allocate as much as possible, halve the size and try again
        for (int i = 30; i >= 0; i--) {
            try {
                do {
                    int alloc = 1 << i;
                    extents.add(new byte[alloc]);
                    tally += alloc;
                } while (true);
            } catch (OutOfMemoryError oom) {}
        }
        // now the heap should be full so even the smallest allocation should fail
        try {
            for (int i = 0; i < 1024; i++) extents.add(new byte[128]);
            Assert.fail("this allocation should have failed");
        } catch (OutOfMemoryError e) {}
        System.gc();
        return tally;
    }
}
