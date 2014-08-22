public class Bakery {

    private BakeryParam bp;
    private BakeryEvent be;

    private Tickets ticketDispenser;

    public Bakery(BakeryParam bp) {
        this.bp = bp;
        this.be = new BakeryEvent(bp);
        be.logOtherEvent(bp.useCook() + " " + bp.fairCook() + " "
                + bp.getNC() + " " + bp.getNS() + " " + bp.getNB());
        ticketDispenser = new Tickets(bp.getNC());
        for (int i = 0; i < 20; i++) {
            System.out.println("number: " + ticketDispenser.dispense());
        }
    }

    public static void main(String[] args) {
        Bakery b = new Bakery(new BakeryParam(args));
    }

    class Customer extends Thread {
        private int id;
        private TicketDispenser td;
        private Ticket ticket;

        public Customer(int cid, TicketDispenser td) {
            super();
            id = cid;
            this.td = td;
        }
        
        public void take() {
            this.ticket = td.dispense(id);
        }
    }

    class Server extends Thread {
        private int id;

        public Server (int sid, TicketDispenser td) {
            super();
            id = sid;
            this.td = td;
        }
    }

    class TicketDispenser {
        private int value = 0;
        private int max;

        public TicketDispenser(int max) {
            this.max = max;
        }

        /** give out a ticket and increment */
        public Ticket dispense(int cid) {
            int n = nextNum();
            be.logTake(cid, n);
            return new Ticket(n);
        }

        private synchronized int nextNum() {
            return value++;
        }

        private synchronized int nextCall() {
            
        }
    }

    class Ticket {
        int num;
        int valid;

        Ticket(int n) {
            this.num = n;
            valid = true;
        }

        public int getNum() {
            return num;
        }

        public boolean isValid() {
            return valid;
        }

        public void invalidate() {
            valid = false;
        }
    }

}
