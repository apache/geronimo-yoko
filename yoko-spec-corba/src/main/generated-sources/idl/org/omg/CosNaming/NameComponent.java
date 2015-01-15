package org.omg.CosNaming;

/**
 * org/omg/CosNaming/NameComponent.java . Error reading Messages File. Error
 * reading Messages File. Thursday, January 14, 2010 1:08:58 AM PST
 */

public final class NameComponent implements org.omg.CORBA.portable.IDLEntity {
    private static final long serialVersionUID = -1052538183391762390L;
    public String id = null;
    public String kind = null;

    public NameComponent() {
    }

    public NameComponent(String _id, String _kind) {
        id = _id;
        kind = _kind;
    }

    @Override
    public String toString() {
        return id + "." + kind;
    }
}
