package lk.denuwara_manike;

public class Passenger implements java.io.Serializable ,Comparable<Passenger>{

    //Instance variables of the Passenger Class.
    //All the variables are accessed using getters and setters.
    private String firstName;
    private String surname;
    private int passengerSeatNo;
    private int secondsInQueue;

    public String getName(){
        return firstName+" "+surname;
    }

    public String getFirstName(){
        return firstName;
    }

    public void setName(String name,String surname){
        this.firstName = name;
        this.surname = surname;
    }

    public void setPassengerSeatNo(int passengerSeatNo) {
        this.passengerSeatNo = passengerSeatNo;
    }

    public int getPassengerSeatNo() {
        return passengerSeatNo;
    }

    public int getSecondsInQueue(){
        return secondsInQueue;
    }

    public void setSecondsInQueue(int seconds){
        this.secondsInQueue = seconds;
    }

    //Overriding the Comparable Class
    @Override
    public int compareTo(Passenger o) {
        //The bubble sorting is compared accordingly.
        return this.getPassengerSeatNo() - o.getPassengerSeatNo();
    }
}
