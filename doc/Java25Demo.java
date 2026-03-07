package io;
import java.lang.reflect.StableValue;

class Base {
    static { System.out.println("1. Base: Static Initializer"); }
    { System.out.println("4. Base: Instance Initializer"); }

    Base(String data) {
        System.out.println("5. Base: Constructor (received: " + data + ")");
    }
}

class Sub extends Base {
    // Stable Value: Lazily initialized once when first accessed
    private static final StableValue<String> LAZY_CONFIG = StableValue.of();

    static { System.out.println("2. Sub: Static Initializer"); }
    
    private final int value;
    { System.out.println("6. Sub: Instance Initializer"); }

    Sub(int input) {
        // --- PHASE 1: Early Construction Context (New in Java 25) ---
        // 3. Statements before super() can now validate and assign fields
        if (input < 0) throw new IllegalArgumentException("Must be positive");
        this.value = input; // Field assignment allowed before super()
        System.out.println("3. Sub: Early Construction (value assigned: " + value + ")");
        
        super("Data-" + input); // Explicit call to superclass constructor

        // --- PHASE 2: Post-Super Initialization ---
        System.out.println("7. Sub: Constructor Epilogue");
    }

    public void showLazy() {
        // Initialized only on first call
        String config = LAZY_CONFIG.computeIfUnset(() -> "Initialized Late");
        System.out.println("8. Lazy Config: " + config);
    }
}

public class Java25Demo {
    public static void main(String[] args) {
        Sub s = new Sub(10);
        s.showLazy();
    }
}
