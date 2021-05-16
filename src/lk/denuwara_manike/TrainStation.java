package lk.denuwara_manike;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class TrainStation extends Application {

    public static final int SEATING_CAPACITY = 42;
    //Data Structure which stores all details of the passengers that
    // is loaded from the database.
    private static ArrayList<String> bookedPassengers = new ArrayList<>();
    //Data Structure which stores all of the passengers only if arrived.
    private Passenger[] waitingRoom = new Passenger[SEATING_CAPACITY];
    //Creating a PassengerQueue object.
    private PassengerQueue trainQueue = new PassengerQueue();
    //Data structure which stores
    private ArrayList<Passenger> simulatedPassengers = new ArrayList<>();

    public static void main(String[] args) {
        launch();
    }

    public void start(Stage primaryStage) {
        //Loads the booked passengers
        loadBookingDetails();
        //Checks for bookings of the day.
        if (!bookedPassengers.isEmpty()) {
            //GUI to add booked passengers if arrived
            // to the waiting room.
            addingPassengersToWaitingRoom(primaryStage);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("No Bookings for the day.");
            alert.showAndWait();
        }

    }

    private void menu(Stage primaryStage)
            throws IOException, ClassNotFoundException {

        Scanner input = new Scanner(System.in);

        System.out.println("-----------------------------------------------");
        System.out.println("A - Add a passenger to the Train Queue.");
        System.out.println("V - View the Train Queue.");
        System.out.println("D - Delete Passenger from the Train Queue.");
        System.out.println("S - Store Train Queue data.");
        System.out.println("L - Load Train Queue data.");
        System.out.println("R - Running the Simulation details.");
        System.out.println("Q - To exit program");
        System.out.println("-----------------------------------------------");

        System.out.print("Enter Menu Option : ");
        String option = input.next().toLowerCase();

        switch (option) {
            case "a":
                addingToTrainQueue(primaryStage);
                break;
            case "v":
                viewTrainQueue(primaryStage);
                break;
            case "d":
                deletePassenger(primaryStage);
                break;
            case "s":
                storeTrainQueue(primaryStage);
                break;
            case "l":
                loadTrainQueue(primaryStage);
                break;
            case "r":
                runSimulation(primaryStage);
                break;
            case "q":
                break;
            default:
                System.out.println("Invalid Input option entered.");
                menu(primaryStage);
        }
    }

    private void addingToTrainQueue(Stage primaryStage) {

        //To place a object on the GUI listview a ObservabelList.
        ObservableList<Passenger> passengerObservableList =
                FXCollections.observableArrayList();
        ListView<Passenger> listView = new ListView<>(passengerObservableList);

        Label title = new Label("WAITING ROOM");
        title.setStyle("-fx-font-size:38px;-fx-font-weight:bold;-fx-background-color:white");
        Button randomBtn = new Button("GENERATE RANDOM NUMBER");
        Button backBtn = new Button("BACK");
        Button queueBtn = new Button("QUEUE");

        implementListView(passengerObservableList, listView, randomBtn);

        backBtn.setStyle("-fx-border-color:black;-fx-font-size:15px;" +
                "fx-font-weight:bold;-fx-border-width:3px;" +
                "-fx-border-radius: 5px;-fx-background-color:white");
        queueBtn.setStyle("-fx-border-color:black;-fx-font-size:13.5px;" +
                "-fx-font-weight:bold;-fx-border-width:3px;" +
                "-fx-border-radius: 5px;;-fx-background-color:white");

        backBtn.setOnAction(event -> {
            primaryStage.close();
            try {
                menu(primaryStage);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        queueBtn.setOnAction(event -> {
            primaryStage.close();
            displayTrainQueue(primaryStage);
        });

        Label randomLabel = new Label("");

        VBox vBox2 = new VBox(10);
        vBox2.getChildren().addAll(queueBtn, backBtn);
        vBox2.setPadding(new Insets(0, 0, 0, 550));

        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(title, listView, randomLabel, randomBtn, vBox2);
        vBox.setPadding(new Insets(30, 30, 30, 30));
        vBox.setStyle("-fx-background-color:white;");

        int[] randomNumber = {0};
        randomBtn.setOnAction(event -> {
            if (!trainQueue.isFull()) {
                Random random = new Random();
                randomNumber[0] = random.nextInt(6) + 1;
                randomLabel.setText("Number of passengers t" +
                        "o be moved to the Train Queue : "
                        + randomNumber[0]);
                vBox.getChildren().remove(randomBtn);
                listView.setDisable(false);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText("The Train Queue is Full.");
                alert.showAndWait();
                listView.setDisable(true);
            }
        });
        int[] count = {0};
        listView.setOnMouseClicked(event -> {
            int selectedIdx = listView.getSelectionModel()
                    .getSelectedIndex();
            Passenger objectPassenger = listView.getSelectionModel()
                    .getSelectedItem();

            addingObjectToQueue(objectPassenger, listView);

            listView.getItems().remove(selectedIdx);
            if (count[0] == randomNumber[0] - 1) {
                listView.setMouseTransparent(true);
            }
            count[0]++;

        });

        Scene scene = new Scene(vBox, 700, 600);
        primaryStage.setTitle("Waiting Room");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void implementListView(ObservableList<Passenger> observe,
                                   ListView<Passenger>
                                           listView, Button randomBtn) {
        boolean waitingRoomEmpty = true;
        if (!trainQueue.isFull()) {
            for (int i = 0; i < SEATING_CAPACITY; i++) {
                if (waitingRoom[i] != null) {
                    waitingRoomEmpty = false;
                    observe.add(waitingRoom[i]);
                }
            }
            listView.setPrefHeight(400);
            listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            listView.setCellFactory(param -> new ListCell<Passenger>() {
                @Override
                protected void updateItem(Passenger item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null || item.getName() == null) {
                        setText(null);
                    } else {
                        String seat = "   Seat Number : "
                                + item.getPassengerSeatNo() + "    ";
                        String name = " Full Name : " + item.getName();
                        String text = seat + name;
                        setText(text);
                    }
                }
            });
            listView.setDisable(true);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("The Train Queue is Full.");
            alert.showAndWait();
            listView.setDisable(true);
        }
        if (waitingRoomEmpty) {
            randomBtn.setDisable(true);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("The Waiting Room is Empty.");
            alert.showAndWait();
            listView.setDisable(true);
        }

    }

    private void addingObjectToQueue(Passenger objectPassenger,
                                     ListView<Passenger> listView) {
        if (!trainQueue.isFull()) {
            waitingRoom[objectPassenger.getPassengerSeatNo() - 1] = null;
            trainQueue.add(objectPassenger);
            trainQueue.setQueueArray(bubbleSort(trainQueue.getQueueArray()));
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("The Train Queue is Full.");
            alert.showAndWait();
            listView.setDisable(true);
        }
    }

    private void displayTrainQueue(Stage primaryStage) {

        ObservableList<Passenger> passengerObservableList =
                FXCollections.observableArrayList();
        ObservableList<String> stringObservableList =
                FXCollections.observableArrayList();
        ListView<String> listView = new ListView<>(stringObservableList);

        int front = trainQueue.getFirst();
        int rear = trainQueue.getLast();

        if (trainQueue.isEmpty()) {
            System.out.println("The Queue is empty.");
        } else {
            while (front != rear) {
                passengerObservableList.add(trainQueue.getQueueArray()[front]);
                front = (front + 1) % SEATING_CAPACITY;
            }
            passengerObservableList.add(trainQueue.getQueueArray()[rear]);
        }

        for (int i = 0; i < SEATING_CAPACITY; i++) {
            int slot = i + 1;
            int size = passengerObservableList.size();
            String slotName = "";
            if (i < size) {
                String seat = "    Seat Number : "
                        + passengerObservableList.get(i).getPassengerSeatNo();
                String name = ". Full Name : "
                        + passengerObservableList.get(i).getName();
                slotName = slot + name + seat;
            } else {
                slotName = slot + ". Empty";
            }
            stringObservableList.add(slotName);
        }

        Label title = new Label("TRAIN QUEUE");
        title.setStyle("-fx-font-size:38px;" +
                "-fx-font-weight:bold;-fx-background-color:white");
        listView.setFocusTraversable(false);

        Button menuBtn = new Button("MENU");

        menuBtn.setStyle("-fx-border-color:black;" +
                "-fx-font-size:15px;-fx-font-weight:bold;" +
                "-fx-border-width:3px;" +
                "-fx-border-radius: 5px;;-fx-background-color:white");

        menuBtn.setOnAction(event -> {
            primaryStage.close();
            try {
                menu(primaryStage);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        Label randomLabel = new Label("");

        VBox vBox2 = new VBox(10);
        vBox2.getChildren().addAll(menuBtn);
        vBox2.setPadding(new Insets(0, 0, 0, 550));

        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(title, listView, randomLabel, vBox2);
        vBox.setPadding(new Insets(30, 30, 30, 30));
        vBox.setStyle("-fx-background-color:white;");

        Scene scene = new Scene(vBox, 700, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Train Queue");
        primaryStage.show();
    }

    private void viewTrainQueue(Stage primaryStage) {

        ObservableList<Passenger> passengerObservableList =
                FXCollections.observableArrayList();
        ListView<Passenger> listView = new ListView<>(passengerObservableList);

        Label title = new Label("WAITING ROOM");
        title.setStyle("-fx-font-size:38px;-fx-font-weight:bold;" +
                "-fx-background-color:white");
        Button randomBtn = new Button("GENERATE RANDOM NUMBER");
        Button seatBtn = new Button("TRAIN");
        Button queueBtn = new Button("QUEUE");

        implementListView(passengerObservableList, listView, randomBtn);
        listView.setDisable(false);

        seatBtn.setStyle("-fx-border-color:black;-fx-font-size:15px;-" +
                "fx-font-weight:bold;-fx-border-width:3px;" +
                "-fx-border-radius: 5px;;-fx-background-color:white");
        queueBtn.setStyle("-fx-border-color:black;-fx-font-size:13.5px;" +
                "-fx-font-weight:bold;-fx-border-width:3px;" +
                "-fx-border-radius: 5px;;-fx-background-color:white");

        seatBtn.setOnAction(event -> {
            primaryStage.close();
            try {
                boardedPassenger(primaryStage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });

        queueBtn.setOnAction(event -> {
            primaryStage.close();
            displayTrainQueue(primaryStage);
        });


        VBox vBox2 = new VBox(10);
        vBox2.getChildren().addAll(queueBtn, seatBtn);
        vBox2.setPadding(new Insets(0, 0, 0, 550));

        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(title, listView, vBox2);
        vBox.setPadding(new Insets(30, 30, 30, 30));
        vBox.setStyle("-fx-background-color:white;");

        Scene scene = new Scene(vBox, 700, 600);
        primaryStage.setTitle("Waiting Room");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void boardedPassenger(Stage window) throws FileNotFoundException {

        Label title = new Label("BOARDED SEATS");
        title.setStyle("-fx-font-size:38px;-fx-font-weight:bold;" +
                "-fx-background-color:white");

        if (!simulatedPassengers.isEmpty()) {
            String[] mainArray = new String[SEATING_CAPACITY];

            for (int i = 0; i < simulatedPassengers.size(); i++) {
                int index = simulatedPassengers.get(i).getPassengerSeatNo();
                mainArray[index - 1] = simulatedPassengers.get(i).getFirstName();
            }

            FileInputStream imageIn = new FileInputStream("seat.png");
            Image image = new Image(imageIn);

            ImageView[] imageViews1 = new ImageView[23];
            ImageView[] imageViews2 = new ImageView[23];
            ImageView[] imageViews3 = new ImageView[44];
            ImageView[] imageViews4 = new ImageView[44];

            Button[] btn1 = new Button[22];
            Button[] btn2 = new Button[23];
            Button[] btn3 = new Button[43];
            Button[] btn4 = new Button[42];

            HBox row1 = new HBox(10);
            HBox row2 = new HBox(9);
            HBox row3 = new HBox(7);
            HBox row4 = new HBox(7);

            row1.setPadding(new

                    Insets(40, 10, 10, 40));
            row2.setPadding(new

                    Insets(10, 10, 30, 40));
            row3.setPadding(new

                    Insets(30, 10, 20, 40));
            row4.setPadding(new

                    Insets(5, 10, 20, 40));

            for (int i = 1; i <= btn1.length; i = i + 2) {

                imageViews1[i] = new ImageView(image);
                btn1[i] = new Button(Integer.toString(i), imageViews1[i]);
                btn1[i].setMouseTransparent(true);
                row1.getChildren().add(btn1[i]);
                if (mainArray[i - 1] != null) {
                    btn1[i].setStyle("-fx-background-color:#e06812");
                } else {
                    btn1[i].setStyle("-fx-background-color:#32CD32");
                }
            }

            for (int i = 2; i <= btn2.length; i = i + 2) {
                imageViews2[i] = new ImageView(image);
                btn2[i] = new Button(Integer.toString(i), imageViews2[i]);
                btn2[i].setMouseTransparent(true);
                row2.getChildren().add(btn2[i]);
                if (mainArray[i - 1] != null) {
                    btn2[i].setStyle("-fx-background-color:#e06812");
                } else {
                    btn2[i].setStyle("-fx-background-color:#32CD32");
                }
            }
            for (int i = 24; i <= btn3.length; i = i + 2) {
                imageViews3[i] = new ImageView(image);
                btn3[i] = new Button(Integer.toString(i), imageViews3[i]);
                btn3[i].setMouseTransparent(true);
                row3.getChildren().add(btn3[i]);

                if (mainArray[i - 1] != null) {
                    btn3[i].setStyle("-fx-background-color:#e06812");
                } else {
                    btn3[i].setStyle("-fx-background-color:#32CD32");
                }
            }
            for (int i = 23; i <= btn4.length; i = i + 2) {
                imageViews4[i] = new ImageView(image);
                btn4[i] = new Button(Integer.toString(i), imageViews4[i]);
                btn4[i].setMouseTransparent(true);
                row4.getChildren().add(btn4[i]);

                if (mainArray[i - 1] != null) {
                    btn4[i].setStyle("-fx-background-color:#e06812");
                } else {
                    btn4[i].setStyle("-fx-background-color:#32CD32");
                }
            }

            Button menuBtn = new Button("MENU");
            menuBtn.setStyle("-fx-border-color:black;-fx-font-size:28px;" +
                    "-fx-font-weight:bold;-fx-border-width:3px;" +
                    "-fx-border-radius: 5px;;-fx-background-color:white");
            menuBtn.setOnAction(event -> {
                window.close();
                try {
                    menu(window);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });

            HBox header = new HBox(10);
            header.setPadding(new Insets(10, 0, 10, 20));
            header.getChildren().add(title);

            HBox footer = new HBox(10);
            footer.setPadding(new Insets(30, 0, 0, 1270));
            footer.getChildren().add(menuBtn);

            Pane layout = new FlowPane();
            layout.getChildren().addAll(header, row1, row2, row3, row4, footer);
            layout.setPadding(new Insets(20, 20, 20, 20));
            layout.setStyle("-fx-background-color:white;");

            window.setTitle("View All Seats");
            Scene scene2 = new Scene(layout, 1500, 750);
            window.setScene(scene2);
            window.setResizable(false);
            window.setTitle("Boarded Seats");
            window.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("No Passengers Boarded!");
            alert.showAndWait();
            try {
                menu(window);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private void deletePassenger(Stage primaryStage)
            throws IOException, ClassNotFoundException {

        ArrayList<Integer> checkSeat = new ArrayList<>();

        Scanner sc = new Scanner(System.in);

        int front = trainQueue.getFirst();
        int rear = trainQueue.getLast();

        if (trainQueue.isEmpty()) {
            System.out.println("The Queue is Empty.");
        } else {
            while (front != rear) {
                int seatNo = trainQueue.
                        getQueueArray()[front].getPassengerSeatNo();
                checkSeat.add(seatNo);
                String seat = "Seat Number :" + seatNo;
                String name = "(" + trainQueue.
                        getQueueArray()[front].getName() + ")";
                System.out.println(seat + name);
                front = (front + 1) % SEATING_CAPACITY;
            }

            int seatNo = trainQueue.getQueueArray()[rear].
                    getPassengerSeatNo();
            checkSeat.add(seatNo);
            String seat = "Seat Number :" + seatNo;
            String name = "(" + trainQueue.getQueueArray()[rear].
                    getName() + ")";
            System.out.println(seat + name);
            System.out.print("Enter Seat No to Delete passenger from Queue :");
            try {
                boolean noSeatFound = true;
                int delete = sc.nextInt();
                System.out.println();
                int index = 0;
                for (int i = 0; i < checkSeat.size(); i++) {
                    if (delete == checkSeat.get(i)) {
                        index = i;
                        noSeatFound = false;
                        break;
                    }
                }
                if (!noSeatFound) {

                    System.out.println("Seat Number Deleted Successfully.");
                    System.out.println("Deleted Passenger Details:");
                    System.out.println("Name  : " + trainQueue.getQueueArray()
                            [index].getName());
                    System.out.println("SeatNo: " + trainQueue.getQueueArray()
                            [index].getPassengerSeatNo());

                    boolean zeroRear = true;
                    Passenger temp;
                    trainQueue.getQueueArray()[index] = null;
                    for (int j = index; j < SEATING_CAPACITY; j++) {
                        if (front == 0 && rear == 0) {
                            zeroRear = false;
                            trainQueue.getQueueArray()[trainQueue.getLast()] = null;
                            break;
                        } else if (j != SEATING_CAPACITY - 1) {
                            temp = trainQueue.getQueueArray()[j];
                            trainQueue.getQueueArray()[j] = trainQueue.
                                    getQueueArray()[j + 1];
                            trainQueue.getQueueArray()[j + 1] = temp;
                        }
                    }
                    if (zeroRear) {
                        rear = (rear - 1) % SEATING_CAPACITY;
                    } else {
                        front = rear = -1;
                        trainQueue.setFirst(front);
                    }
                    trainQueue.setLast(rear);
                } else {
                    System.out.println("<<<Wrong Seat Number entered.>>>");
                }
            } catch (InputMismatchException ex) {
                System.out.println("(Incorrect Input: Integer is required)");
                sc.nextLine();
            }
        }
        menu(primaryStage);
    }

    private void storeTrainQueue(Stage primaryStage)

            throws IOException, ClassNotFoundException {

        if (!trainQueue.isEmpty()) {
            File file = new File("TrainQueue.txt");
            FileOutputStream outputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream =
                    new ObjectOutputStream(outputStream);

            objectOutputStream.flush();

            for (int i = 0; i < SEATING_CAPACITY; i++) {
                if (trainQueue.getQueueArray()[i] != null) {
                    Passenger jj = trainQueue.getQueueArray()[i];
                    objectOutputStream.writeObject(jj);
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Queue Is Empty");
            alert.setContentText("No data to be saved.");
            alert.showAndWait();
        }
        menu(primaryStage);
    }

    private void loadTrainQueue(Stage primaryStage)
            throws IOException, ClassNotFoundException {

        File file = new File("TrainQueue.txt");
        FileInputStream fileInputStream = new
                FileInputStream(file);
        ObjectInputStream objectInputStream = new
                ObjectInputStream(fileInputStream);
        while (true) {
            try {

                Passenger passenger =
                        (Passenger) objectInputStream.readObject();
                trainQueue.add(passenger);
                int seatNo = passenger.getPassengerSeatNo();

                for (int i = 0; i < SEATING_CAPACITY; i++) {

                    if (waitingRoom[i] != null) {

                        int waitingRoomSeatNo =
                                waitingRoom[i].getPassengerSeatNo();

                        if (seatNo == waitingRoomSeatNo) {
                            waitingRoom[seatNo - 1] = null;
                        }
                    }
                }

            } catch (EOFException e) {
                System.out.println("Completed reading objects.");
                break;
            }
        }
        menu(primaryStage);
    }

    private void runSimulation(Stage primaryStage) {

        if (!trainQueue.isEmpty()) {
            int queueLength = 0;//Local variable to get length of the queue.

            for (int i = 0; i <= trainQueue.getLast(); i++) {

                if (trainQueue.getQueueArray()[i] != null &&
                        !simulatedPassengers.
                                contains(trainQueue.getQueueArray()[i])) {

                    queueLength++;//making the queue length.

                    Random random = new Random();
                    //Generates 3 random numbers from 1 to 6.
                    int dice1 = random.nextInt(6) + 1;
                    int dice2 = random.nextInt(6) + 1;
                    int dice3 = random.nextInt(6) + 1;
                    int seconds = dice1 + dice2 + dice3;

                    //Sets the processing time for each passenger.
                    trainQueue.getQueueArray()[i].setSecondsInQueue(seconds);

                    //Place the passenger data to another data structure
                    // to display on the report since it is removed
                    // from the circular arry
                    simulatedPassengers.add(trainQueue.getQueueArray()[i]);
                    trainQueue.remove();
                }
            }

            //Local variables
            int max = 0;
            int total = 0;

            for (int i = 0; i < simulatedPassengers.size(); i++) {
                int seconds = simulatedPassengers
                        .get(i).getSecondsInQueue();
                total = total + seconds;
                if (seconds > max) {
                    max = seconds;
                }
            }

            int min = max;

            for (int j = 0; j < simulatedPassengers.size(); j++) {
                int seconds = simulatedPassengers.get(j).
                        getSecondsInQueue();
                if (seconds < min) {
                    min = seconds;
                }
            }

            trainQueue.setAverageStayInQueue
                    (total / simulatedPassengers.size());
            trainQueue.setMaxStayInQueue(max);
            trainQueue.setMinStayInQueue(min);

            if (trainQueue.getMaxLength() < queueLength) {
                trainQueue.setMaxLength(queueLength);
            }

            TableView<Passenger> tableView = new TableView<>();

            reportTableView(tableView);

            Label report = new Label("REPORT");
            report.setStyle("-fx-font-size:38px;-fx-font-weight:bold;" +
                    "-fx-background-color:white");

            Label para1 = new Label();
            Label para2 = new Label();
            Label para3 = new Label();
            Label para4 = new Label();

            para1.setText(" Maximum Length of the Queue       : "
                    + trainQueue.getMaxLength());
            para2.setText(" Maximum Waiting Time of Passengers: "
                    + trainQueue.getMaxStayInQueue() + "s");
            para3.setText(" Minimum Waiting Time of Passengers: "
                    + trainQueue.getMinStayInQueue() + "s");
            para4.setText(" Average Waiting Time of Passengers: "
                    + trainQueue.getAverageStayInQueue() + "s");

            VBox summaryVBox = new VBox(para1, para2, para3, para4);
            summaryVBox.setPadding
                    (new Insets(10, 0, 0, 0));
            summaryVBox.setStyle("-fx-font-family: monospace;");

            VBox vBox1 = new VBox();
            vBox1.getChildren().addAll(report);
            vBox1.setPadding(new Insets(20, 20, 20, 330));
            vBox1.setStyle("-fx-background-color:white;");

            Button menuBtn = new Button("MENU");
            menuBtn.setStyle("-fx-border-color:black;-fx-font-size:15px;" +
                    "-fx-font-weight:bold;-fx-border-width:3px;" +
                    "-fx-border-radius: 5px;;-fx-background-color:white");

            menuBtn.setOnAction(event -> {
                primaryStage.close();
                try {
                    menu(primaryStage);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });

            saveReport();

            VBox vBox2 = new VBox();
            vBox2.getChildren().add(menuBtn);
            vBox2.setPadding(new Insets(10, 0, 0, 750));

            VBox mainBox = new VBox(10);
            mainBox.getChildren().addAll(vBox1, tableView, summaryVBox, vBox2);
            mainBox.setPadding(new Insets(25, 25, 25, 25));
            mainBox.setStyle("-fx-background-color:white;");

            Scene scene = new Scene(mainBox, 900, 550);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Report");
            primaryStage.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Queue Is Empty");
            alert.setContentText("No data to be saved.");
            alert.showAndWait();
            try {
                menu(primaryStage);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    private void saveReport() {
        File file = new File("Report.txt");
        PrintWriter printWriter = null;
        FileWriter fileWriter = null;
        file.delete();

        try {
            fileWriter = new FileWriter(file, true);
            printWriter = new PrintWriter(fileWriter);

            printWriter.println("                  REPORT");
            printWriter.println("---------------------------------------------");
            printWriter.println("Maximum Length of the Queue :"
                    + trainQueue.getMaxLength());
            printWriter.println("Minimum Waiting Time of Passengers:"
                    + trainQueue.getMinStayInQueue());
            printWriter.println("Maximum Waiting Time of Passengers:"
                    + trainQueue.getMaxStayInQueue());
            printWriter.println("Average Waiting Time of Passengers:"
                    + trainQueue.getAverageStayInQueue());
            printWriter.println("---------------------------------------------");
            ArrayList<Passenger> arrayList =
                    sortSimulatedPassengers(simulatedPassengers);
            for (int i = 0; i < arrayList.size(); i++) {
                String name = arrayList.get(i).getName();
                int seatNo = arrayList.get(i).getPassengerSeatNo();
                int seconds = arrayList.get(i).getSecondsInQueue();
                printWriter.println("NAME :" + name + " |SEAT NO :" + seatNo +
                        " |TIME :" + seconds + "s");
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (IOException e) {
            System.out.println("No permission given to write the file.");
        } finally {
            try {
                fileWriter.close();
                printWriter.close();
            } catch (IOException e) {
                System.out.println("Error with closing the file streams");
            }
        }

    }

    private void reportTableView(TableView<Passenger> tableView) {

        ObservableList<Passenger> boardedPassengers
                = FXCollections.observableArrayList();
        boardedPassengers.addAll
                (sortSimulatedPassengers(simulatedPassengers));

        TableColumn<Passenger, String> passengerFirstName
                = new TableColumn<>("Passenger First Name");
        passengerFirstName.setSortable(false);
        passengerFirstName.setStyle("-fx-alignment:CENTER");
        passengerFirstName.setCellValueFactory
                (new PropertyValueFactory<>("firstName"));

        TableColumn<Passenger, String> passengerSurname
                = new TableColumn<>("Passenger Surname");
        passengerSurname.setStyle("-fx-alignment:CENTER");
        passengerSurname.setSortable(false);
        passengerSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));

        TableColumn<Passenger, Integer> passengerSeatNumber =
                new TableColumn<>("Seat Number");
        passengerSeatNumber.setSortable(false);
        passengerSeatNumber.setStyle("-fx-alignment:CENTER");
        passengerSeatNumber.setCellValueFactory
                (new PropertyValueFactory<>("passengerSeatNo"));

        TableColumn<Passenger, Integer> secondsInQueue =
                new TableColumn<>("Seconds in Queue");
        secondsInQueue.setSortable(false);
        secondsInQueue.setStyle("-fx-alignment:CENTER");
        secondsInQueue.setCellValueFactory
                (new PropertyValueFactory<>("secondsInQueue"));

        tableView.setItems(boardedPassengers);
        tableView.getColumns().addAll(passengerFirstName,
                passengerSurname, passengerSeatNumber, secondsInQueue);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private ArrayList<Passenger> sortSimulatedPassengers(ArrayList<Passenger> arr) {

        for (int i = 0; i < simulatedPassengers.size() - 1; i++) {
            for (int j = 0; j < simulatedPassengers.size() - i - 1; j++) {
                if (arr.get(j).compareTo(arr.get(j + 1)) > 0) {
                    Passenger temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);
                }
            }
        }
        return arr;
    }

    private Passenger[] bubbleSort(Passenger[] arr) {
        for (int i = 0; i < SEATING_CAPACITY - 1; i++) {
            if (arr[i] != null) {
                for (int j = 0; j < trainQueue.getLast() - i; j++) {
                    if (arr[j].compareTo(arr[j + 1]) > 0) {
                        Passenger temp = arr[j];
                        arr[j] = arr[j + 1];
                        arr[j + 1] = temp;
                    }
                }
            } else {
                arr[i] = null;
            }
        }
        return arr;
    }

    private void loadBookingDetails() {

        MongoClient mongodb = new MongoClient
                ("localhost", 27017);//Creates a client.
        MongoDatabase database = mongodb.getDatabase
                ("DenuwaraManike");//Creates a database.
        MongoCollection<Document> collection = database.getCollection
                ("HashMap");//Creates a collection.

        LocalDate presentDate = LocalDate.now();
        String date = presentDate.getYear() + "-" +
                presentDate.getMonth() + "-" + presentDate.getDayOfMonth();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CONFIRMATION");
        alert.setHeaderText("Denuwara Manike!");
        alert.setContentText("Choose your route.");

        ButtonType badullaBtn =
                new ButtonType("Colombo to Badulla.");
        ButtonType colomboBtn =
                new ButtonType("Badulla to Colombo.");
        ButtonType cancel =
                new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(badullaBtn, colomboBtn, cancel);

        String keyToCompareId = "";

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == badullaBtn) {
            keyToCompareId = date + "-" + "ToBadulla";
        } else if (result.get() == colomboBtn) {
            keyToCompareId = date + "-" + "ToColombo";
        } else {
            System.out.println("Route Not Selected.");
        }

        FindIterable<Document> data = collection.find();
        for (Document record : data) {
            for (String id : record.keySet()) {
                if (id.equals(keyToCompareId)) {
                    Object arrayObject = record.get(id);
                    bookedPassengers = (ArrayList) arrayObject;
                }
            }
        }

        System.out.println("Loaded Booking Data successfully.\n");
    }

    private void addingPassengersToWaitingRoom(Stage window) {

        ListView<String> listBooking = new ListView<>();

        for (int i = 0; i < bookedPassengers.size(); i++) {
            if (bookedPassengers.get(i) != null) {
                int seatNumber = i + 1;
                String info = seatNumber + "-" +
                        bookedPassengers.get(i);
                listBooking.getItems().addAll(info);
            }
        }

        listBooking.getSelectionModel().
                setSelectionMode(SelectionMode.MULTIPLE);

        listBooking.setOnMouseClicked(event -> {
            int selectedIndex = listBooking.getSelectionModel().getSelectedIndex();
            String details = listBooking.getSelectionModel().getSelectedItem();
            String[] splitDetails = details.split("-");
            int seatNo = Integer.parseInt(splitDetails[0]);
            String name = splitDetails[1].substring(0, 1)
                    .toUpperCase() + splitDetails[1].substring(1);
            String surname = splitDetails[2].substring(0, 1)
                    .toUpperCase() + splitDetails[2].substring(1);

            waitingRoom[seatNo - 1] = new Passenger();
            waitingRoom[seatNo - 1].setName(name, surname);
            waitingRoom[seatNo - 1].setPassengerSeatNo(seatNo);
            listBooking.getItems().remove(selectedIndex);
        });


        Label title = new Label("List of Bookings.");
        Button nextBtn = new Button("NEXT");


        title.setStyle("-fx-font-size:38px;" +
                "-fx-font-weight:bold;-fx-background-color:white");
        nextBtn.setStyle("-fx-border-color:black;" +
                "-fx-font-size:15px;-fx-font-weight:bold;" +
                "-fx-border-width:3px;" +
                "-fx-border-radius: 5px;;-fx-background-color:white");

        nextBtn.setOnAction(event -> {
            window.close();
            try {
                menu(window);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        Label randomLabel = new Label("");

        VBox vBox2 = new VBox(10);
        vBox2.getChildren().addAll(nextBtn);
        vBox2.setPadding(new Insets(0, 0, 0, 550));

        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(title, listBooking, randomLabel, vBox2);
        vBox.setPadding(new Insets(30, 30, 30, 30));
        vBox.setStyle("-fx-background-color:white;");

        Scene scene = new Scene(vBox, 700, 600);
        window.setScene(scene);
        window.setTitle("Bookings");
        window.show();
    }
}
