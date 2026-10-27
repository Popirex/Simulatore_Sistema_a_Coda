package sim;

public class Event implements Comparable<Event>{
    public final double time;
    public final EventType type;
    public final Customer customer;
    public final long seq;

    public Event(double time, EventType type, Customer customer, long seq){
        this.time=time;
        this.type=type;
        this.customer=customer;
        this.seq=seq;
    }

    @Override 
    public int compareTo(Event other){
        int cmp=Double.compare(this.time, other.time);
        if (cmp!=0) {
            return cmp;
        }
        
        return Long.compare(this.seq, other.seq);
    }
}
