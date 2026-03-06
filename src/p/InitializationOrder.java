package p;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import io.Logging;
interface InitializationOrder {
	AtomicInteger sequence=new AtomicInteger();
	static void trace(String message) {
		System.err.printf("%02d %s%n",sequence.incrementAndGet(),message);
	}
	static void forceInitialize(Class<?> clazz) {
		try {
			Class.forName(clazz.getName(),true,clazz.getClassLoader());
			trace("forced "+clazz.getSimpleName());
		} catch(ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	abstract static class First {
		static {
			trace("First.<clinit> enter");
			Logging.mainLogger.info("First reference second");
			Logging.mainLogger.info(String.valueOf(Second.second));
			trace("First.<clinit> exit");
		}
		public static void main(String[] args) {
			trace("First.main()");
			Logging.mainLogger.info("First.main()");
		}
	}
	enum Second {
		second;
		Second() {
			trace("Second.<init>");
			Logging.mainLogger.info("construct second");
		}
		public static void main(String[] args) {
			trace("Second.main()");
			Logging.mainLogger.info("Second.main()");
		}
		static {
			trace("Second.<clinit>");
			Logging.mainLogger.info("Second::<clinit>");
		}
	}
	static class Fifth extends First {
		public static void main(String[] args) {
			trace("Fifth.main()");
			Logging.mainLogger.info(String.valueOf(5));
		}
		static { // force enum second initialization from any main class.
			trace("Fifth.<clinit>");
			Logging.mainLogger.info("4");
		}
	}
	public static void main(String[] args) {
		Logging.mainLogger.setLevel(Level.ALL);
		trace("InitializationOrder.main()");
		if(args.length==0) {
			forceInitialize(Fifth.class);
			return;
		}
		for(String arg:args) {
			switch(arg.toLowerCase()) {
				case "first":
					forceInitialize(First.class);
					break;
				case "second":
					forceInitialize(Second.class);
					break;
				case "fifth":
					forceInitialize(Fifth.class);
					break;
				default:
					trace("unknown arg: "+arg);
			}
		}
	}
	int y=3; 
	static int x=2;
	//static { trace("x="+x); }
}
