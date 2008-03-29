package name.kugelman.john.util;

public final class Pair<A, B> {
    public A a;
    public B b;
    
    public Pair() {
        this.a = null;
        this.b = null;
    }
    
    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }
    
    public static<A, B> Pair<A, B> of(A a, B b) {
        return new Pair<A, B>(a, b);
    }
    
    @Override
    public boolean equals(Object that) {
        if (!(that instanceof Pair<?, ?>)) {
            return false;
        }
        
        return this.equals((Pair<?, ?>) that);
    }
    
    public boolean equals(Pair<?, ?> that) {
        if (this.a == null) {
            if (that.a != null) {
                return false;
            }
        }
        else {
            if (!this.a.equals(that.a)) {
                return false;
            }
        }
        
        if (this.b == null) {
            if (that.b != null) {
                return false;
            }
        }
        else {
            if (!this.b.equals(that.b)) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCodeA = (a == null) ? 0 : a.hashCode();
        int hashCodeB = (b == null) ? 0 : b.hashCode();
        
        return hashCodeA ^ hashCodeB;
    }
}
