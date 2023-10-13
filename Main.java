import DistanceMatrix.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;


public class Main {

    public static void main(String[] args) {

        String mtr = "/Users/user/IdeaProjects/tp2/src/Distancias.txt";
        DistanceMatrix dist = new DistanceMatrix(mtr);
        ArrayList<String> c = new ArrayList<String>( dist.getCities() );

        SimAnnealing s = new SimAnnealing( mtr,c);
        ArrayList<String> sol = s.solve();

        System.out.println( s.get_cost() );
        for( String s1: sol){ System.out.println(s1); }
    }
}

class SimAnnealing {

    public static final int init_temp = 2000;
    public static final int max_iter = 100;
    private int tmp;
    private int n_iter;
    private DistanceMatrix dm;
    private solution current_sol;
    private solution best_sol;


    public SimAnnealing( String filepath,ArrayList<String> cities ){

        dm  = new DistanceMatrix(filepath);
        ArrayList<String> dc = dm.getCities();

        for( String s1: cities ){
            if( !dc.contains( s1 )){
                dm = null;
                return ;
            }
        }

        tmp = init_temp();
        dc = create_init_sol( cities );
        current_sol =
                new solution(
                        dc, dm
                );
        current_sol.calc_cost();
        best_sol = current_sol.cpy();
    }

    public int get_cost(){
        return best_sol.cost;
    }

    public ArrayList<String> solve(){

        n_iter = 0;

        while( stop() ){
            iterate();
            decay();
            ++n_iter;
        }
        return best_sol.seq;
    }

    private boolean stop(){
        return n_iter < max_iter;
    }

    private ArrayList<String> create_init_sol(ArrayList<String> cities){

        int n;
        String s;

        ArrayList<String> fsol = new ArrayList<String>(cities);

        for( int i = 0;i < ( cities.size()/2 );++i ){
            n = (int) ( Math.random() * cities.size() );
            Collections.swap( fsol,i,n );
        }

        return fsol;
    }

    private void iterate(){

        int ax = 0,
            i  = 0,
            j  = 0;
        //Get 2 values

        while( ax == 0 || Math.abs(ax) == 1 ) {

            i = (int) (Math.random() * (current_sol.seq.size() - 1));
            j = (int) (Math.random() * (current_sol.seq.size() - 1));

            ax = j - i ;//used to compare i and j
        }

        if (ax < 0) {
            int a = j;
            j = i;
            i = a;
        }
        //calc cost

        int cost = current_sol.swap_cost( i,j );

        if( cost < 0 ){
            //better
            current_sol.swap( i, j, cost );

            if( cost < best_sol.cost ){
                //new best ?
                best_sol = current_sol;
            }
            return;
        }

        double  prob = get_prob( cost ),
                rand = Math.random();

        if( rand < prob ){
            //accept
            current_sol.swap( i, j, cost );
        }
    }

    private double get_prob( int d ){
        return Math.exp(
                -( (double) d / tmp )
        );
    }

    private int init_temp(){
        return init_temp;
    }

    private int decay(){
        return tmp *= 0.9;
    }

    private class solution{

        public ArrayList<String> seq;
        private final DistanceMatrix dm;
        public int cost;

        public solution(ArrayList<String> seq, DistanceMatrix d){

            this.seq = new ArrayList<String>( seq );
            dm = d;
        }

        public solution cpy(){

            solution s = new solution( seq,dm );
            s.cost = cost;

            return s;
        }
        public void calc_cost(){

            cost = 0;
            String s1,s2;
            Iterator<String> it = dm.getCities().iterator();

            s1 = it.next();
            s2 = it.next();

            while(it.hasNext()){
                cost += dm.distance( s1,s2 );
                s1 = s2;
                s2 = it.next();
            }
        }
        public int swap_cost( int i, int j ) {

            int j1 = j == ( seq.size() - 1 ) ? 0 : j + 1;

            return
                    ( dm.distance( seq.get( i ), seq.get( j ) ) +
                            dm.distance( seq.get( i + 1 ), seq.get( j1 )))
                   - ( dm.distance( seq.get( i ), seq.get( i + 1 ) ) +
                            dm.distance( seq.get( j ), seq.get( j1 ) ));
        }

        public void swap( int i,int j,int c ) {

            //System.out.println( "inicial " +cost+" c "+c);
            cost += c;
            //System.out.println( "new "+cost);
            ++i;
            for( ; i < j; ++i, --j ){
                swap_seq(i,j);
            }

        }

        private void swap_seq( int i, int j ) {

            String s = seq.get(i);
            seq.set(
                    i ,
                    seq.get(j));

            seq.set( j, s );
        }
    }
}
