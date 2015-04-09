package org.apache.yoko.rmi.cmsf;

public final class CmsfThreadLocalStack {
    private static final ThreadLocal<Frame> cmsfFrames = 
            new ThreadLocal<Frame>() {
                @Override protected Frame initialValue() {
                    return Frame.DEFAULT;
                }
            };
    private final static class Frame {
        final static Frame DEFAULT = new Frame();
        final byte value;
        final Frame prev;
        
        private Frame() {
            this.value = 1;
            this.prev = this;
        }
        
        Frame(byte value, Frame prev) {
            this.value = value;
            this.prev = prev;
        }
    }
    
    public static void push(byte value) {
        cmsfFrames.set(new Frame(value, cmsfFrames.get()));
    }
    
    public static byte peek() {
        return cmsfFrames.get().value;
    }
    
    public static byte pop() {
        Frame f = cmsfFrames.get();
        cmsfFrames.set(f.prev);
        return f.value;
    }
}
