/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.clickrace.services;

import edu.eci.arsw.clickrace.model.RaceParticipant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import org.springframework.stereotype.Service;

/**
 *
 * @author hcadavid
 */
@Service
public class ClickRaceServicesStub implements ClickRaceServices {

    //racenum x racersid_set
    ConcurrentHashMap<Integer, Set<RaceParticipant>> racesData=new ConcurrentHashMap<>();
    
    public ClickRaceServicesStub(){
        racesData.put(25, new ConcurrentSkipListSet<>());        
    }
    
    @Override
    public void registerPlayerToRace(int racenum, RaceParticipant rp) throws ServicesException{
        if (!racesData.containsKey(racenum)){
            throw new ServicesException("Race "+racenum+" not registered in the server.");
        }
        else{
            if (racesData.get(racenum).contains(rp)){
                throw new ServicesException("Racer "+rp.getNumber()+" already registered in race "+racenum);
            }
            else{
                racesData.get(racenum).add(rp);
            }
            
        }
        
    }

    @Override
    public Set<RaceParticipant> getRegisteredPlayers(int racenum) throws ServicesException {
        return racesData.get(racenum);
    }
 
    
    @Override
    public void removePlayerFromRace(int racenum, RaceParticipant rp) throws ServicesException {
        if (!racesData.containsKey(racenum)){
            throw new ServicesException("Race "+racenum+" not registered in the server.");
        }
        else{
            if (!racesData.get(racenum).contains(rp)){
                throw new ServicesException("Racer "+rp.getNumber()+" not registered in race "+racenum);
            }
            else{
                racesData.get(racenum).remove(rp);
            }            
        }
    }
    
}
