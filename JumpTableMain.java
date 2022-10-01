/* Ethan Hebert
 * CSC-325-001
 * 10-1-22
 * Assignment 2 - Jump Tables
 * Implements stack, queue, and list data structures with displays
 */

import java.util.*;
import java.io.*;

/* Enter and Exit return nothing */
interface StateEnterExitMeth {
    public void invoke();
}

/* Stay returns boolean for possibility of quitting application */
interface StateStayMeth {
    public boolean invoke();
}

/* Var that contains all possible states */
enum State {
    IDLE,
    STACK,
    QUEUE,
    LIST
}



class Screen {
    /* Declare class variables and dictionaries */
    private State state;
    private boolean keepRunning;
    private char[] input = {' ', ' '};
    private Scanner scanner = new Scanner(System.in);

    private HashMap<State, StateEnterExitMeth> stateEnterMeths;
    private HashMap<State, StateStayMeth> stateStayMeths;
    private HashMap<State, StateEnterExitMeth> stateExitMeths;

    static final String STACKFILENAME = "stack.txt";
    static final String QUEUEFILENAME = "queue.txt";
    static final String LISTFILENAME = "list.txt";

    private String stackData = "";
    private String queueData = "";
    private String listData = "";

    private Stack<Character> stack;
    private Queue<Character> queue;
    private ArrayList<Character> list;


    /* Constructor */
    public Screen() {
        /* Create dictionaries */
        stateEnterMeths = new HashMap<>();
        stateStayMeths = new HashMap<>();
        stateExitMeths = new HashMap<>();

        /* Map each state to its corresping function for jump table */
        stateEnterMeths.put(State.IDLE, () -> { StateEnterIdle(); });
        stateEnterMeths.put(State.STACK, () -> { StateEnterStack(); });
        stateEnterMeths.put(State.QUEUE, () -> { StateEnterQueue(); });
        stateEnterMeths.put(State.LIST, () -> { StateEnterList(); });
        
        stateStayMeths.put(State.IDLE, () -> { keepRunning = StateStayIdle(); return keepRunning; });
        stateStayMeths.put(State.STACK, () -> { keepRunning = StateStayStack(); return keepRunning; });
        stateStayMeths.put(State.QUEUE, () -> { keepRunning = StateStayQueue(); return keepRunning; });
        stateStayMeths.put(State.LIST, () -> { keepRunning = StateStayList(); return keepRunning; });

        stateExitMeths.put(State.IDLE, () -> { StateExitIdle(); });
        stateExitMeths.put(State.STACK, () -> { StateExitStack(); });
        stateExitMeths.put(State.QUEUE, () -> { StateExitQueue(); });
        stateExitMeths.put(State.LIST, () -> { StateExitList(); });

        /* Create stack, queue, list */
        stack = new Stack<Character>();
        list = new ArrayList<Character>();
        queue = new LinkedList<Character>();

        /* Declare initial values */
        state = State.IDLE;
        keepRunning = true;
    }



    /* State Methods */
    public void changeState(State newState) {
        /* Switch to new state if not already in it */
        if (state != newState) {
            // call the exit method of the previous state, switch to new state
            stateExitMeths.get(state).invoke();
            state = newState;
            /* First call this new state's enter method */
            if (stateEnterMeths.containsKey(newState)) {
                stateEnterMeths.get(newState).invoke();
            }
        }
    }

    public boolean doState() {
        /* keepRunning will turn false if the user decides to quit */
        keepRunning = false;
        if (stateStayMeths.containsKey(state)) {
            keepRunning = stateStayMeths.get(state).invoke();
        }
        // run the exit state to save file before the program quits
        if (keepRunning == false) {
            stateExitMeths.get(state).invoke();
        }
        return keepRunning;
    }



    /// ENTER
    private void StateEnterIdle() { /* do nothing */ }

    private void StateEnterStack() {
        stackData = readFile();
        // push every element from the file into the stack
        for (int i=0; i<stackData.length(); i++) {
            if (i % 2 == 0)
                stack.push(stackData.charAt(i));
        }
    }

    private void StateEnterQueue() {
        queueData = readFile();
        // enqueue every element from the file into the queue
        for (int i=0; i<queueData.length(); i++) {
            if (i % 2 == 0)
                queue.add(queueData.charAt(i));
        }
    }
    
    private void StateEnterList() {
        listData = readFile();
        // append every element from the file into the list
        for (int i=0; i<listData.length(); i++) {
            if (i % 2 == 0)
                list.add(listData.charAt(i));
        }
    }



    /// STAY
    private boolean StateStayIdle() {
        /* clear screen, print screen, read input */
        clearScreen();
        // menus in yellow
        System.out.print("\u001b[33m1. Stack\n2. Queue\n3. List\n4. Quit\n?  \u001b[0m");
        getInput();

        // Check to change state
        if (input[0] == '1')
            changeState(State.STACK);

        else if (input[0] == '2')
            changeState(State.QUEUE);

        else if (input[0] == '3')
            changeState(State.LIST);
        
        /* check if user quits */
        else if (input[0] == '4')
            return false;
        return true;
    }

    private boolean StateStayStack() {
        /* clear screen, print screen, read input */
        clearScreen();
        drawStack();
        //Bonus.check(stack);
        getInput();
        
        // push - only push if there's an element to be pushed
        if (input[0] == '1' && input[1] != '\u0000')
            stack.push(input[1]);
            
        //pop
        else if (input[0] == '2') {
            // can't pop if the stack is empty
            try {
                stack.pop();
            } catch (EmptyStackException e) {}
        }

        //save and move to queue
        else if (input[0] == '3') {
            changeState(State.QUEUE);
        }

        //save and move to list
        else if (input[0] == '4') {
            changeState(State.LIST);
        }
            
        /* check if user quits */
        else if (input[0] == '5')
            return false;

        return true;
    }

