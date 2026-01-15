package sgf;

import io.Logging;

final class SgfTestSupport {
    private SgfTestSupport() {}

    static String loadExpectedSgf(Object key) {
        if(key==null) throw new RuntimeException("key: "+key+" is nul!");
        String sgf=Parser.getSgfData(key);
        if(sgf==null) return null;
        int p=Parser.parentheses(sgf);
        if(p!=0) {
            Logging.mainLogger.info(" bad parentheses: "+p);
            throw new RuntimeException(key+" bad parentheses: "+p);
        }
        return sgf;
    }

    static void assertSgfDelimiters(String sgf,Object key) {
        if(sgf!=null) if(sgf.startsWith("(")) {
            if(!sgf.endsWith(")")) Logging.mainLogger.info(key+" does not end with an close parenthesis");
        } else if(!sgf.equals("")) {
            throw new AssertionError(key+" does not start with an open parenthesis");
        }
    }

    static void logBadParentheses(String sgf,Object key,String label) {
        if(sgf==null) return;
        int p=Parser.parentheses(sgf);
        if(p!=0) Logging.mainLogger.info(key+" "+label+" bad parentheses: "+p);
    }
}
