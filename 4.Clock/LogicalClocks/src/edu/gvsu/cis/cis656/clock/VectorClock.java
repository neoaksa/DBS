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
            int valueOther = other.getTime(Integer.parseInt(pid));
            // if current clock less than input, replace with greater one
            if(valueCur<valueOther) {
                clock.put(pid,valueOther);
            }
        }
        // add new key if the pid is not exist
        for(String opid : other.getMap().keySet()){
            this.addProcess(Integer.parseInt(opid),other.getTime(Integer.parseInt(opid)));
        }
    }

    @Override
    public void setClock(Clock other) {
        this.clock = other.getMap();
    }

    @Override
    public void tick(Integer pid) {
        if(pid!=null)
            clock.put(Integer.toString(pid),clock.get(pid)+1);
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
        StringBuilder output = new StringBuilder();
        // see the structure in VectorClockTests
        output.append("{");
        for(String pid : clock.keySet()){
            output.append("\""+pid+"\":");
            output.append(clock.get(pid));
            output.append(",");
        }
        //cut the last comma
        output.deleteCharAt(output.length()-1);
        output.append("}");
        return output.toString();
    }

    @Override
    public void setClockFromString(String clock) {
        try {
            String inputStr = clock.substring(1, clock.length() - 1);

            if (inputStr.equals("")) {
                this.clock.clear();
                return;
            }

            String[] spArray = inputStr.split(",");

            Map<String, Integer> newClock = new Hashtable<String, Integer>();

            for (String sp : spArray) {
                String[] s = sp.split(":");
                newClock.put(s[0], Integer.parseInt(s[1]));
                newClock.put(s[0].replace("\"", ""), Integer.parseInt(s[1]));
            }

            this.clock = newClock;
        } catch (Exception e) {
            // swallow
        }
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
