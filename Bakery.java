import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Bakery {
    private BakeryParam bp;
    private BakeryEvent be;

    private Counter counter; //main monitor class

    Bakery(BakeryParam bp) {
        this.bp = bp;
        this.be = new BakeryEvent(bp);
        counter = new Counter(bp.getNC());
    }

    /** Open the doors of the bakery! */
    void open() {
        for (int i = 0; i < bp.getNS(); i++) {
            new Server(i, counter).start();
        }
        for (int i = 0; i < bp.getNC(); i++) {
            new Customer(i, counter).start();
        }
    }

    public static void main(String[] args) {
        //open a new bakery
        new Bakery(new BakeryParam(args)).open();
    }

    class Customer extends Thread {
        private int id;
        private Counter ct;

        Customer(int cid, Counter ct) {
            super();
            id = cid;
            this.ct = ct;
        }
        
        public void run() {
            try {
                while (true) {
                    int ticketNumber = ct.dispense(id); //take a ticket
                    be.sleepEvents();                   //wait for a bit
                    //order for some buns
                    ct.placeOrder(ticketNumber, bp.getRandVal(bp.getNB()) + 1);
                }
            } catch (InterruptedException e) {
            }
        }
    }

    class Server extends Thread {
        private int id;
        private Counter ct;

        Server (int sid, Counter ct) {
            super();
            id = sid;
            this.ct = ct;
        }

        public void run() {
            try {
                while (true) {
                    int ticketNum = ct.callNext(id); //call next customer
                    be.sleepEvents();                //wait for a bit
                    ct.giveBuns(ticketNum);          //give them buns
                }
            } catch (InterruptedException e) {
            }
        }
    }

    class Order {
        private int cid;
        private int sid;
        private int buns;

        private boolean hasServer = false;
        private boolean hasBuns = false;

        Order(int cid) {
            this.cid = cid;
        }

        void setSid(int sid) {
            this.sid = sid;
            hasServer= true;
        }

        void setBuns(int buns) {
            this.buns = buns;
            hasBuns = true;
        }

        int getSid() {
            return sid;
        }

        int getCid() {
            return cid;
        }

        int getBuns() {
            return buns;
        }

        boolean ready() {
            return hasServer && hasBuns;
        }
    }

    class Counter {
        private int value = 0;
        private int totalTickets;   //number of tickets to take
        private int max;            //largest ticket number
        private int currNum;        //the number to be taken
        
        //ticketNo, customerNo, ServerNo, BunNo
        private Queue<Integer> callQueue;
        private HashMap<Integer,Order> orders; //ticketNo, order

        Counter(int tickets) {
            totalTickets = tickets; 
            max = tickets - 1;
            callQueue = new LinkedList<Integer>();

            currNum = 0; //first num to be taked
            orders = new HashMap<Integer,Order>();
        }

        private int nextNum() {
            return (currNum + 1) % totalTickets;
        }

        /* give out a ticket and increment */
        synchronized int dispense(int cid)
                throws InterruptedException {
            // wait if the next ticket is already in use
            while (orders.keySet().contains(nextNum())) wait();
            int ticketNum = currNum;
            orders.put(ticketNum, new Order(cid));
            callQueue.add(ticketNum);  //call queue
            be.logTake(cid, ticketNum);
            currNum = nextNum();
            notifyAll();
            return ticketNum;
        }

        /* calls the first ticket in callQueue */
        synchronized int callNext(int sid)
                throws InterruptedException {
            while (callQueue.isEmpty()) wait();
            int callTicket = callQueue.poll();
            be.logCall(sid, callTicket);
            Order o = orders.get(callTicket);
            o.setSid(sid);
            orders.put(callTicket, o);
            notifyAll();
            return callTicket;
        }

        /* puts an order for buns */
        synchronized void placeOrder(int ticketNo, int buns)
                throws InterruptedException {
            while (callQueue.contains(ticketNo)) wait();
            Order o = orders.get(ticketNo);
            be.logPay(o.getCid(), o.getSid(), ticketNo, buns);
            o.setBuns(buns);
            orders.put(ticketNo, o);
            notifyAll();
        }

        /* give buns to completed order */
        synchronized void giveBuns(int ticketNo)
                throws InterruptedException {
            //wait if the order isn't ready yet
            while (!orders.get(ticketNo).ready()) wait();
            Order o = orders.get(ticketNo);
            be.logBun(o.getCid(), o.getSid(), o.getBuns());
            orders.remove(ticketNo);
            notifyAll();
        }

    }
}
