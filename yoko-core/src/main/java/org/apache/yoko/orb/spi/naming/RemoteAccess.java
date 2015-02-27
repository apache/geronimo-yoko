package org.apache.yoko.orb.spi.naming;

import org.omg.CosNaming.NamingContext;

/** The remote access settings for a name service. */
public enum RemoteAccess{
    /** Remote clients can perform operations on {@link NamingContext} that do not alter the state of the name service.*/
    readOnly, 
    /** Remote clients can perform all operations on {@link NamingContext} objects from this name service.*/
    readWrite
}