import distanceMatrix.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

public class Main {
    public static void main(String[] args) {

        String mtr = "/home/porcaontas/IdeaProjects/TP2/src/Distancias.txt";
        DistanceMatrix dist = new DistanceMatrix(mtr);
        ArrayList<String> c = new ArrayList<String>( dist.getCities() );

        SimAnnealing s = new SimAnnealing( mtr,c);
        ArrayList<String> sol = s.solve();

        System.out.println( s.get_cost() );
        for( String s1: sol){ System.out.println(s1); }
    }
}

class SimAnnealing {

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
        return n_iter < 100;
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

        //Get 2 values
        int n1 = (int) ( Math.random() * (current_sol.seq.size() - 1) ) ,
            n2 = (int) ( Math.random() * (current_sol.seq.size() - 1)) ;

        //calc cost

        int cost = current_sol.swap_cost( n1,n2 );

        if( cost < 0 ){
            //better
            current_sol.swap( n1 , n2 );

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
            current_sol.swap( n1 , n2 );
            current_sol.cost = cost;
        }
    }

    private double get_prob( int d ){
        return Math.exp(
                -( (double) d / tmp )
        );
    }

    private int init_temp(){
        return 1500;
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

        public int swap_cost( int n1, int n2 ){

            int ax = n2 - n1;
            if( ax == 0 || ax == 1 ){
                return 0;
            }
            else if( ax < 0 ){
                int i = n1;
                n1 = n2;
                n2 = i;
            }
            //n1;

            int m = n2 == seq.size() - 2 ? 0 : n2 + 1;
            System.out.println(n1+" "+m);
            return
                    ( dm.distance( seq.get(n1),seq.get(n2) )
                            + dm.distance( seq.get(n1+1),seq.get(m) ))
                    -
                    ( dm.distance( seq.get(n1),seq.get(n1+1) )
                            + dm.distance( seq.get(n2),seq.get(m) ));
        }

        public void swap( int n1,int n2 ){

            if( n1 > n2 ){
                int i = n1;
                n1 = n2;
                n2 = i;
            }

            for(; n1 < n2 ;++n1,--n2 ){
                swap_seq( n1,n2 );
            }

        }

        private void swap_seq( int n1, int n2 ){

            if( n1 == n2 ){ return; }

            String s = seq.get(n1);
            seq.set(
                    n1 ,
                    seq.get(n2-1));

            seq.set( n2, s );
        }
    }
}