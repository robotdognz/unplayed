package game.player;

import java.util.ArrayList;

public class PlayerVelocityHistory<K> extends ArrayList<K> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int maxSize;

    public PlayerVelocityHistory(int size){
        this.maxSize = size;
    }

    public boolean add(K k){
        boolean r = super.add(k);
        if (size() > maxSize){
            removeRange(0, size() - maxSize);
        }
        return r;
    }

    public K getYoungest() {
        return get(size() - 1);
    }

    public K getOldest() {
        return get(0);
    }
    
//    public K getLargest() {
//    	
//    	
//    	for() {
//    		
//    	}
//    	
//    	return
//    }
}