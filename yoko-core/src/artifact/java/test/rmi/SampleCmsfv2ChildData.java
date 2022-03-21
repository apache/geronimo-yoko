package test.rmi;

public class SampleCmsfv2ChildData extends SampleCmsfv2ParentData {
    private static final long serialVersionUID = 1L;
    private final String value = "splat";
    
    @Override
    public int hashCode() {
        return 31*super.hashCode() + value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;
        if (!(obj instanceof SampleCmsfv2ChildData))
            return false;
        return value.equals(((SampleCmsfv2ChildData) obj).value);
    }
    
    @Override
    public String toString() {
        return String.format("%s:\"%s\"", super.toString(), value);
    }
}
