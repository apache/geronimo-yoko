package org.apache.yoko.orb.CosNaming.tnaming2;

public class NameServiceReadOnlyInitializer extends NameServiceInitializer {
    @Override
    boolean remoteReadOnly() {
        return true;
    }

}
