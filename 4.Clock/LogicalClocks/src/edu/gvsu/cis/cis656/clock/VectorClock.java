package edu.gvsu.cis.cis656.clock;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class VectorClock implements Clock {

    // suggested data structure ...
    private Map<String,Integer> clock = new Hashtable<String,Integer>();


    @Override
    public void update(Clock other) {
        Map<String,Integer> otherClock = other.getMap();
        for (String otKey : otherClock.keySet()) {
            Integer otTime = otherClock.get(otKey);
            if(clock.containsKey(otKey)) {
                int max = Math.max(clock.get(otKey), otTime);
                clock.put(otKey, max);
            } else {
                clock.put(otKey, otTime);
            }
        }
    }

    @Override
    public void setClock(Clock other) {
        this.clock = other.getMap();
    }

    @Override
    public void tick(Integer pid) {
        clock.put(Integer.toString(pid),clock.get(pid.toString())+1);
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
        // sort by key
        Map<String, Integer> sortedClock = this.clock.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        StringBuilder output = new StringBuilder("{");
        // see the structure in VectorClockTests
        for(String pid : sortedClock.keySet()){
            output.append("\""+pid+"\":");
            output.append(sortedClock.get(pid));
            output.append(",");
        }
        //cut the last comma
        if(output.length()>1)
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