    private boolean StateStayQueue() {
        /* clear screen, print screen, read input */
        clearScreen();
        drawQueue();
        //Bonus.check(queue);
        getInput();

        // enqueue - only enqueue if there's an element to be enqueued
        if (input[0] == '1' && input[1] != '\u0000')
            queue.add(input[1]);
            
        //dequeue
        else if (input[0] == '2') {
            // can't dequeue if the queue is empty
            try {
                queue.remove();
            } catch (NoSuchElementException e) {}
        }

        //save and move to stack
        else if (input[0] == '3') {
            changeState(State.STACK);
        }

        //save and move to list
        else if (input[0] == '4') {
            changeState(State.LIST);
        }

        /* check if user quits */
        else if (input[0] == '5')
            return false;

        return true;
    }

    private boolean StateStayList() {
        /* clear screen, print screen, read input */
        clearScreen();
        drawList();
        //Bonus.check(list);
        getInput();

        // append - only append if there's an element to be appended
        if (input[0] == '1' && input[1] != '\u0000')
            list.add(input[1]);
            
        //remove
        else if (input[0] == '2') {
            // can't remove if the list is empty
            try {
                list.remove(list.size() - 1);
            } catch (IndexOutOfBoundsException e) {}
        }

        //save and move to stack
        else if (input[0] == '3') {
            changeState(State.STACK);
        }

        //save and move to queue
        else if (input[0] == '4') {
            changeState(State.QUEUE);
        }

        /* check if user quits */
        else if (input[0] == '5')
            return false;

        return true;
    }



    /// EXIT
    private void StateExitIdle() { /* do nothing */ }

    private void StateExitStack() {
        //store the stack data into a String separated by commas to write to file
        stackData = "";
        for (Character curr: stack) {
            stackData += curr + ",";
        }
        //clear the whole stack
        stack.clear();

        //write the stackData to the stack file
        writeFile(stackData);
    }

    private void StateExitQueue() {
        //store the queue data into a String separated by commas to write to file
        queueData = "";
        for (Character curr: queue) {
            queueData += curr + ",";
        }
        //clear the whole queue
        queue.clear();

        //write the queueData to the queue file
        writeFile(queueData);
    }

    private void StateExitList() {
        //store the list data into a String separated by commas to write to file
        listData = "";
        for (Character curr: list) {
            listData += curr + ",";
        }
        //clear the whole list
        list.clear();

        //write the listData to the list file
        writeFile(listData);
    }



    /* Extra Methods */
    private void clearScreen() {  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }

    private void getInput() {
        String line = scanner.nextLine();
        /* user inputs char _space_ char, read the 2 chars */
        if (line.length() >= 3) {
            input[0] = line.charAt(0);
            input[1] = line.charAt(2);
        }

        /* user input one char, read that char and reset second char value to blank */
        else if (line.length() >= 1) {
            input[0] = line.charAt(0);
            input[1] = '\u0000';
        }
        
        /* no input, read nothing and reset input values */
        else {
            input[0] = '\u0000';
            input[1] = '\u0000';
        }
    }

    private String readFile() {
        String fileData = "";

        //try to find and read the file, if not found print error
        try {
            File newFile = new File(getFileName());
            Scanner fileReader = new Scanner(newFile);
            while (fileReader.hasNextLine())
                fileData += fileReader.nextLine();
            fileReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occured reading the file: " + getFileName());
            e.printStackTrace();
        }
        return fileData;
    }

    private void writeFile(String fileData) {
        //try to find and write the file, if not found print error
        try {
            FileWriter fileWriter = new FileWriter(getFileName());
            fileWriter.write(fileData);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("An error occured writing to file: " + getFileName());
            e.printStackTrace();
        }
    }

    private String getFileName() {
        String filename = "";

        if (state == State.STACK) {
            filename = STACKFILENAME;   
        }
        else if (state == State.QUEUE) {
            filename = QUEUEFILENAME;
        }
        else if (state == State.LIST) {
            filename = LISTFILENAME;
        }

        return filename;
    }

    private void drawStack() {
        //formatted vertically
        String output = "";
        for (Character curr: stack) {
           output = ("\n| " + curr + " |\n|---|" + output);
        }
        output = "|   |\n|---|" + output;
        // stack in red, menus in yellow
        System.out.println("\u001b[31m" + output + "\u001b[0m");
        System.out.print("\u001b[33m1. Push\n2. Pop\n3. Save & Move to Queue\n4. Save & Move to List\n5. Quit\n?  \u001b[0m");

    }

    private void drawQueue() {
        //formatted horizontally with | dividers
        String output = "|";
        for (Character curr: queue) {
           output += " " + curr + " |";
        }
        //queue in green, menus in yellow
        System.out.println("\u001b[32m" + output + "\u001b[0m");
        System.out.print("\u001b[33m1. Enqueue\n2. Dequeue\n3. Save & Move to Stack\n4. Save & Move to List\n5. Quit\n?  \u001b[0m");

    }

    private void drawList() {
        //formatted horizontally with commas and {}
        String output = "{ ";
        for (Character curr: list) {
           output += curr + ", ";
        }
        output += " }";
        //list in blue, menus in yellow
        System.out.println("\u001b[34m" + output + "\u001b[0m");
        System.out.print("\u001b[33m1. Append\n2. Remove\n3. Save & Move to Stack\n4. Save & Move to Queue\n5. Quit\n?  \u001b[0m");

    }
}



public class JumpTableMain {
    public static void main(String[] args) {
        Screen screen = new Screen();
        boolean keepRunning = true;
        //if the user selects to quit, end program
        while (keepRunning) {
            keepRunning = screen.doState();
        }
    }
}