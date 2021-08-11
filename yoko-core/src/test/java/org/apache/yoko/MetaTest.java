package org.apache.yoko;

import java.io.Serializable;

import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;

import junit.framework.TestCase;

import org.junit.Assert;
import org.omg.CORBA.ValueDefPackage.FullValueDescription;
import org.omg.SendingContext.CodeBase;


public class MetaTest extends TestCase {

    public void testMetaForClassWithASelfReference() {
        ValueHandler vh = Util.createValueHandler();
        CodeBase codebase = (CodeBase)vh.getRunTimeCodeBase();
        String dataClassRepid = vh.getRMIRepositoryID(Data.class);
        FullValueDescription fvd = codebase.meta(dataClassRepid);
        Assert.assertNotNull(fvd);
    }

    public void testMetaForClassWithTwoSelfReferences() {
        ValueHandler vh = Util.createValueHandler();
        CodeBase codebase = (CodeBase)vh.getRunTimeCodeBase();
        String dataClassRepid = vh.getRMIRepositoryID(X.class);
        System.out.println(dataClassRepid);
        FullValueDescription fvd = codebase.meta(dataClassRepid);
        Assert.assertNotNull(fvd);
    }

    public static class Data implements Serializable {
        private static final long serialVersionUID = 1L;
        public Data d;
    }

    public static class X {
        X x1;
        X x2;
    }
}


