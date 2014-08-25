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

    /* Open the doors of the bakery! */
    void open() {
        for (int i = 0; i < bp.getNS(); i++) {
            new Server(i, counter).start();
        }
        for (int i = 0; i < bp.getNC(); i++) {
            new Customer(i, counter).start();
        }
    }

    public static void main(String[] args) {
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
                    be.sleepEvents();                   //wait a bit
                    //order for some buns
                    ct.placeOrder(id, ticketNumber,
                            bp.getRandVal(bp.getNB()) + 1);
                    be.sleepEvents();                   //wait a bit
                }
            } catch (InterruptedException e) {
            }
        }
    }

    class Server extends Thread {
        private int id;
        private Counter ct;
        private int buns;

        Server (int sid, Counter ct) {
            super();
            id = sid;
            this.ct = ct;
            buns = bp.getNB();
        }

        public void run() {
            try {
                while (true) {
                    int ticketNum = ct.callNext(id); //call next customer
                    be.sleepEvents();                //wait a bit
                    //check buns remaining
                    buns = ct.checkBuns(ticketNum, buns);
                    be.sleepEvents();                //wait a bit
                    ct.giveBuns(ticketNum);          //give them buns
                    be.sleepEvents();                //wait a bit
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
        private int totalTickets;   //number of tickets to be taken
        private int currNum;        //the current number to be taken
        private Queue<Integer> callQueue;
        private HashMap<Integer,Order> orders; //ticketNo -> order
        private LinkedList<Integer> customersList; //who has a ticket

        Counter(int tickets) {
            totalTickets = tickets; 
            currNum = 0; //first num to be taken
            callQueue = new LinkedList<Integer>();
            orders = new HashMap<Integer,Order>();
            customersList = new LinkedList<Integer>();
        }

        private synchronized int nextNum() {
            return (currNum + 1) % totalTickets;
        }

        /* give out a ticket and increment */
        synchronized int dispense(int cid)
                throws InterruptedException {
            // wait if the next ticket is already in use
            while (customersList.contains(cid) ||
                    orders.keySet().contains(nextNum())) wait();
            int ticketNum = currNum;
            orders.put(ticketNum, new Order(cid));
            callQueue.add(ticketNum);  //add number to call queue
            be.logTake(cid, ticketNum);
            currNum = nextNum(); //update the ticket number
            customersList.add(cid); //ensure they don't take another ticket
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
        synchronized void placeOrder(int cid, int ticketNo, int buns)
                throws InterruptedException {
            while (callQueue.contains(ticketNo)) wait();
            Order o = orders.get(ticketNo);
            assert cid == o.getCid(); //ensure same customer
            be.logPay(o.getCid(), o.getSid(), ticketNo, buns);
            o.setBuns(buns);
            orders.put(ticketNo, o);
            notifyAll();
        }

        /* give buns to completed order */
        synchronized void giveBuns(int ticketNo)
                throws InterruptedException {
            //wait if the order doesn't exist or isn't ready yet
            while (!orders.containsKey(ticketNo) ||
                    !orders.get(ticketNo).ready()) wait();
            Order o = orders.get(ticketNo);
            be.logBun(o.getCid(), o.getSid(), o.getBuns());
            customersList.remove(Integer.valueOf(o.getCid())); //object
            orders.remove(ticketNo);
            notifyAll();
        }

        /* check to see if server has enough buns */
        synchronized int checkBuns(int ticketNo, int buns)
                throws InterruptedException {
            //wait if the order doesn't exist or isn't ready yet
            while (!orders.containsKey(ticketNo) ||
                    !orders.get(ticketNo).ready()) wait();
            Order o = orders.get(ticketNo);
            if (buns < o.getBuns()) {
                be.logTopup(o.getSid());
                return bp.getNB() - o.getBuns();
            }
            return buns - o.getBuns();
        }
    }
}
