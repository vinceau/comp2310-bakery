public class Bakery {

    public static void main(String[] args) {
        BakeryParam bp = new BakeryParam(args);
        System.out.println(bp.useCook() + " " + bp.fairCook() + " "
                + bp.getNC() + " " + bp.getNS() + " " + bp.getNB());
    }

}
