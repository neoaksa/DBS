package edu.gvsu.cis.cis656.clock;

import java.util.Hashtable;
import java.util.Map;

public class VectorClock implements Clock {

    // suggested data structure ...
    private Map<String,Integer> clock = new Hashtable<String,Integer>();


    @Override
    public void update(Clock other) {
        //update existing key
        for(String pid : clock.keySet()){
            int valueCur = clock.get(pid);
            int valueOhter = other.getTime(Integer.parseInt(pid));
            if(valueCur<valueOhter) {
                clock.put(pid,valueOhter);
            }
        }
        // add new key
        for(String opid : other.getMap().keySet()){
            this.addProcess(Integer.parseInt(opid),other.getTime(Integer.parseInt(opid)));
        }
    }

    @Override
    public void setClock(Clock other) {
        clock = other.getKeys();
    }

    @Override
    public void tick(Integer pid) {
        if(pid!=null)
            clock.put(Integer.toString(pid),clock.get(pid)+1);
        else
            pass;
    }

    @Override
    public boolean happenedBefore(Clock other) {
        boolean signal = true;
        for(String pid : clock.keySet()){
            int valueCur = clock.get(pid);
            int valueOhter = other.getTime(Integer.parseInt(pid));
            if(valueCur>valueOhter) {
                signal = false;
            }
        }
        return signal;
    }

    public String toString() {
        StringBuilder output;
        output.append("[")
        for(String pid : clock.keySet()){
            output.append(clock.get(pid));
            output.append(" ");
        }
        output.append("]");
        return output.toString();
    }

    @Override
    public void setClockFromString(String clock) {

    }

    @Override
    public int getTime(int p) {
        return clock.get(Integer.toString(p));
    }

    @Override
    public void addProcess(int p, int c) {
        clock.put(Integer.toString(p), c);
    }

    @Override
    public Map<String,Integer> getMap(){
        return clock;

    }
}
