package sim;

public class Customer {
    public final int id;
    public final double arrivalTime;
    public double serviceStartTime=-1;
    public double departureTime=-1;


    public Customer(int id, double arrivalTime){
        this.id=id;
        this.arrivalTime=arrivalTime;
    }

    public double waitingTime(){
        return serviceStartTime-arrivalTime;
    }

    public double systemTime(){
        return departureTime-arrivalTime;
    }
}


