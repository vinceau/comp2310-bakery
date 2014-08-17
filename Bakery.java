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

        public Customer() {
            super();
        }
        
        public void take() {
            

        }
    }

    class Tickets {
        
        private int value = 0;
        private int max;

        public Tickets(int max) {
            this.max = max;
        }

        /** give out a ticket and increment */
        synchronized public int dispense() {
            return (value++ % max);
        }
    }

}
