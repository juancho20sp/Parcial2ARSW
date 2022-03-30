/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.clickrace.services;

import edu.eci.arsw.clickrace.model.RaceParticipant;
import java.util.Set;

/**
 *
 * @author hcadavid
 */
public interface ClickRaceServices {

    public void registerPlayerToRace(int racenum,RaceParticipant rp) throws ServicesException;
    
    public void removePlayerFromRace(int racenum,RaceParticipant rp) throws ServicesException;
    
    public Set<RaceParticipant> getRegisteredPlayers(int racenum) throws ServicesException;
    
}
