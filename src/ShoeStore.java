public class ShoeStore {



    public void shoestoe(){
        System.out.println("Välkommen till den bästa onlinebutiken");
    }

    public static void main(String[] args) {
        Repository r= new Repository();
        r.getShoeDetailsByCategory();
    }
}
