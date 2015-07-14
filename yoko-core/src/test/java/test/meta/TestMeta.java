package test.meta;

import java.io.Serializable;

import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;

import org.junit.Assert;
import org.omg.CORBA.ValueDefPackage.FullValueDescription;
import org.omg.SendingContext.CodeBase;

public class TestMeta {

    public static void main(String[] args) throws Exception {
        testMeta();
    }

    public static void testMeta() throws Exception {
        ValueHandler vh = Util.createValueHandler();
        CodeBase codebase = (CodeBase)vh.getRunTimeCodeBase();
        String dataClassRepid = vh.getRMIRepositoryID(Data.class);
        FullValueDescription fvd = codebase.meta(dataClassRepid);
        Assert.assertNotNull(fvd);
    }

    public static class Data implements Serializable {
        private static final long serialVersionUID = 1L;
        public Data d;
    }

}
