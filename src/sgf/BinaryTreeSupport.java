package sgf;
import java.util.function.BiConsumer;
import java.util.function.Function;
import utilities.Holder;
final class BinaryTreeSupport {
    private BinaryTreeSupport() {}
    static <T> T lastSibling(T start,Function<T,T> right,Holder<Integer> count) {
        T node=null,last=start;
        for(node=right.apply(start);node!=null;node=right.apply(node)) {
            last=node;
            ++count.t;
        }
        return last;
    }
    static <T> T lastDescendant(T start,Function<T,T> left,Holder<Integer> count) {
        T node=null,last=start;
        for(node=left.apply(start);node!=null;node=left.apply(node)) {
            last=node;
            ++count.t;
        }
        return last;
    }
    static <T> int siblingCount(T start,Function<T,T> right) {
        Holder<Integer> count=new Holder<>(0);
        lastSibling(start,right,count);
        return count.t;
    }
    static <T> T lastChild(T leftChild,Function<T,T> right) {
        if(leftChild==null) return null;
        return lastSibling(leftChild,right,new Holder<>(0));
    }
    static <T> void appendSibling(T start,Function<T,T> right,BiConsumer<T,T> setRight,T sibling) {
        if(start==null) return;
        T last=lastSibling(start,right,new Holder<>(0));
        setRight.accept(last,sibling);
    }
    static <T> void appendDescendant(T start,Function<T,T> left,BiConsumer<T,T> setLeft,T descendant) {
        if(start==null) return;
        T last=lastDescendant(start,left,new Holder<>(0));
        setLeft.accept(last,descendant);
    }
    static <T> void appendChild(T parent,Function<T,T> left,Function<T,T> right,BiConsumer<T,T> setLeft,
            BiConsumer<T,T> setRight,T child) {
        if(parent==null) return;
        T first=left.apply(parent);
        if(first==null) { setLeft.accept(parent,child); return; }
        T last=lastSibling(first,right,new Holder<>(0));
        setRight.accept(last,child);
    }
    static <T> int childCount(T left,Function<T,T> right) {
        if(left==null) return 0;
        return siblingCount(left,right)+1;
    }
}
