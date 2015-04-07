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
        final int value;
        final Frame prev;
        
        private Frame() {
            this.value = 1;
            this.prev = this;
        }
        
        Frame(int value, Frame prev) {
            this.value = value;
            this.prev = prev;
        }
    }
    
    public static void push(int value) {
        cmsfFrames.set(new Frame(value, cmsfFrames.get()));
    }
    
    public static int peek() {
        return cmsfFrames.get().value;
    }
    
    public static int pop() {
        Frame f = cmsfFrames.get();
        cmsfFrames.set(f.prev);
        return f.value;
    }
}
