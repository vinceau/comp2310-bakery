public class Bakery {

    private BakeryParam bp;
    private BakeryEvent be;

    public Bakery(BakeryParam bp) {
        this.bp = bp;
        this.be = new BakeryEvent(bp);
        be.logOtherEvent(bp.useCook() + " " + bp.fairCook() + " "
                + bp.getNC() + " " + bp.getNS() + " " + bp.getNB());
    }

    public static void main(String[] args) {
        Bakery b = new Bakery(new BakeryParam(args));
    }

}
