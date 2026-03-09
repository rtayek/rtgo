Yes. That split makes sense.

What you are describing is really:

* **bootOnce()**: process-wide, low-level, idempotent
* **bootLogging()** or **bootPerMain()**: repeatable, per-entrypoint policy

That is a valid pattern.

I would not use callbacks first. Callbacks are useful later if you need extension points. Right now, a small explicit bootstrap class is cleaner.

## Recommendation

Use three layers:

* `bootOnce()`

  * initialize ANSI/color tables
  * detect console capabilities
  * install static formatters/helpers
  * safe to call many times; only runs once

* `bootLogging()`

  * configure logger levels/handlers for this program
  * may reset or reconfigure logging
  * safe to call repeatedly

* `boot()`

  * public convenience entry
  * calls `bootOnce()` then `bootLogging()`

That gives you:

* some mains can call `bootOnce()`
* most mains call `boot()`
* tests can call `bootLoggingOff()` if they want silence

## Example 1: plain bootstrap class

```java
package io;

import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

class Bootstrap {
    static void boot() {
        bootOnce();
        bootLogging();
    }

    static void bootOnce() {
        if (booted.compareAndSet(false, true)) {
            detectConsole();
            initAnsiConstants();
        }
    }

    static void bootLogging() {
        bootOnce();
        Logger logger = Logger.getLogger("");
        logger.setUseParentHandlers(false);
        for (Handler handler : logger.getHandlers()) logger.removeHandler(handler);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.OFF);
        logger.addHandler(handler);
        logger.setLevel(Level.OFF);
    }

    static void bootLogging(Level level) {
        bootOnce();
        Logger logger = Logger.getLogger("");
        logger.setUseParentHandlers(false);
        for (Handler handler : logger.getHandlers()) logger.removeHandler(handler);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(level);
        logger.addHandler(handler);
        logger.setLevel(level);
    }

    static boolean ansiEnabled() {
        bootOnce();
        return ansiEnabled;
    }

    static String red() {
        bootOnce();
        return red;
    }

    static String reset() {
        bootOnce();
        return reset;
    }

    static void detectConsole() {
        PrintStream out = System.out;
        ansiEnabled = System.console() != null && out != null;
    }

    static void initAnsiConstants() {
        if (ansiEnabled) {
            red = "\u001B[31m";
            green = "\u001B[32m";
            yellow = "\u001B[33m";
            blue = "\u001B[34m";
            reset = "\u001B[0m";
        } else {
            red = "";
            green = "";
            yellow = "";
            blue = "";
            reset = "";
        }
    }

    static AtomicBoolean booted = new AtomicBoolean();
    static boolean ansiEnabled;
    static String red;
    static String green;
    static String yellow;
    static String blue;
    static String reset;
}
```

Usage:

```java
package app;

import io.Bootstrap;
import java.util.logging.Level;

class Main {
    public static void main(String[] args) {
        Bootstrap.boot();
        // or Bootstrap.bootLogging(Level.INFO);
        System.out.println(Bootstrap.green() + "hello" + Bootstrap.reset());
    }
}
```

## Example 2: better names than once/twice

This is the same idea, but names explain the lifecycle better.

```java
package io;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

class RuntimeInit {
    static void initialize() {
        initializeProcessOnce();
        initializeProgramLogging();
    }

    static void initializeProcessOnce() {
        if (initialized.compareAndSet(false, true)) {
            installConsolePalette();
            detectEnvironment();
        }
    }

    static void initializeProgramLogging() {
        initializeProcessOnce();
        configureLogging(Level.OFF);
    }

    static void configureLogging(Level level) {
        initializeProcessOnce();
        Logger root = Logger.getLogger("");
        root.setUseParentHandlers(false);
        for (Handler handler : root.getHandlers()) root.removeHandler(handler);
        root.setLevel(level);
    }

    static void installConsolePalette() {
        // low-level constant init
    }

    static void detectEnvironment() {
        // windows? ansi? ide console?
    }

    static AtomicBoolean initialized = new AtomicBoolean();
}
```

This is the version I would prefer over `once()` / `twice()`.

## Example 3: callback style, if you really want it

Callbacks are useful if you want “run the standard boot, then do program-specific setup.”

```java
package io;

import java.util.concurrent.atomic.AtomicBoolean;

class Bootstrap {
    static void boot(Runnable afterBoot) {
        bootOnce();
        bootLogging();
        afterBoot.run();
    }

    static void bootOnce() {
        if (booted.compareAndSet(false, true)) {
            initLowLevel();
        }
    }

    static void bootLogging() {
        bootOnce();
        // configure logging
    }

    static void initLowLevel() {
        // colors, console detection
    }

    static AtomicBoolean booted = new AtomicBoolean();
}
```

Usage:

```java
class Main {
    public static void main(String[] args) {
        Bootstrap.boot(() -> {
            // main-specific config
        });
    }
}
```

I would use this only if you actually have many entrypoints with tiny variations.

## Example 4: enum singleton if you want “exactly once” semantics

Not my first choice for your case, but it is very robust.

```java
package io;

import java.util.logging.Level;

enum Bootstrap {
    instance;

    void boot() {
        if (!booted) {
            initLowLevel();
            booted = true;
        }
        configureLogging(Level.OFF);
    }

    void configureLogging(Level level) {
        // repeatable
    }

    void initLowLevel() {
        // once only
    }

    boolean booted;
}
```

Usage:

```java
class Main {
    public static void main(String[] args) {
        Bootstrap.instance.boot();
    }
}
```

This is fine, but a plain utility class is simpler.

## What I would do in your project

Given your style and your codebase, I would do this:

* one small class: `io.Bootstrap`
* one public method:

  * `boot()`
* maybe two optional methods:

  * `bootLogging(Level level)`
  * `bootQuiet()`

Internally:

* `bootOnce()` private/package-private
* `initLowLevel()` private
* `configureLogging(...)` private/package-private

That gives you predictable startup without framework nonsense.

## Suggested shape for your mains

```java
class GoMain {
    public static void main(String[] args) {
        Bootstrap.boot();
        // launch go app
    }
}

class TttMain {
    public static void main(String[] args) {
        Bootstrap.boot();
        // launch ttt app
    }
}

class DebugMain {
    public static void main(String[] args) {
        Bootstrap.bootLogging(Level.FINE);
        // debug launch
    }
}
```

## My opinion on callbacks

Callbacks are not wrong, but they are probably premature here.

Start with explicit methods:

* `boot()`
* `bootLogging(Level)`
* `bootQuiet()`

If later you notice every main does:

* boot
* tweak one logger
* tweak one property
* run app

then introduce callbacks or a small config object.

## The main design rule

`bootOnce()` should do only things that are:

* process-wide
* idempotent
* independent of application mode
* safe before any main logic runs

`bootLogging()` should do only things that are:

* policy
* repeatable
* allowed to differ by main/test/tool

That is a clean split.

If you want, I can write a version tailored to your existing `io.Logging` style and Java package layout.
