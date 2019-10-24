package org.apache.yoko;

import org.apache.yoko.osgi.locator.LocalFactory;
import org.apache.yoko.osgi.locator.activator.AbstractBundleActivator;

public final class Activator extends AbstractBundleActivator {
    private enum MyLocalFactory implements LocalFactory {
        INSTANCE;
        @Override
        public Class<?> forName(String clsName) throws ClassNotFoundException {
            return Class.forName(clsName);
        }

        @Override
        public Object newInstance(Class cls) throws InstantiationException, IllegalAccessException {
            return cls.newInstance();
        }
    }

    public Activator() {
        super(MyLocalFactory.INSTANCE,
                "org.omg.BiDirPolicy",
                "org.omg.CONV_FRAME",
                "org.omg.CORBA",
                "org.omg.CORBA.ContainedPackage",
                "org.omg.CORBA.ContainerPackage",
                "org.omg.CORBA.InterfaceDefPackage",
                "org.omg.CORBA.ORBPackage",
                "org.omg.CORBA.PollableSetPackage",
                "org.omg.CORBA.TypeCodePackage",
                "org.omg.CORBA.ValueDefPackage",
                "org.omg.CORBA.portable",
                "org.omg.CORBA_2_3",
                "org.omg.CORBA_2_3.portable",
                "org.omg.CORBA_2_4",
                "org.omg.CORBA_2_4.portable",
                "org.omg.CSI",
                "org.omg.CSIIOP",
                "org.omg.CosNaming",
                "org.omg.CosNaming.NamingContextExtPackage",
                "org.omg.CosNaming.NamingContextPackage",
                "org.omg.CosTSInteroperation",
                "org.omg.CosTransactions",
                "org.omg.Dynamic",
                "org.omg.DynamicAny",
                "org.omg.DynamicAny.DynAnyFactoryPackage",
                "org.omg.DynamicAny.DynAnyPackage",
                "org.omg.GIOP",
                "org.omg.GSSUP",
                "org.omg.IIOP",
                "org.omg.IOP",
                "org.omg.IOP.CodecFactoryPackage",
                "org.omg.IOP.CodecPackage",
                "org.omg.MessageRouting",
                "org.omg.Messaging",
                "org.omg.PortableInterceptor",
                "org.omg.PortableInterceptor.ORBInitInfoPackage",
                "org.omg.PortableServer",
                "org.omg.PortableServer.CurrentPackage",
                "org.omg.PortableServer.POAManagerFactoryPackage",
                "org.omg.PortableServer.POAManagerPackage",
                "org.omg.PortableServer.POAPackage",
                "org.omg.PortableServer.ServantLocatorPackage",
                "org.omg.PortableServer.portable",
                "org.omg.SSLIOP",
                "org.omg.Security",
                "org.omg.SecurityLevel1",
                "org.omg.SecurityLevel2",
                "org.omg.SendingContext",
                "org.omg.SendingContext.CodeBasePackage",
                "org.omg.TimeBase");
    }
}
