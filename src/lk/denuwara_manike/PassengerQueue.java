package lk.denuwara_manike;

public class PassengerQueue {

    //Instance variables of the PassengerQueue Class.
    //All the variables are accessed using getters and setters.
    private static final int SEATING_CAPACITY = 42;
    private Passenger[] queueArray  = new Passenger[SEATING_CAPACITY];;
    private int first;
    private int last;
    private int maxStayInQueue;
    private int minStayInQueue;
    private int averageStayInQueue;
    private int queueLength;
    private int maxLength;

    public PassengerQueue(){
        //Constructor of the Passenger Queue Class.
        super();
        this.first = -1;//The circular array starts at index from front.
        this.last = -1;//The circular array starts at index from rear.
        this.maxLength = 0;
        this.queueLength = SEATING_CAPACITY;
    }

    public void add(Passenger object) {
        //Checks if the circular array is empty.
        if (isEmpty()){
            //Making both front and rear to index zero.
            first = last = 0;
            //Assigning the first Passenger to the circular array.
            queueArray[last] = object;
        }else if (isFull()){
            //If the train queue is full.
            System.out.println("Full");
        }else {
            //Since the queue implementation is a circular array.
            //The rear index may be place at the last or back
            // to the first index .
            //So the rear index is calculated accordingly.
            last = (last + 1)%queueLength;
            queueArray[last] = object;
        }
    }

    public void remove(){
        //Checks if the circular array is empty.
        if (isEmpty()){
            System.out.println("Queue is Empty.");
        }else  if (first == last){
            //Makes rear and front to the  starting position
            // of the circular array.
            first = last = -1;
        }else {
            //Replace the front to the next element.
            //The front index may be place at the last or back
            // to the first index .
            //So the front index is calculated accordingly.
            first = (first+1)%queueLength;
        }
    }

    public boolean isEmpty() {
        return first == -1 && last == -1;
    }

    public boolean isFull(){
        return ((last + 1)%queueLength) == first;
    }

    public Passenger[] getQueueArray() {
        return queueArray;
    }

    public void setQueueArray(Passenger[] queueArray) {
        this.queueArray = queueArray;
    }

    public int getFirst() {
        return first;
    }

    public int getLast() {
        return last;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public void setLast(int last){
        this.last = last;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxStayInQueue(int maxStayInQueue) {
        this.maxStayInQueue = maxStayInQueue;
    }

    public int getMaxStayInQueue() {
        return maxStayInQueue;
    }

    public void setMinStayInQueue(int minStayInQueue) {
        this.minStayInQueue = minStayInQueue;
    }

    public int getMinStayInQueue() {
        return minStayInQueue;
    }

    public void setAverageStayInQueue(int averageStayInQueue) {
        this.averageStayInQueue = averageStayInQueue;
    }

    public int getAverageStayInQueue() {
        return averageStayInQueue;
    }
}

