/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.clickrace.controllers;

import edu.eci.arsw.clickrace.model.RaceParticipant;
import edu.eci.arsw.clickrace.services.ClickRaceServices;
import edu.eci.arsw.clickrace.services.ServicesException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author hcadavid
 */
@RestController
@RequestMapping(value = "/races")
public class ClicRaceRESTController {
    

    @Autowired
    ClickRaceServices services;
    
    
    
    @RequestMapping(path = "/{racenum}/participants",method = RequestMethod.GET)
    public ResponseEntity<?> getRaceParticipantsNums(@PathVariable(name = "racenum") String racenum) {
        
        try {
            return new ResponseEntity<>(services.getRegisteredPlayers(Integer.parseInt(racenum)),HttpStatus.ACCEPTED);
        } catch (ServicesException ex) {
            Logger.getLogger(ClicRaceRESTController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(ex.getLocalizedMessage(),HttpStatus.NOT_FOUND);
        } catch (NumberFormatException ex){
            Logger.getLogger(ClicRaceRESTController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("/{racenum}/ must be an integer value.",HttpStatus.BAD_REQUEST);
        }
    }
    

    @RequestMapping(path = "/{racenum}/participants",method = RequestMethod.PUT)
    public ResponseEntity<?> addParticipantNum(@PathVariable(name = "racenum") String racenum,@RequestBody RaceParticipant rp) {
        try {
            services.registerPlayerToRace(Integer.parseInt(racenum), rp);
                    return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (ServicesException ex) {
            Logger.getLogger(ClicRaceRESTController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(ex.getLocalizedMessage(),HttpStatus.BAD_REQUEST);
        } catch (NumberFormatException ex){
            Logger.getLogger(ClicRaceRESTController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("/{racenum}/ must be an integer value.",HttpStatus.BAD_REQUEST);
        }

    }

    

    

}
